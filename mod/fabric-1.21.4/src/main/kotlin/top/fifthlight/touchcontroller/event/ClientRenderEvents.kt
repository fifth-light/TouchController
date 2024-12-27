package top.fifthlight.touchcontroller.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.MinecraftClient
import top.fifthlight.touchcontroller.event.ClientRenderEvents.EndRenderTick
import top.fifthlight.touchcontroller.event.ClientRenderEvents.StartRenderTick

object ClientRenderEvents {
    val START_TICK: Event<StartRenderTick> = EventFactory.createArrayBacked(
        StartRenderTick::class.java
    ) { listeners: Array<StartRenderTick> ->
        StartRenderTick { client, tick ->
            for (event in listeners) {
                event.onStartTick(client, tick)
            }
        }
    }

    val END_TICK: Event<EndRenderTick> = EventFactory.createArrayBacked(
        EndRenderTick::class.java
    ) { listeners: Array<EndRenderTick> ->
        EndRenderTick { client, tick ->
            for (event in listeners) {
                event.onEndTick(client, tick)
            }
        }
    }

    fun interface StartRenderTick {
        fun onStartTick(client: MinecraftClient, tick: Boolean)
    }

    fun interface EndRenderTick {
        fun onEndTick(client: MinecraftClient, tick: Boolean)
    }
}