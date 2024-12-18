import org.gradle.internal.extensions.stdlib.capitalized

version = "0.0.1"

val targets = mapOf(
    "i686" to "i686-pc-windows-gnullvm",
    "x86_64" to "x86_64-pc-windows-gnullvm",
    "aarch64" to "aarch64-pc-windows-gnullvm",
)

val compileRustTasks = targets.map { (arch, target) ->
    task<Exec>("compileRust${arch.capitalized()}") {
        commandLine("cargo", "build", "--target=$target", "--release")
        inputs.apply {
            files("Cargo.toml", "Cargo.lock")
            dir("src")
            files("../proxy-protocol/Cargo.toml", "../proxy-protocol/Cargo.lock")
            dir("../proxy-protocol/src")
        }
        outputs.file("../target/$target/release/proxy_windows.dll")
    }
}

val compileRustTask = task("compileRust") {
    dependsOn(compileRustTasks)
}

val compileTask = task("compile") {
    dependsOn(compileRustTask)
}

val assembleTask = task<Jar>("assemble") {
    archiveFileName = "TouchController-Proxy-Windows.jar"
    destinationDirectory = layout.buildDirectory.dir("lib")
    targets.values.forEach { target ->
        from("../target/$target/release/proxy_windows.dll") {
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
