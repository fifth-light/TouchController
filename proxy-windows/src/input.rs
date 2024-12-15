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
    Foundation::{BOOL, HINSTANCE, HWND, LPARAM, LRESULT, POINT, RECT, WPARAM},
    Graphics::Gdi::ClientToScreen,
    System::Threading::GetCurrentThreadId,
    UI::{
        Controls::{
            SetWindowFeedbackSetting, FEEDBACK_GESTURE_PRESSANDTAP,
            FEEDBACK_TOUCH_CONTACTVISUALIZATION, FEEDBACK_TOUCH_DOUBLETAP,
            FEEDBACK_TOUCH_PRESSANDHOLD, FEEDBACK_TOUCH_RIGHTTAP, FEEDBACK_TOUCH_TAP,
            FEEDBACK_TYPE,
        },
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
    SetWindowFeedbackSetting,
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
            InitializeError::SetWindowFeedbackSetting => {
                write!(f, "SetWindowFeedbackSetting() failed")
            }
        }
    }
}

impl Error for InitializeError {}

pub fn disable_feedback(handle: HWND, feedback: FEEDBACK_TYPE) -> Result<(), ()> {
    let mut enabled: BOOL = BOOL(0);
    let enabled: *mut BOOL = &mut enabled;
    if unsafe {
        SetWindowFeedbackSetting(
            handle,
            feedback,
            0,
            size_of::<BOOL>() as u32,
            Some(enabled as *mut c_void),
        )
    }
    .0 == 0
    {
        Err(())
    } else {
        Ok(())
    }
}

pub fn init(handle: HWND) -> Result<(), InitializeError> {
    unsafe { RegisterTouchWindow(handle, TWF_WANTPALM) }
        .map_err(InitializeError::RegisterTouchWindow)?;

    for feedback in [
        FEEDBACK_TOUCH_CONTACTVISUALIZATION,
        FEEDBACK_TOUCH_TAP,
        FEEDBACK_TOUCH_DOUBLETAP,
        FEEDBACK_TOUCH_PRESSANDHOLD,
        FEEDBACK_TOUCH_RIGHTTAP,
        FEEDBACK_GESTURE_PRESSANDTAP,
    ] {
        disable_feedback(handle, feedback)
            .map_err(|_| InitializeError::SetWindowFeedbackSetting)?;
    }

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

#[derive(Debug)]
struct PointerState {
    tick: u64,
    next_id: u32,
    id_map: HashMap<u32, (u32, u64)>,
}

impl Default for PointerState {
    fn default() -> Self {
        Self {
            tick: 0,
            next_id: 1,
            id_map: HashMap::new(),
        }
    }
}

static POINTER_STATE: Mutex<Option<PointerState>> = Mutex::new(None);

fn handle_touch_event(message: &CWPSTRUCT) -> Result<(), EventError> {
    let pointers_count = low_word(message.wParam.0);
    let pointers_handle = message.lParam.0;

    let mut pointers = vec![TOUCHINPUT::default(); pointers_count.min(16)];

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
    let pointer_state = pointers_state.get_or_insert_with(PointerState::default);
    let PointerState {
        tick,
        next_id,
        id_map,
    } = pointer_state;
    *tick += 1;

    for pointer in pointers {
        let id = match id_map.entry(pointer.dwID) {
            Entry::Occupied(mut entry) => {
                let (id, pointer_tick) = entry.get_mut();
                *pointer_tick = *tick;
                *id
            }
            Entry::Vacant(entry) => {
                let id = *next_id;
                entry.insert((id, *tick));
                *next_id += 1;
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
            id_map.remove(&pointer.dwID);
            queue.push_back(ProxyMessage::Remove { index: id });
        }
    }

    id_map.retain(|_, (id, pointer_tick)| {
        if *pointer_tick == *tick {
            true
        } else {
            queue.push_back(ProxyMessage::Remove { index: *id });
            false
        }
    });

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
