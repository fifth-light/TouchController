package top.fifthlight.combine.platform

import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvents
import top.fifthlight.combine.sound.SoundKind
import top.fifthlight.combine.sound.SoundManager

class SoundManagerImpl(
    private val soundManager: net.minecraft.client.sound.SoundManager
) : SoundManager {
    override fun play(kind: SoundKind, pitch: Float) {
        val soundEvent = when (kind) {
            SoundKind.BUTTON_PRESS -> SoundEvents.UI_BUTTON_CLICK
        }
        soundManager.play(PositionedSoundInstance.master(soundEvent, pitch))
    }
}