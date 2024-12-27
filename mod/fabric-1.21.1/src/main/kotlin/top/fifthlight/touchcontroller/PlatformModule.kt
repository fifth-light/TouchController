package top.fifthlight.touchcontroller

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BeforeBlockOutline
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents
import net.minecraft.client.MinecraftClient
import org.koin.dsl.binds
import org.koin.dsl.module
import top.fifthlight.combine.data.DataComponentTypeFactory
import top.fifthlight.combine.data.ItemFactory
import top.fifthlight.combine.data.TextFactory
import top.fifthlight.combine.platform.*
import top.fifthlight.combine.screen.ScreenFactory
import top.fifthlight.combine.sound.SoundManager
import top.fifthlight.touchcontroller.config.ConfigDirectoryProvider
import top.fifthlight.touchcontroller.config.GameConfigEditor
import top.fifthlight.touchcontroller.event.*
import top.fifthlight.touchcontroller.handler.*
import top.fifthlight.touchcontroller.layout.*
import top.fifthlight.touchcontroller.platform.NativeLibraryPathGetter
import top.fifthlight.touchcontroller.platform.NativeLibraryPathGetterImpl
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback as FabricHudRenderCallback

val platformModule = module {
    val client = MinecraftClient.getInstance()
    single<SoundManager> { SoundManagerImpl(client.soundManager) }
    single<ItemFactory> { ItemFactoryImpl }
    single<TextFactory> { TextFactoryImpl }
    single<DataComponentTypeFactory> { DataComponentTypeFactoryImpl }
    single<ScreenFactory> { ScreenFactoryImpl }
    single<GameConfigEditor> { GameConfigEditorImpl }
    single<ConfigDirectoryProvider> { ConfigDirectoryProviderImpl }
    single<NativeLibraryPathGetter> { NativeLibraryPathGetterImpl }
    single<CrosshairRenderer> { CrosshairRendererImpl }
    single<InventoryActionProvider> { InventoryActionProviderImpl }
    single<PlayerHandleFactory> { PlayerHandleFactoryImpl }
    single<ViewActionProvider> { ViewActionProviderImpl }

    single<FabricHudRenderCallback> { HudCallbackHandler() }
    single<KeyboardInputEvents.EndInputTick> { KeyboardInputHandler() }
    single { WorldRendererHandler() } binds arrayOf(
        BeforeBlockOutline::class,
        HudRenderCallback.CrosshairRender::class,
        WorldRenderEvents.Start::class
    )
    single<ClientRenderEvents.StartRenderTick> { ClientRenderHandler() }
    single<ClientPlayConnectionEvents.Join> { ClientPlayConnectionHandler() }
    single<ClientLifecycleEvents.ClientStarted> { ClientStartHandler() }
    single<ClientPlayerBlockBreakEvents.After> { ClientPlayerBlockBreakHandler() }
}
