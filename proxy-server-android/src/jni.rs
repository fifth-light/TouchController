use jni::{
    objects::{JClass, JObject, JString},
    sys::jlong,
    JNIEnv,
};

use crate::transport::UnixSocketTransport;

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
    match UnixSocketTransport::new(address) {
        Ok(transport) => Box::into_raw(Box::new(transport)) as jlong,
        Err(err) => {
            env.throw_new("java/io/IOException", err.to_string())
                .unwrap();
            -1
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_top_fifthlight_touchcontroller_platform_android_Transport_receive<
    'a,
>(
    mut env: JNIEnv<'a>,
    _class: JClass,
    handle: jlong,
) -> JObject<'a> {
    let transport = unsafe { &mut *(handle as *mut UnixSocketTransport) };

    match transport.receive() {
        Ok(Some(buffer)) => {
            let byte_array = env.byte_array_from_slice(&buffer).unwrap();
            JObject::from(byte_array)
        }
        Ok(None) => {
            // No data available
            JObject::null()
        }
        Err(err) => {
            env.throw_new("java/io/IOException", err.to_string())
                .unwrap();
            JObject::null()
        }
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_top_fifthlight_touchcontroller_platform_android_Transport_destroy(
    _env: JNIEnv,
    _class: JClass,
    handle: jlong,
) {
    let boxed = unsafe { Box::from_raw(handle as *mut UnixSocketTransport) };
    drop(boxed)
}
