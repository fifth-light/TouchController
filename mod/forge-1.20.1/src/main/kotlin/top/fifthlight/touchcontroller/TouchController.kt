package top.fifthlight.touchcontroller

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.ClientPlayerNetworkEvent
import net.minecraftforge.client.event.RenderGuiEvent
import net.minecraftforge.client.event.RenderHighlightEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent.RenderTickEvent
import net.minecraftforge.event.level.BlockEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import top.fifthlight.combine.platform.CanvasImpl
import top.fifthlight.touchcontroller.config.TouchControllerConfigHolder
import top.fifthlight.touchcontroller.di.appModule
import top.fifthlight.touchcontroller.event.BlockBreakEvents
import top.fifthlight.touchcontroller.event.ConnectionEvents
import top.fifthlight.touchcontroller.event.RenderEvents
import top.fifthlight.touchcontroller.event.WindowCreateEvents
import top.fifthlight.touchcontroller.gal.PlatformWindowImpl
import top.fifthlight.touchcontroller.model.ControllerHudModel
import top.fifthlight.touchcontroller.platform.PlatformHolder
import top.fifthlight.touchcontroller.platform.PlatformProvider

@Mod("touchcontroller")
object TouchController : KoinComponent {
    private val logger = LoggerFactory.getLogger(TouchController::class.java)

    init {
        MOD_BUS.addListener(::onClientSetup)
    }

    @Suppress("unused", "UNUSED_PARAMETER")
    private fun onClientSetup(event: FMLClientSetupEvent) {
        logger.info("Loading TouchController…")

        val platform = PlatformProvider.platform
        runBlocking {
            @OptIn(DelicateCoroutinesApi::class)
            platform?.init(GlobalScope)
        }
        val platformHolderModule = module {
            single { PlatformHolder(platform) }
        }

        startKoin {
            slf4jLogger()
            modules(
                platformHolderModule,
                platformModule,
                appModule,
            )
        }

        initialize()

        val client = Minecraft.getInstance()
        WindowCreateEvents.onPlatformWindowCreated(PlatformWindowImpl(client.window))
    }

    private fun initialize() {
        val configHolder: TouchControllerConfigHolder = get()
        configHolder.load()

        val controllerHudModel: ControllerHudModel = get()
        MinecraftForge.EVENT_BUS.register(object {
            @SubscribeEvent
            fun hudRender(event: RenderGuiEvent.Post) {
                val client = Minecraft.getInstance()
                val canvas = CanvasImpl(event.guiGraphics, client.font)
                RenderEvents.onHudRender(canvas)
            }

            @SubscribeEvent
            fun blockOutlineEvent(event: RenderHighlightEvent.Block) {
                if (controllerHudModel.result.crosshairStatus == null) {
                    event.isCanceled = true
                }
            }

            @SubscribeEvent
            fun blockBroken(event: BlockEvent.BreakEvent) {
                BlockBreakEvents.afterBlockBreak()
            }

            @SubscribeEvent
            fun worldRender(event: RenderTickEvent) {
                RenderEvents.onRenderStart()
            }

            @SubscribeEvent
            fun joinWorld(event: ClientPlayerNetworkEvent.LoggingIn) {
                ConnectionEvents.onJoinedWorld()
            }
        })
    }
}
