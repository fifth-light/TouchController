package top.fifthlight.touchcontroller.layout

import net.minecraft.client.MinecraftClient

object InventoryActionProviderImpl : InventoryActionProvider {
    private val client = MinecraftClient.getInstance()

    override fun hasPlayer(): Boolean = client.player != null
    override fun currentSelectedSlot(): Int = client.player?.inventory?.selectedSlot ?: -1
}