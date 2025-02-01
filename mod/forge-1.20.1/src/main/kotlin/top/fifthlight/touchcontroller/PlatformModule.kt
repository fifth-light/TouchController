package top.fifthlight.touchcontroller

import net.minecraft.client.Minecraft
import org.koin.dsl.module
import top.fifthlight.combine.data.DataComponentTypeFactory
import top.fifthlight.combine.data.ItemFactory
import top.fifthlight.combine.data.TextFactory
import top.fifthlight.combine.platform.*
import top.fifthlight.combine.screen.ScreenFactory
import top.fifthlight.combine.sound.SoundManager
import top.fifthlight.touchcontroller.config.ConfigDirectoryProvider
import top.fifthlight.touchcontroller.config.GameConfigEditor
import top.fifthlight.touchcontroller.gal.*

val platformModule = module {
    val client = Minecraft.getInstance()
    single<SoundManager> { SoundManagerImpl(client.soundManager) }
    single<ItemFactory> { ItemFactoryImpl }
    single<TextFactory> { TextFactoryImpl }
    single<DataComponentTypeFactory> { FoodComponentTypeFactoryImpl }
    single<ScreenFactory> { ScreenFactoryImpl }
    single<GameConfigEditor> { GameConfigEditorImpl }
    single<ConfigDirectoryProvider> { ConfigDirectoryProviderImpl }
    single<NativeLibraryPathGetter> { NativeLibraryPathGetterImpl }
    single<CrosshairRenderer> { CrosshairRendererImpl }
    single<PlayerHandleFactory> { PlayerHandleFactoryImpl }
    single<ViewActionProvider> { ViewActionProviderImpl }
    single<GameAction> { GameActionImpl }
    single<GameFeatures> { gameFeatures }
    single<GameStateProvider> { GameStateProviderImpl }
    single<WindowHandle> { WindowHandleImpl }
    single<DefaultItemListProvider> { DefaultItemListProviderImpl }
    single<KeyBindingHandler> { KeyBindingHandlerImpl }
}
