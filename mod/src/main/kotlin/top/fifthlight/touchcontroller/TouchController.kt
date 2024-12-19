package top.fifthlight.touchcontroller

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory
import top.fifthlight.touchcontroller.config.TouchControllerConfigHolder
import top.fifthlight.touchcontroller.di.appModule
import top.fifthlight.touchcontroller.event.ClientRenderEvents
import top.fifthlight.touchcontroller.event.KeyboardInputEvents
import top.fifthlight.touchcontroller.platform.PlatformHolder
import top.fifthlight.touchcontroller.platform.PlatformProvider
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback as FabricHudRenderCallback
import top.fifthlight.touchcontroller.event.HudRenderCallback as TouchControllerHudRenderCallback

object TouchController : ClientModInitializer {
    private val logger = LoggerFactory.getLogger(TouchController::class.java)

    const val NAMESPACE = "touchcontroller"

    override fun onInitializeClient() {
        logger.info("Loading TouchController…")

        val platform = PlatformProvider.platform
        runBlocking {
            @OptIn(DelicateCoroutinesApi::class)
            platform?.init(GlobalScope)
        }
        val platformModule = module {
            single { PlatformHolder(platform) }
        }

        val app = startKoin {
            slf4jLogger()
            modules(platformModule, appModule)
        }
        app.koin.initialize()
    }

    private fun Koin.initialize() {
        get<TouchControllerConfigHolder>().load()
        FabricHudRenderCallback.EVENT.register(get())
        TouchControllerHudRenderCallback.CROSSHAIR.register(get())
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register(get())
        WorldRenderEvents.START.register(get())
        KeyboardInputEvents.END_INPUT_TICK.register(get())
        ClientRenderEvents.START_TICK.register(get())
        ClientPlayConnectionEvents.JOIN.register(get())
        ClientLifecycleEvents.CLIENT_STARTED.register(get())
        ClientPlayerBlockBreakEvents.AFTER.register(get())
    }
}
