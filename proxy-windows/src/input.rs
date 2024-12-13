use std::{
    collections::VecDeque,
    error::Error,
    fmt::{self, Display, Formatter},
    ptr::null_mut,
    sync::Mutex,
};

use proxy_protocol::ProxyMessage;
use windows::Win32::{
    Foundation::{HINSTANCE, HWND, LPARAM, LRESULT, WPARAM},
    System::Threading::GetCurrentThreadId,
    UI::{
        Input::Touch::{RegisterTouchWindow, TWF_WANTPALM},
        WindowsAndMessaging::{
            CallNextHookEx, SetWindowsHookExW, CWPSTRUCT, HHOOK, WH_CALLWNDPROC, WM_TOUCH,
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

unsafe extern "system" fn event_hook(ncode: i32, wparam: WPARAM, lparam: LPARAM) -> LRESULT {
    let message_struct = unsafe { &*(lparam.0 as *const CWPSTRUCT) };
    if message_struct.message == WM_TOUCH {
        eprintln!(
            "Event hook: lparam: {} wparam: {}",
            message_struct.lParam.0, message_struct.wParam.0
        );
    }

    CallNextHookEx(HHOOK(null_mut()), ncode, wparam, lparam)
}
