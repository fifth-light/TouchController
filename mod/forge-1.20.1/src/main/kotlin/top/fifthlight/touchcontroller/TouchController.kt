package top.fifthlight.touchcontroller

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraftforge.client.event.ClientPlayerNetworkEvent
import net.minecraftforge.client.event.RenderGuiEvent
import net.minecraftforge.client.event.RenderHighlightEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.level.BlockEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
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
import top.fifthlight.touchcontroller.ui.screen.config.getConfigScreen

@Mod(BuildInfo.MOD_ID)
class TouchController : KoinComponent {
    private val logger = LoggerFactory.getLogger(TouchController::class.java)

    init {
        FMLJavaModLoadingContext.get().modEventBus.addListener(::onClientSetup)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onClientSetup(event: FMLClientSetupEvent) {
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

        val client = Minecraft.getInstance()
        // MUST RUN ON RENDER THREAD
        // Because Forge load mods in parallel, mods don't load on main render thread,
        // which is ok for most cases, but RegisterTouchWindow() and other Win32 API
        // requires caller on the thread created window. We post an event to render
        // thread here, to solve this problem.
        client.tell {
            WindowCreateEvents.onPlatformWindowCreated(PlatformWindowImpl(client.window))
        }
    }

    private fun initialize() {
        val configHolder: GlobalConfigHolder = get()
        configHolder.load()

        MinecraftForge.registerConfigScreen { parent ->
            getConfigScreen(parent) as Screen
        }

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
                if (!controllerHudModel.result.showBlockOutline) {
                    event.isCanceled = true
                }
            }

            @SubscribeEvent
            fun blockBroken(event: BlockEvent.BreakEvent) {
                BlockBreakEvents.afterBlockBreak()
            }

            @SubscribeEvent
            fun worldRender(event: TickEvent.RenderTickEvent) {
                RenderEvents.onRenderStart()
            }

            @SubscribeEvent
            fun joinWorld(event: ClientPlayerNetworkEvent.LoggingIn) {
                ConnectionEvents.onJoinedWorld()
            }
        })
    }
}
