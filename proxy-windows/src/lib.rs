use std::{ffi, io::Cursor};

use bytemuck::cast_slice;
use input::{init, EVENT_QUEUE};
use jni::{
    objects::{JByteArray, JClass},
    sys::jint,
    JNIEnv,
};
use proxy_protocol::binrw::BinWrite;
use windows::Win32::Foundation::HWND;

mod input;

#[no_mangle]
pub extern "system" fn Java_top_fifthlight_touchcontroller_platform_win32_Interface_init(
    mut env: JNIEnv<'_>,
    _class: JClass,
    window_handle: u64,
) {
    let window_handle = HWND(window_handle as *mut ffi::c_void);
    if let Err(err) = init(window_handle) {
        env.throw_new("java/lang/Exception", err.to_string())
            .expect("Failed to throw exception");
    }
}

#[no_mangle]
pub extern "system" fn Java_top_fifthlight_touchcontroller_platform_win32_Interface_pollEvent(
    env: JNIEnv<'_>,
    _class: JClass,
    buffer: JByteArray,
) -> jint {
    let mut queue = EVENT_QUEUE.lock().unwrap();
    let message = queue.pop_front();
    drop(queue);

    if let Some(message) = message {
        eprintln!("Message: {:?}", message);
        let buffer_length: usize = env
            .get_array_length(&buffer)
            .expect("Failed to get buffer length")
            .try_into()
            .expect("Bad buffer length");

        let msg_buffer: Vec<u8> = Vec::with_capacity(128);
        let mut cursor = Cursor::new(msg_buffer);
        message.write_be(&mut cursor).unwrap();
        let msg_buffer = cursor.into_inner();

        if msg_buffer.len() > buffer_length {
            panic!(
                "Buffer overflow: message length: {}, buffer length: {}",
                msg_buffer.len(),
                buffer_length
            );
        }
        env.set_byte_array_region(buffer, 0, cast_slice(msg_buffer.as_slice()))
            .expect("Failed to set array region");
        msg_buffer.len() as jint
    } else {
        0
    }
}
