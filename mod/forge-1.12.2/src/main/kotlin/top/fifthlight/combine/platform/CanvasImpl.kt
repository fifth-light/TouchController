package top.fifthlight.combine.platform

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import top.fifthlight.combine.data.ItemStack
import top.fifthlight.combine.data.Texture
import top.fifthlight.combine.paint.*
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntRect
import top.fifthlight.data.IntSize
import top.fifthlight.data.Rect
import top.fifthlight.combine.data.Text as CombineText

class CanvasImpl(
    val fontRenderer: FontRenderer,
) : Canvas, Gui() {
    private val client = Minecraft.getMinecraft()
    private val scaledResolution by lazy { ScaledResolution(client) }
    private val itemRenderer = client.renderItem
    override val textMeasurer: TextMeasurer = TextMeasurerImpl(fontRenderer)

    override fun pushState() {
        GlStateManager.pushMatrix()
    }

    override fun popState() {
        GlStateManager.popMatrix()
    }

    override fun translate(x: Int, y: Int) {
        GlStateManager.translate(x.toDouble(), y.toDouble(), 0.0)
    }

    override fun translate(x: Float, y: Float) {
        GlStateManager.translate(x.toDouble(), y.toDouble(), 0.0)
    }

    override fun rotate(degrees: Float) {
        GlStateManager.rotate(degrees, 0f, 0f, 1f)
    }

    override fun scale(x: Float, y: Float) {
        GlStateManager.scale(x, y, 1f)
    }

    override fun fillRect(offset: IntOffset, size: IntSize, color: Color) {
        drawRect(offset.x, offset.y, offset.x + size.width, offset.y + size.height, color.value)
    }

    override fun drawRect(offset: IntOffset, size: IntSize, color: Color) {
        //  1 -> 2  |
        //  |    |  |
        //  4 -> 3 \|/

        val strokeSize = 1

        // 1 to 2
        fillRect(
            offset = offset,
            size = IntSize(
                width = size.width - strokeSize,
                height = strokeSize,
            ),
            color = color,
        )

        // 2 to 3
        fillRect(
            offset = IntOffset(
                x = offset.x + size.width - strokeSize,
                y = offset.y,
            ),
            size = IntSize(
                width = strokeSize,
                height = size.height - strokeSize,
            ),
            color = color,
        )

        // 4 to 3
        fillRect(
            offset = IntOffset(
                x = offset.x + strokeSize,
                y = offset.y + size.height - strokeSize,
            ),
            size = IntSize(
                width = size.width - strokeSize,
                height = strokeSize,
            ),
            color = color,
        )

        // 4 to 1
        fillRect(
            offset = IntOffset(
                x = offset.x,
                y = offset.y + strokeSize,
            ),
            size = IntSize(
                width = strokeSize,
                height = size.height - strokeSize,
            ),
            color = color,
        )
    }

    override fun drawText(offset: IntOffset, text: String, color: Color) {
        withBlend {
            fontRenderer.drawString(text, offset.x, offset.y, color.value)
        }
    }

    override fun drawText(offset: IntOffset, width: Int, text: String, color: Color) {
        withBlend {
            fontRenderer.drawSplitString(text, offset.x, offset.y, width, color.value)
        }
    }

    override fun drawText(offset: IntOffset, text: CombineText, color: Color) =
        drawText(offset, text.toMinecraft().formattedText, color)

    override fun drawText(offset: IntOffset, width: Int, text: CombineText, color: Color) =
        drawText(offset, width, text.toMinecraft().formattedText, color)

    override fun drawTextWithShadow(offset: IntOffset, text: String, color: Color) {
        withBlend {
            fontRenderer.drawStringWithShadow(text, offset.x.toFloat(), offset.y.toFloat(), color.value)
        }
    }

    override fun drawTextWithShadow(offset: IntOffset, width: Int, text: String, color: Color) {
        // TODO wrap text
        withBlend {
            fontRenderer.drawStringWithShadow(text, offset.x.toFloat(), offset.y.toFloat(), color.value)
        }
    }

    override fun drawTextWithShadow(offset: IntOffset, text: CombineText, color: Color) =
        drawTextWithShadow(offset, text.toMinecraft().formattedText, color)

    override fun drawTextWithShadow(offset: IntOffset, width: Int, text: CombineText, color: Color) =
        drawTextWithShadow(offset, width, text.toMinecraft().formattedText, color)

    companion object {
        private val IDENTIFIER_WIDGETS = ResourceLocation("textures/gui/widgets.png")
    }

    override fun drawTexture(texture: Texture, dstRect: Rect, uvRect: Rect, tint: Color) {
        client.textureManager.bindTexture(texture.identifier.toMinecraft())
        GlStateManager.color(1f, 1f, 1f, 1f)
        val tessellator = Tessellator.getInstance()
        val bufferBuilder = tessellator.buffer
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        bufferBuilder
            .pos(dstRect.left.toDouble(), dstRect.top.toDouble(), 0.0)
            .tex(uvRect.left.toDouble(), uvRect.top.toDouble())
            .endVertex()
        bufferBuilder
            .pos(dstRect.left.toDouble(), dstRect.bottom.toDouble(), 0.0)
            .tex(uvRect.left.toDouble(), uvRect.bottom.toDouble())
            .endVertex()
        bufferBuilder
            .pos(dstRect.right.toDouble(), dstRect.bottom.toDouble(), 0.0)
            .tex(uvRect.right.toDouble(), uvRect.bottom.toDouble())
            .endVertex()
        bufferBuilder
            .pos(dstRect.right.toDouble(), dstRect.top.toDouble(), 0.0)
            .tex(uvRect.right.toDouble(), uvRect.top.toDouble())
            .endVertex()
        tessellator.draw()
    }

    private fun drawButtonTexture(dstRect: IntRect, textureY: Int) {
        client.textureManager.bindTexture(IDENTIFIER_WIDGETS)
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)
        drawTexturedModalRect(
            dstRect.offset.x,
            dstRect.offset.y,
            0,
            textureY,
            dstRect.size.width / 2,
            dstRect.size.height,
        )
        drawTexturedModalRect(
            dstRect.offset.x + dstRect.size.width / 2,
            dstRect.offset.y,
            200 - dstRect.size.width / 2,
            textureY,
            dstRect.size.width / 2,
            dstRect.size.height,
        )
    }

    override fun drawGuiTexture(texture: GuiTexture, dstRect: IntRect) {
        when (texture) {
            GuiTexture.BUTTON -> drawButtonTexture(dstRect, 66)
            GuiTexture.BUTTON_HOVER -> drawButtonTexture(dstRect, 86)
            GuiTexture.BUTTON_ACTIVE -> drawButtonTexture(dstRect, 86)
            GuiTexture.BUTTON_DISABLED -> drawButtonTexture(dstRect, 46)
        }
    }

    override fun drawItemStack(offset: IntOffset, size: IntSize, stack: ItemStack) {
        val minecraftStack = ((stack as? ItemStackImpl) ?: return).inner
        scale(size.width.toFloat() / 16f, size.height.toFloat() / 16f)
        pushState()
        GlStateManager.enableDepth()
        RenderHelper.enableGUIStandardItemLighting()
        itemRenderer.renderItemAndEffectIntoGUI(minecraftStack, offset.x, offset.y)
        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableDepth()
        popState()
    }

    override fun enableBlend() {
        GlStateManager.enableBlend()
        GlStateManager.enableAlpha()
    }

    override fun disableBlend() {
        GlStateManager.disableAlpha()
        GlStateManager.disableBlend()
    }

    override fun blendFunction(func: BlendFunction) {
        fun BlendFactor.toSrcFactor() =
            when (this) {
                BlendFactor.ONE -> GlStateManager.SourceFactor.ONE
                BlendFactor.ZERO -> GlStateManager.SourceFactor.ZERO
                BlendFactor.SRC_COLOR -> GlStateManager.SourceFactor.SRC_COLOR
                BlendFactor.SRC_ALPHA -> GlStateManager.SourceFactor.SRC_ALPHA
                BlendFactor.ONE_MINUS_SRC_ALPHA -> GlStateManager.SourceFactor.ONE_MINUS_SRC_ALPHA
                BlendFactor.ONE_MINUS_SRC_COLOR -> GlStateManager.SourceFactor.ONE_MINUS_SRC_COLOR
                BlendFactor.DST_COLOR -> GlStateManager.SourceFactor.DST_COLOR
                BlendFactor.DST_ALPHA -> GlStateManager.SourceFactor.DST_ALPHA
                BlendFactor.ONE_MINUS_DST_ALPHA -> GlStateManager.SourceFactor.ONE_MINUS_DST_ALPHA
                BlendFactor.ONE_MINUS_DST_COLOR -> GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR
            }

        fun BlendFactor.toDstFactor() =
            when (this) {
                BlendFactor.ONE -> GlStateManager.DestFactor.ONE
                BlendFactor.ZERO -> GlStateManager.DestFactor.ZERO
                BlendFactor.SRC_COLOR -> GlStateManager.DestFactor.SRC_COLOR
                BlendFactor.SRC_ALPHA -> GlStateManager.DestFactor.SRC_ALPHA
                BlendFactor.ONE_MINUS_SRC_ALPHA -> GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
                BlendFactor.ONE_MINUS_SRC_COLOR -> GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR
                BlendFactor.DST_COLOR -> GlStateManager.DestFactor.DST_COLOR
                BlendFactor.DST_ALPHA -> GlStateManager.DestFactor.DST_ALPHA
                BlendFactor.ONE_MINUS_DST_ALPHA -> GlStateManager.DestFactor.ONE_MINUS_DST_ALPHA
                BlendFactor.ONE_MINUS_DST_COLOR -> GlStateManager.DestFactor.ONE_MINUS_DST_COLOR
            }

        GlStateManager.tryBlendFuncSeparate(
            func.srcFactor.toSrcFactor(),
            func.dstFactor.toDstFactor(),
            func.srcAlpha.toSrcFactor(),
            func.dstAlpha.toDstFactor()
        )
    }

    override fun defaultBlendFunction() {
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        )
    }

    private val clipStack = arrayListOf<IntRect>()

    override fun pushClip(absoluteArea: IntRect, relativeArea: IntRect) {
        val scaleFactor = scaledResolution.scaleFactor
        val rect = IntRect(
            offset = absoluteArea.offset * scaleFactor,
            size = absoluteArea.size * scaleFactor,
        )
        GL11.glScissor(rect.left, client.displayHeight - rect.bottom, rect.size.width, rect.size.height)
        if (clipStack.isEmpty()) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST)
        }
        clipStack.add(rect)
    }

    override fun popClip() {
        if (clipStack.isEmpty()) {
            return
        } else if (clipStack.size == 1) {
            clipStack.clear()
            GL11.glDisable(GL11.GL_SCISSOR_TEST)
        } else {
            val item = clipStack.removeLast<IntRect>()
            GL11.glScissor(item.left, client.displayHeight - item.bottom, item.size.width, item.size.height)
        }
    }
}