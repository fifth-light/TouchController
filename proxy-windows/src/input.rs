use std::{
    collections::{hash_map::Entry, HashMap, VecDeque},
    error::Error,
    fmt::{self, Display, Formatter},
    os::raw::c_void,
    ptr::null_mut,
    sync::Mutex,
};

use proxy_protocol::ProxyMessage;
use windows::Win32::{
    Foundation::{HINSTANCE, HWND, LPARAM, LRESULT, POINT, RECT, WPARAM},
    Graphics::Gdi::ClientToScreen,
    System::Threading::GetCurrentThreadId,
    UI::{
        Input::Touch::{
            GetTouchInputInfo, RegisterTouchWindow, HTOUCHINPUT, TOUCHEVENTF_DOWN,
            TOUCHEVENTF_MOVE, TOUCHEVENTF_UP, TOUCHINPUT, TWF_WANTPALM,
        },
        WindowsAndMessaging::{
            CallNextHookEx, GetClientRect, SetWindowsHookExW, CWPSTRUCT, HHOOK, WH_CALLWNDPROC,
            WM_TOUCH,
        },
    },
};

pub static EVENT_QUEUE: Mutex<VecDeque<ProxyMessage>> = Mutex::new(VecDeque::new());

#[derive(Debug)]
pub enum InitializeError {
    RegisterTouchWindow(windows::core::Error),
    SetWindowsHookExW(windows::core::Error),
}

impl Display for InitializeError {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        match self {
            InitializeError::RegisterTouchWindow(error) => {
                write!(f, "RegisterTouchWindow() failed: {:?}", error)
            }
            InitializeError::SetWindowsHookExW(error) => {
                write!(f, "SetWindowsHookExW() failed: {:?}", error)
            }
        }
    }
}

impl Error for InitializeError {}

pub fn init(handle: HWND) -> Result<(), InitializeError> {
    unsafe { RegisterTouchWindow(handle, TWF_WANTPALM) }
        .map_err(InitializeError::RegisterTouchWindow)?;

    let thread_id = unsafe { GetCurrentThreadId() };
    unsafe {
        SetWindowsHookExW(
            WH_CALLWNDPROC,
            Some(event_hook),
            HINSTANCE(null_mut()),
            thread_id,
        )
    }
    .map_err(InitializeError::SetWindowsHookExW)?;

    Ok(())
}

fn low_word(l: usize) -> usize {
    l & 0xffff
}

#[derive(Debug)]
pub enum EventError {
    GetTouchInputInfo(windows::core::Error),
    GetClientRect(windows::core::Error),
    ClientToScreen,
}

impl Display for EventError {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        match self {
            EventError::GetTouchInputInfo(error) => {
                write!(f, "GetTouchInputInfo() failed: {:?}", error)
            }
            EventError::GetClientRect(error) => {
                write!(f, "GetClientRect() failed: {:?}", error)
            }
            EventError::ClientToScreen => {
                write!(f, "ClientToScreen() failed")
            }
        }
    }
}

impl Error for EventError {}

static POINTER_STATE: Mutex<Option<(HashMap<u32, u32>, u32)>> = Mutex::new(None);

fn handle_touch_event(message: &CWPSTRUCT) -> Result<(), EventError> {
    let pointers_count = low_word(message.wParam.0);
    let pointers_handle = message.lParam.0;

    let mut pointers = vec![TOUCHINPUT::default(); pointers_count];

    unsafe {
        GetTouchInputInfo(
            HTOUCHINPUT(pointers_handle as *mut c_void),
            &mut pointers,
            size_of::<TOUCHINPUT>() as i32,
        )
    }
    .map_err(EventError::GetTouchInputInfo)?;

    let mut client_rect = RECT::default();
    unsafe { GetClientRect(message.hwnd, &mut client_rect) }.map_err(EventError::GetClientRect)?;
    let mut point = POINT::default();
    if unsafe { ClientToScreen(message.hwnd, &mut point) }.0 == 0 {
        return Err(EventError::ClientToScreen);
    }

    let scaled_left = point.x * 100;
    let scaled_top = point.y * 100;
    let scaled_width = (client_rect.right - client_rect.left) as f32 * 100.0;
    let scaled_height = (client_rect.bottom - client_rect.top) as f32 * 100.0;

    let mut queue = EVENT_QUEUE.lock().unwrap();
    let mut pointers_state = POINTER_STATE.lock().unwrap();
    let pointer_state = pointers_state.get_or_insert_with(|| (HashMap::new(), 1));
    let (pointers_map, next_pointer) = pointer_state;
    for pointer in pointers {
        let id = match pointers_map.entry(pointer.dwID) {
            Entry::Occupied(entry) => *entry.get(),
            Entry::Vacant(entry) => {
                let id = *next_pointer;
                entry.insert(id);
                *next_pointer += 1;
                id
            }
        };

        let x = (pointer.x - scaled_left) as f32 / scaled_width;
        let y = (pointer.y - scaled_top) as f32 / scaled_height;
        if pointer.dwFlags.0 & TOUCHEVENTF_DOWN.0 != 0
            || pointer.dwFlags.0 & TOUCHEVENTF_MOVE.0 != 0
        {
            queue.push_back(ProxyMessage::Add {
                index: id,
                position: (x, y),
            });
        }
        if pointer.dwFlags.0 & TOUCHEVENTF_UP.0 != 0 {
            pointers_map.remove(&pointer.dwID);
            queue.push_back(ProxyMessage::Remove { index: id });
        }
    }

    Ok(())
}

unsafe extern "system" fn event_hook(ncode: i32, wparam: WPARAM, lparam: LPARAM) -> LRESULT {
    let message_struct = unsafe { &*(lparam.0 as *const CWPSTRUCT) };
    if message_struct.message == WM_TOUCH {
        if let Err(err) = handle_touch_event(message_struct) {
            eprintln!("Handle touch event failed: {}", err);
        }
    }

    CallNextHookEx(HHOOK(null_mut()), ncode, wparam, lparam)
}
