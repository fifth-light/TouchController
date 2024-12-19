use android_logger::Config;
use bytemuck::cast_slice;
use jni::{
    objects::{JByteArray, JClass, JString},
    sys::{jint, jlong},
    JNIEnv,
};
use log::LevelFilter;

use crate::poller::Poller;

#[no_mangle]
pub extern "system" fn Java_top_fifthlight_touchcontroller_platform_android_Transport_init(
    _env: JNIEnv<'_>,
    _class: JClass,
) {
    android_logger::init_once(
        Config::default()
            .with_tag("TouchController")
            .with_max_level(LevelFilter::Info),
    );
}

#[no_mangle]
pub extern "system" fn Java_top_fifthlight_touchcontroller_platform_android_Transport_new(
    mut env: JNIEnv<'_>,
    _class: JClass,
    name: JString,
) -> jlong {
    let Ok(address) = env.get_string(&name) else {
        env.throw_new("java/lang/IllegalArgumentException", "Invalid string")
            .unwrap();
        return -1;
    };
    let Ok(address) = address.to_str() else {
        env.throw_new("java/lang/IllegalArgumentException", "Bad UTF-8 string")
            .unwrap();
        return -1;
    };
    match Poller::new(address) {
        Ok(poller) => Box::into_raw(Box::new(poller)) as jlong,
        Err(err) => {
            env.throw_new("java/io/IOException", err.to_string())
                .unwrap();
            -1
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_top_fifthlight_touchcontroller_platform_android_Transport_receive(
    mut env: JNIEnv<'_>,
    _class: JClass,
    handle: jlong,
    buffer: JByteArray,
) -> jint {
    let poller = unsafe { &mut *(handle as *mut Poller) };

    match poller.receive() {
        Ok(Some(message)) => {
            env.set_byte_array_region(buffer, 0, cast_slice(&message))
                .expect("Failed to set array region");
            message.len() as jint
        }
        Ok(None) => {
            // No data available
            0
        }
        Err(err) => {
            env.throw_new("java/io/IOException", err.to_string())
                .unwrap();
            -1
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_top_fifthlight_touchcontroller_platform_android_Transport_send(
    env: JNIEnv<'_>,
    _class: JClass,
    handle: jlong,
    buffer: JByteArray,
    off: jint,
    len: jint,
) {
    let poller = unsafe { &mut *(handle as *mut Poller) };

    let mut array = vec![0; len as usize];
    if env.get_byte_array_region(buffer, off, &mut array).is_err() {
        return;
    };

    poller.send(cast_slice(&array));
}

#[no_mangle]
pub unsafe extern "system" fn Java_top_fifthlight_touchcontroller_platform_android_Transport_destroy(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
) {
    let poller = unsafe { Box::from_raw(handle as *mut Poller) };
    drop(poller)
}
