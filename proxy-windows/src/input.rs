use std::{
    collections::VecDeque,
    error::Error,
    fmt::{self, Display, Formatter},
    os::raw::c_void,
    ptr::null_mut,
    sync::Mutex,
};

use proxy_protocol::ProxyMessage;
use windows::Win32::{
    Foundation::{HINSTANCE, HWND, LPARAM, LRESULT, RECT, WPARAM},
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
    HookWindowEvent(windows::core::Error),
}

impl Display for InitializeError {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        match self {
            InitializeError::RegisterTouchWindow(error) => {
                write!(f, "Failed to register touch window: {:?}", error)
            }
            InitializeError::HookWindowEvent(error) => {
                write!(f, "Failed to hook window event: {:?}", error)
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
    .map_err(InitializeError::HookWindowEvent)?;

    Ok(())
}

fn low_word(l: usize) -> usize {
    l & 0xffff
}

unsafe extern "system" fn event_hook(ncode: i32, wparam: WPARAM, lparam: LPARAM) -> LRESULT {
    let message_struct = unsafe { &*(lparam.0 as *const CWPSTRUCT) };
    if message_struct.message == WM_TOUCH {
        let pointers_count = low_word(message_struct.wParam.0);
        let pointers_handle = message_struct.lParam.0;

        let mut pointers = vec![TOUCHINPUT::default(); pointers_count];

        let touch_result = unsafe {
            GetTouchInputInfo(
                HTOUCHINPUT(pointers_handle as *mut c_void),
                &mut pointers,
                size_of::<TOUCHINPUT>() as i32,
            )
        };
        if let Err(err) = touch_result {
            eprintln!("Call GetTouchInputInfo() failed: {}", err);
        } else {
            let mut client_rect = RECT::default();
            let client_rect_result =
                unsafe { GetClientRect(message_struct.hwnd, &mut client_rect) };
            if let Err(err) = client_rect_result {
                eprintln!("Call GetClientRect() failed: {}", err);
            } else {
                let scaled_left = client_rect.left * 100;
                let scaled_top = client_rect.top * 100;
                let scaled_width = (client_rect.right - client_rect.left) as f32 * 100.0;
                let scaled_height = (client_rect.bottom - client_rect.top) as f32 * 100.0;

                let mut queue = EVENT_QUEUE.lock().unwrap();
                for pointer in pointers {
                    let x = (pointer.x - scaled_left) as f32 / scaled_width;
                    let y = (pointer.y - scaled_top) as f32 / scaled_height;
                    if pointer.dwFlags.0 & TOUCHEVENTF_DOWN.0 != 0
                        || pointer.dwFlags.0 & TOUCHEVENTF_MOVE.0 != 0
                    {
                        queue.push_back(ProxyMessage::Add {
                            index: pointer.dwID,
                            position: (x, y),
                        });
                    }
                    if pointer.dwFlags.0 & TOUCHEVENTF_UP.0 != 0 {
                        queue.push_back(ProxyMessage::Remove {
                            index: pointer.dwID,
                        });
                    }
                    eprintln!("Pointer: x: {}, y: {}, id: {}", x, y, pointer.dwID);
                }
            }
        }
    }

    CallNextHookEx(HHOOK(null_mut()), ncode, wparam, lparam)
}
