package top.fifthlight.touchcontroller.ext

import net.minecraft.client.gui.widget.ClickableWidget
import top.fifthlight.data.IntSize

fun ClickableWidget.setDimensions(size: IntSize) = setDimensions(size.width, size.height)