package top.fifthlight.combine.platform

import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.sounds.SoundManager
import net.minecraft.sounds.SoundEvents
import top.fifthlight.combine.sound.SoundKind
import top.fifthlight.combine.sound.SoundManager as CombineSoundManager

class SoundManagerImpl(
    private val soundManager: SoundManager
) : CombineSoundManager {
    override fun play(kind: SoundKind, pitch: Float) {
        val soundEvent = when (kind) {
            SoundKind.BUTTON_PRESS -> SoundEvents.UI_BUTTON_CLICK
        }
        soundManager.play(SimpleSoundInstance.forUI(soundEvent, pitch))
    }
}