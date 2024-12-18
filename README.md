# TouchController

一个为 Minecraft Java 版添加触控支持的 Mod。目前处于早期开发中，如果遇到 Bug 或者其他问题，欢迎积极报告！

## 支持的平台

由于项目处在早期开发阶段，目前只支持 Minecraft 1.21.3。

TouchController 的平台输入代码和实际的输入处理代码部分是相互隔离开的，目前支持在 Windows 和[我修改后的 PojavLauncher](https://github.com/fifth-light/PojavLauncher) 和[我修改后的 FoldCraftLauncher](https://github.com/fifth-light/FoldCraftLauncher) 上使用。在未来可能会添加对 Linux 上触屏的支持。

## 目前支持的功能

- Minecraft 基岩版风格的触屏输入（不支持分离控制）
- 可自定义的控制器布局
- 能够根据游泳、飞行等状态切换不同按键的显示

## 编译

首先你需要 Rust 编译器，可以使用 [rustup](https://rustup.rs/) 安装。

接下来你需要用 `rustup target add <目标>` 的方式安装以下目标的 Rust 工具链：

- i686-pc-windows-gnullvm
- x86_64-pc-windows-gnullvm
- aarch64-pc-windows-gnullvm

然后你还需要 LLVM MinGW，在 [mstorsjo/llvm-mingw](https://github.com/mstorsjo/llvm-mingw/releases) 获取工具链，并将其中的
bin 目录加入 PATH 环境变量即可。

最后运行 `./gradlew build` 就可以编译了，编译好的 mod 文件在 `mod/build/libs` 下。

## 添加新的启动器支持

欢迎添加其他启动器的支持！为其他启动器添加支持的步骤有：

- 添加 TouchController 的 proxy-client 库到启动器内
- 选定一个 UDP 端口，并在启动时作为 `TOUCH_CONTROLLER_PROXY` 环境变量传送到游戏中
- 使用 proxy-client 库中的 `localhostLauncherSocketProxyClient` 方法，传入上一步选定的 UDP 端口，构建一个 `LauncherSocketProxyClient` 对象，即可使用 `send`（如果你的启动器使用 Kotlin）或者使用 `trySend`（如果你的启动器使用 Java）方法向游戏发送触控消息。

触控消息分为以下三种：

- AddPointerMessage：添加或者更新一个触点
- ClearPointerMessage：清除所有的触点
- RemovePointerMessage：删除一个触点

要注意的是消息中的 index 必须是单调递增的（与 Android 中可以复用 ID 的行为相反），并且 offset 的范围是相对于游戏显示区域的 [0.0, 1.0]，而不是屏幕坐标。
