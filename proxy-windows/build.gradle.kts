import org.gradle.internal.extensions.stdlib.capitalized

version = "0.0.1"

val targets = mapOf(
    "i686" to "i686-pc-windows-gnu",
    "x86_64" to "x86_64-pc-windows-gnu",
)

val compileRustTasks = targets.map { (arch, target) ->
    task<Exec>("compileRust${arch.capitalized()}") {
        commandLine("cargo", "build", "--target=$target", "--release")
        inputs.dir("src")
        outputs.file("target/$target/release/proxy_windows.dll")
    }
}

val compileRustTask = task("compileRust") {
    dependsOn(compileRustTasks)
}

val compileTask = task("compile") {
    dependsOn(compileRustTask)
}

val assembleTask = task("assemble") {
    dependsOn(compileTask)
}

task("build") {
    dependsOn(assembleTask)
}