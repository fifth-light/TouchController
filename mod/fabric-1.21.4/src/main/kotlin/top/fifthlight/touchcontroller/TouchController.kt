package top.fifthlight.touchcontroller

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents
import net.minecraft.client.MinecraftClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory
import top.fifthlight.combine.platform.CanvasImpl
import top.fifthlight.touchcontroller.config.GlobalConfigHolder
import top.fifthlight.touchcontroller.di.appModule
import top.fifthlight.touchcontroller.event.BlockBreakEvents
import top.fifthlight.touchcontroller.event.ConnectionEvents
import top.fifthlight.touchcontroller.event.RenderEvents
import top.fifthlight.touchcontroller.event.WindowCreateEvents
import top.fifthlight.touchcontroller.gal.PlatformWindowImpl
import top.fifthlight.touchcontroller.model.ControllerHudModel
import top.fifthlight.touchcontroller.platform.PlatformHolder
import top.fifthlight.touchcontroller.platform.PlatformProvider
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback as FabricHudRenderCallback

object TouchController : ClientModInitializer, KoinComponent {
    private val logger = LoggerFactory.getLogger(TouchController::class.java)

    override fun onInitializeClient() {
        logger.info("Loading TouchController…")

        val platformHolder = PlatformHolder(null)
        val platformHolderModule = module {
            single { platformHolder }
        }

        startKoin {
            slf4jLogger()
            modules(
                platformHolderModule,
                platformModule,
                appModule,
            )
        }

        PlatformProvider.platform?.let { platform ->
            runBlocking {
                @OptIn(DelicateCoroutinesApi::class)
                platform.init(GlobalScope)
            }
            platformHolder.platform = platform
        }

        initialize()
    }

    private fun initialize() {
        val configHolder: GlobalConfigHolder = get()
        configHolder.load()

        FabricHudRenderCallback.EVENT.register { drawContext, _ ->
            val client = MinecraftClient.getInstance()
            val canvas = CanvasImpl(drawContext, client.textRenderer)
            RenderEvents.onHudRender(canvas)
        }

        val controllerHudModel: ControllerHudModel = get()
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register { _, _ ->
            controllerHudModel.result.crosshairStatus != null
        }
        WorldRenderEvents.START.register {
            RenderEvents.onRenderStart()
        }
        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            ConnectionEvents.onJoinedWorld()
        }
        ClientLifecycleEvents.CLIENT_STARTED.register {
            val client = MinecraftClient.getInstance()
            WindowCreateEvents.onPlatformWindowCreated(PlatformWindowImpl(client.window))
        }
        ClientPlayerBlockBreakEvents.AFTER.register { _, _, _, _ ->
            BlockBreakEvents.afterBlockBreak()
        }
    }
}
