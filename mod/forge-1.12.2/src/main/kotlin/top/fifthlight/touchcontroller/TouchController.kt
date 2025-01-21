package top.fifthlight.touchcontroller

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.slf4j.LoggerFactory
import top.fifthlight.combine.platform.CanvasImpl
import top.fifthlight.touchcontroller.config.GlobalConfigHolder
import top.fifthlight.touchcontroller.di.appModule
import top.fifthlight.touchcontroller.event.BlockBreakEvents
import top.fifthlight.touchcontroller.event.ConnectionEvents
import top.fifthlight.touchcontroller.event.RenderEvents
import top.fifthlight.touchcontroller.event.WindowCreateEvents
import top.fifthlight.touchcontroller.gal.PlatformWindowImpl
import top.fifthlight.touchcontroller.platform.PlatformHolder
import top.fifthlight.touchcontroller.platform.PlatformProvider

@Mod(
    modid = BuildInfo.MOD_ID,
    name = BuildInfo.MOD_NAME,
    version = BuildInfo.MOD_VERSION,
    clientSideOnly = true,
    acceptedMinecraftVersions = "1.12.2",
    acceptableRemoteVersions = "*",
    canBeDeactivated = false,
    guiFactory = "top.fifthlight.touchcontroller.ForgeGuiFactoryImpl"
)
class TouchController : KoinComponent {
    private val logger = LoggerFactory.getLogger(TouchController::class.java)

    @Mod.EventHandler
    fun onClientSetup(event: FMLInitializationEvent) {
        logger.info("Loading TouchController…")

        val platformHolder = PlatformHolder(null)
        val platformHolderModule = module {
            single { platformHolder }
        }

        startKoin {
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

        val client = Minecraft.getMinecraft()
        // MUST RUN ON RENDER THREAD
        // Because Forge load mods in parallel, mods don't load on main render thread,
        // which is ok for most cases, but RegisterTouchWindow() and other Win32 API
        // requires caller on the thread created window. We post an event to render
        // thread here, to solve this problem.
        client.addScheduledTask {
            WindowCreateEvents.onPlatformWindowCreated(PlatformWindowImpl)
        }
    }

    private fun initialize() {
        val configHolder: GlobalConfigHolder = get()
        configHolder.load()

        MinecraftForge.EVENT_BUS.register(object {
            @SubscribeEvent
            fun hudRender(event: RenderGameOverlayEvent.Post) {
                if (event.type == ElementType.ALL) {
                    val client = Minecraft.getMinecraft()
                    val canvas = CanvasImpl(client.fontRenderer)
                    GlStateManager.disableAlpha()
                    GlStateManager.disableBlend()
                    GlStateManager.disableLighting()
                    RenderEvents.onHudRender(canvas)
                    GlStateManager.enableAlpha()
                    GlStateManager.enableBlend()
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
            fun joinWorld(event: PlayerEvent.PlayerLoggedInEvent) {
                ConnectionEvents.onJoinedWorld()
            }
        })
    }
}
