# TouchController

一个为 Minecraft Java 版添加触控支持的 Mod。目前处于早期开发中，如果遇到 Bug 或者其他问题，欢迎积极报告！

## 支持的游戏版本和平台

目前 TouchController 只支持 Minecraft 1.21.3，更多游戏版本、mod 加载器的支持正在开发中。

目前支持的平台有：

- Windows（v0.0.8 开始支持 x86 和 x86_64 架构，v0.0.11 添加了 ARM64 架构的支持）
- [官方版 Fold Craft Launcher](https://github.com/FCL-Team/FoldCraftLauncher)
- [官方版 Zalith Launcher](https://github.com/ZalithLauncher/ZalithLauncher)
- [官方版 Pojav Glow·Worm](https://github.com/Vera-Firefly/Pojav-Glow-Worm)
- [我修改后的 PojavLauncher](https://github.com/fifth-light/PojavLauncher)

在未来可能会添加 Linux 上 X11 和 Wayland 触屏的支持。macOS 由于没有相应环境和设备，不会提供支持，但是接受 macOS 支持的 Pull Request。

## 目前支持的功能

- Minecraft 基岩版风格的触屏输入（暂时不支持分离控制）
- 可自定义的控制器布局
- 能够根据游泳、飞行等状态切换不同按键的显示
- 破坏方块时进行震动反馈（目前只支持 Android 平台）

## 编译

首先你需要 Rust 编译器，可以使用 [rustup](https://rustup.rs/) 安装。

接下来你需要安装以下几个目标的 Rust 工具链：

- armv7-linux-androideabi
- aarch64-linux-android
- i686-linux-android
- x86_64-linux-android
- i686-pc-windows-gnullvm
- x86_64-pc-windows-gnullvm
- aarch64-pc-windows-gnullvm

这些工具链可以用 `rustup target add <工具链目标>` 添加。

你还需要一份 Android SDK，可以在 Android Studio 内安装，然后在项目根目录创建 `local.properties`，其中内容如下：

```
sdk.dir=<Android SDK 目录>
```

默认 Android Studio 会帮你做这件事情，如果你用 Android Studio 打开过这个项目，则不需要配置这个选项。

接下来你还需要安装一份 Android NDK，同样也可以在 Android Studio 内安装，安装完后使用 `cargo install cargo-ndk` 安装 `cargo-ndk` 工具。

然后你还需要 LLVM MinGW 工具链，在 [mstorsjo/llvm-mingw](https://github.com/mstorsjo/llvm-mingw/releases) 获取工具链，并将其中的
bin 目录加入 PATH 环境变量即可。

最后运行 `./gradlew build` 就可以编译了，编译好的 mod 文件在 `mod/build/libs` 下。

## 添加新的启动器支持

欢迎添加其他启动器的支持！为其他启动器添加支持的步骤有：

1. 添加 TouchController 的 proxy-client 库到启动器内

- Groovy
```groovy
implementation 'top.fifthlight.touchcontroller:proxy-client-android:0.0.2'
```

- Kotlin
```kotlin
implementation("top.fifthlight.touchcontroller:proxy-client-android:0.0.2")
```

- Gradle version catalogs
```toml
touchcontroller-proxy-client-android = { group = "top.fifthlight.touchcontroller", name = "proxy-client-android", version = "0.0.2" }
```

2. 创建 MessageTransport

目前版本的 TouchController 使用 Unix 套接字进行游戏和启动器之间的 IPC，因此需要先创建一个 UnixSocketTransport：

```java
private static final String socketName = "YourLauncher";

/* ... */

MessageTransport transport = UnixSocketTransportKt.UnixSocketTransport(socketName);
```

3. 创建一个 LauncherProxyClient

有了 MessageTransport 后你就可以创建一个 LauncherProxyClient 了，这是实现启动器和游戏之间交互协议的类：

```java
LauncherProxyClient client = new LauncherProxyClient(transport);
```

4. 创建一个 VibrationHandler（可选）

TouchController 从 v0.0.12 版本开始支持震动反馈。首先你需要实现 VibrationHandler：

```kotlin
interface VibrationHandler {
    fun viberate(kind: VibrateMessage.Kind)
}
```

在 proxy-client-android 库中的 SimpleVibrationHandler 类实现了一个基本的 VibrationHandler，可以作为参考，但是不建议直接使用这个类，因为这个类缺失震动强度、震动效果的调节：

```kotlin
private val TAG = "SimpleVibrationHandler"

class SimpleVibrationHandler(private val service: Vibrator) : LauncherProxyClient.VibrationHandler {
    override fun viberate(kind: VibrateMessage.Kind) {
        try {
            @Suppress("DEPRECATION")
            service.vibrate(100)
        } catch (ex: Exception) {
            Log.w(TAG, "Failed to trigger vibration", ex)
        }
    }
}
```

然后设置 VibrationHandler 到 LauncherProxyClient 中：

```java
SimpleVibrationHandler handler = new SimpleVibrationHandler(vibrator);
client.setVibrationHandler(handler);
```

5. 启动 LauncherProxyClient，并发送消息：

调用 LauncherProxyClient 的 run() 方法，否则 LauncherProxyClient 不会发送任何消息到游戏：

```java
client.run();
```

然后调用 LauncherProxyClient 的以下方法更新触点：

- addPointer：添加或者更新一个触点
- removePointer：清除所有的触点
- clearPointer：删除一个触点

如果不想手动做消息处理，库内也提供了一个基于 FrameLayout 的 TouchControllerLayout 类，只要将游戏相关的 View 包含在内，然后将 LauncherProxyClient 设置到 TouchControllerLayout 中即可发送处理触摸消息并发送。

要注意的是消息中的 index 必须是单调递增的（与 Android 中可以复用 ID 的行为相反），并且所有坐标的范围是相对于游戏显示区域的 [0.0, 1.0]，而不是屏幕坐标。

## Star 历史

<a href="https://star-history.com/#fifth-light/TouchController&Date">
 <picture>
   <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/svg?repos=fifth-light/TouchController&type=Date&theme=dark" />
   <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/svg?repos=fifth-light/TouchController&type=Date" />
   <img alt="Star 历史图表" src="https://api.star-history.com/svg?repos=fifth-light/TouchController&type=Date" />
 </picture>
</a>
