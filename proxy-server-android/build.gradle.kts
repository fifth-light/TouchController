import org.gradle.internal.extensions.stdlib.capitalized
import java.io.FileFilter

version = "0.0.1"

val androidSdkDir by lazy {
    val path = properties.get("sdk.dir")?.toString()?.takeIf { it.isNotEmpty() }
        ?: System.getenv("ANDROID_HOME").takeIf { it.isNotEmpty() }
        ?: System.getenv("ANDROID_SDK").takeIf { it.isNotEmpty() }
        ?: System.getenv("ANDROID_SDK_HOME").takeIf { it.isNotEmpty() }
        ?: error("No android SDK")
    File(path)
}

fun findNdk(sdkDir: File): File? {
    val ndkDir = sdkDir.resolve("ndk")
    val ndkName = ndkDir.listFiles(FileFilter {
        it.isDirectory
    })?.maxOrNull() ?: return null
    return ndkDir.resolve(ndkName)
}

val androidNdkDir by lazy {
    val path = properties.get("ndk.dir")?.toString()?.takeIf { it.isNotEmpty() }
        ?: System.getenv("ANDROID_NDK")
        ?: System.getenv("ANDROID_NDK_HOME")
        ?: findNdk(androidSdkDir)?.toString()
        ?: error("No android NDK")
    File(path)
}

val targets = mapOf(
    "armhf" to "armv7-linux-androideabi",
    "arm64" to "aarch64-linux-android",
    "i686" to "i686-linux-android",
    "x86_64" to "x86_64-linux-android",
)

val compileRustTasks = targets.map { (arch, target) ->
    task<Exec>("compileRust${arch.capitalized()}") {
        commandLine("cargo", "ndk", "--target=$target", "build", "--release")
        environment.apply {
            set("ANDROID_NDK_HOME", androidNdkDir)
        }
        inputs.apply {
            files("Cargo.toml", "Cargo.lock")
            dir("src")
            files("../proxy-protocol/Cargo.toml", "../proxy-protocol/Cargo.lock")
            dir("../proxy-protocol/src")
        }
        outputs.file("../target/$target/release/libproxy_server_android.so")
    }
}

val compileRustTask = task("compileRust") {
    dependsOn(compileRustTasks)
}

val compileTask = task("compile") {
    dependsOn(compileRustTask)
}

val assembleTask = task<Jar>("assemble") {
    archiveFileName = "TouchController-Proxy-Server-Android.jar"
    destinationDirectory = layout.buildDirectory.dir("lib")
    targets.values.forEach { target ->
        from("../target/$target/release/libproxy_server_android.so") {
            into(target)
        }
    }
    dependsOn(compileTask)
}

task("build") {
    dependsOn(assembleTask)
}

configurations {
    register("default")
}

artifacts {
    add("default", assembleTask)
}
