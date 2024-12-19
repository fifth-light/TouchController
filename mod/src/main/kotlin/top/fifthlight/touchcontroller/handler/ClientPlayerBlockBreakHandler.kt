package top.fifthlight.touchcontroller.handler

import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents
import net.minecraft.block.BlockState
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.touchcontroller.model.ControllerHudModel

class ClientPlayerBlockBreakHandler : ClientPlayerBlockBreakEvents.After, KoinComponent {
    private val controllerHudModel: ControllerHudModel by inject()

    override fun afterBlockBreak(
        world: ClientWorld,
        player: ClientPlayerEntity,
        position: BlockPos,
        blockState: BlockState
    ) {
        controllerHudModel.status.vibrate = true
    }
}