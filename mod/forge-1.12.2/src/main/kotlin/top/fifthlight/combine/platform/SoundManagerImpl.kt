package top.fifthlight.combine.platform

import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.client.audio.SoundHandler
import net.minecraft.init.SoundEvents
import top.fifthlight.combine.sound.SoundKind
import top.fifthlight.combine.sound.SoundManager as CombineSoundManager

class SoundManagerImpl(
    private val soundManager: SoundHandler
) : CombineSoundManager {
    override fun play(kind: SoundKind, pitch: Float) {
        val soundEvent = when (kind) {
            SoundKind.BUTTON_PRESS -> SoundEvents.UI_BUTTON_CLICK
        }
        soundManager.playSound(PositionedSoundRecord.getMasterRecord(soundEvent, pitch))
    }
}