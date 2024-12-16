package top.fifthlight.touchcontroller.asset

import net.minecraft.util.Identifier
import top.fifthlight.touchcontroller.TouchController

object Textures {
    private fun texture(id: String): Identifier = Identifier.of(TouchController.NAMESPACE, id)

    val DPAD_UP_CLASSIC = texture("textures/gui/dpad/up_classic.png")
    val DPAD_DOWN_CLASSIC = texture("textures/gui/dpad/down_classic.png")
    val DPAD_LEFT_CLASSIC = texture("textures/gui/dpad/left_classic.png")
    val DPAD_RIGHT_CLASSIC = texture("textures/gui/dpad/right_classic.png")
    val DPAD_UP = texture("textures/gui/dpad/up.png")
    val DPAD_DOWN = texture("textures/gui/dpad/down.png")
    val DPAD_LEFT = texture("textures/gui/dpad/left.png")
    val DPAD_RIGHT = texture("textures/gui/dpad/right.png")
    val DPAD_UP_ACTIVE = texture("textures/gui/dpad/up_active.png")
    val DPAD_DOWN_ACTIVE = texture("textures/gui/dpad/down_active.png")
    val DPAD_LEFT_ACTIVE = texture("textures/gui/dpad/left_active.png")
    val DPAD_RIGHT_ACTIVE = texture("textures/gui/dpad/right_active.png")
    val DPAD_UP_LEFT = texture("textures/gui/dpad/up_left.png")
    val DPAD_UP_LEFT_ACTIVE = texture("textures/gui/dpad/up_left_active.png")
    val DPAD_UP_LEFT_CLASSIC = texture("textures/gui/dpad/up_left_classic.png")
    val DPAD_UP_RIGHT = texture("textures/gui/dpad/up_right.png")
    val DPAD_UP_RIGHT_ACTIVE = texture("textures/gui/dpad/up_right_active.png")
    val DPAD_UP_RIGHT_CLASSIC = texture("textures/gui/dpad/up_right_classic.png")
    val DPAD_DOWN_LEFT = texture("textures/gui/dpad/down_left.png")
    val DPAD_DOWN_LEFT_ACTIVE = texture("textures/gui/dpad/down_left_active.png")
    val DPAD_DOWN_RIGHT = texture("textures/gui/dpad/down_right.png")
    val DPAD_DOWN_RIGHT_ACTIVE = texture("textures/gui/dpad/down_right_active.png")

    val SNEAK_CLASSIC = texture("textures/gui/sneak/sneak_classic.png")
    val SNEAK_CLASSIC_ACTIVE = texture("textures/gui/sneak/sneak_classic_active.png")
    val SNEAK = texture("textures/gui/sneak/sneak.png")
    val SNEAK_ACTIVE = texture("textures/gui/sneak/sneak_active.png")
    val SNEAK_DPAD = texture("textures/gui/sneak/sneak_dpad.png")
    val SNEAK_DPAD_ACTIVE = texture("textures/gui/sneak/sneak_dpad_active.png")

    val JUMP_CLASSIC = texture("textures/gui/jump/jump_classic.png")
    val JUMP = texture("textures/gui/jump/jump.png")
    val JUMP_ACTIVE = texture("textures/gui/jump/jump_active.png")
    val JUMP_FLYING = texture("textures/gui/jump/jump_flying.png")

    val JOYSTICK_PAD = texture("textures/gui/joystick/pad.png")
    val JOYSTICK_STICK = texture("textures/gui/joystick/stick.png")

    val PAUSE = texture("textures/gui/pause/pause.png")
    val PAUSE_CLASSIC = texture("textures/gui/pause/pause_classic.png")

    val CHAT = texture("textures/gui/chat/chat.png")
    val CHAT_CLASSIC = texture("textures/gui/chat/chat_classic.png")

    val ASCEND_CLASSIC = texture("textures/gui/ascend/ascend_classic.png")
    val WATER_ASCEND = texture("textures/gui/ascend/waterascend.png")
    val WATER_ASCEND_ACTIVE = texture("textures/gui/ascend/waterascend_active.png")
    val FLYING_ASCEND = texture("textures/gui/ascend/flyingascend.png")
    val FLYING_ASCEND_ACTIVE = texture("textures/gui/ascend/flyingascend_active.png")

    val DESCEND_CLASSIC = texture("textures/gui/descend/descend_classic.png")
    val WATER_DESCEND = texture("textures/gui/descend/waterdescend.png")
    val WATER_DESCEND_ACTIVE = texture("textures/gui/descend/waterdescend_active.png")
    val FLYING_DESCEND = texture("textures/gui/descend/flyingdescend.png")
    val FLYING_DESCEND_ACTIVE = texture("textures/gui/descend/flyingdescend_active.png")

    val INVENTORY = texture("textures/gui/inventory/inventory.png")
    val INVENTORY_ACTIVE = texture("textures/gui/inventory/inventory_active.png")

}