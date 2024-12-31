package top.fifthlight.combine.platform

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gl.ShaderProgramKey
import net.minecraft.client.gl.ShaderProgramKeys
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.*
import net.minecraft.text.Text
import org.joml.Quaternionf
import top.fifthlight.combine.data.ItemStack
import top.fifthlight.combine.data.Texture
import top.fifthlight.combine.paint.*
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntRect
import top.fifthlight.data.IntSize
import top.fifthlight.data.Rect
import top.fifthlight.combine.data.Text as CombineText

private inline fun withShader(program: ShaderProgramKey, crossinline block: () -> Unit) {
    val originalShader = RenderSystem.getShader()
    RenderSystem.setShader(program)
    block()
    originalShader?.let {
        RenderSystem.setShader(originalShader)
    }
}

class CanvasImpl(
    val drawContext: DrawContext,
    val textRenderer: TextRenderer,
) : Canvas {
    override val textMeasurer: TextMeasurer = TextMeasurerImpl(textRenderer)

    override fun pushState() {
        drawContext.matrices.push()
    }

    override fun popState() {
        drawContext.matrices.pop()
    }

    override fun translate(x: Int, y: Int) {
        drawContext.matrices.translate(x.toDouble(), y.toDouble(), 0.0)
    }

    override fun translate(x: Float, y: Float) {
        drawContext.matrices.translate(x.toDouble(), y.toDouble(), 0.0)
    }

    override fun rotate(degrees: Float) {
        Quaternionf().apply {
            rotateZ(Math.toRadians(degrees.toDouble()).toFloat())
            drawContext.matrices.multiply(this)
        }
    }

    override fun fillRect(offset: IntOffset, size: IntSize, color: Color) {
        drawContext.fill(offset.x, offset.y, offset.x + size.width, offset.y + size.height, color.value)
    }

    override fun drawRect(offset: IntOffset, size: IntSize, color: Color) {
        drawContext.drawBorder(offset.x, offset.y, size.width, size.height, color.value)
    }

    override fun drawText(offset: IntOffset, text: String, color: Color) {
        drawContext.drawText(textRenderer, text, offset.x, offset.y, color.value, false)
    }

    override fun drawText(offset: IntOffset, width: Int, text: String, color: Color) {
        drawContext.drawWrappedText(textRenderer, Text.literal(text), offset.x, offset.y, width, color.value, false)
    }

    override fun drawText(offset: IntOffset, text: CombineText, color: Color) {
        drawContext.drawText(textRenderer, text.toMinecraft(), offset.x, offset.y, color.value, false)
    }

    override fun drawText(offset: IntOffset, width: Int, text: CombineText, color: Color) {
        drawContext.drawWrappedText(textRenderer, text.toMinecraft(), offset.x, offset.y, width, color.value, false)
    }

    override fun drawTextWithShadow(offset: IntOffset, text: String, color: Color) {
        drawContext.drawText(textRenderer, text, offset.x, offset.y, color.value, true)
    }

    override fun drawTextWithShadow(offset: IntOffset, width: Int, text: String, color: Color) {
        drawContext.drawWrappedTextWithShadow(textRenderer, Text.literal(text), offset.x, offset.y, width, color.value)
    }

    override fun drawTextWithShadow(offset: IntOffset, text: CombineText, color: Color) {
        drawContext.drawText(textRenderer, text.toMinecraft(), offset.x, offset.y, color.value, true)
    }

    override fun drawTextWithShadow(offset: IntOffset, width: Int, text: CombineText, color: Color) {
        drawContext.drawWrappedTextWithShadow(textRenderer, text.toMinecraft(), offset.x, offset.y, width, color.value)
    }

    override fun drawTexture(texture: Texture, dstRect: Rect, uvRect: Rect, tint: Color) {
        RenderSystem.setShaderTexture(0, texture.identifier.toMinecraft())
        withShader(ShaderProgramKeys.POSITION_TEX_COLOR) {
            val matrix = drawContext.matrices.peek().positionMatrix
            val bufferBuilder =
                Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR)
            bufferBuilder
                .vertex(matrix, dstRect.left, dstRect.top, 0f)
                .texture(uvRect.left, uvRect.top)
                .color(tint.value)
            bufferBuilder
                .vertex(matrix, dstRect.left, dstRect.bottom, 0f)
                .texture(uvRect.left, uvRect.bottom)
                .color(tint.value)
            bufferBuilder
                .vertex(matrix, dstRect.right, dstRect.bottom, 0f)
                .texture(uvRect.right, uvRect.bottom)
                .color(tint.value)
            bufferBuilder
                .vertex(matrix, dstRect.right, dstRect.top, 0f)
                .texture(uvRect.right, uvRect.top)
                .color(tint.value)
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        }
    }

    override fun drawGuiTexture(texture: GuiTexture, dstRect: IntRect) {
        drawContext.drawGuiTexture(
            RenderLayer::getGuiTextured,
            texture.toIdentifier(),
            dstRect.left,
            dstRect.top,
            dstRect.size.width,
            dstRect.size.height
        )
    }

    override fun drawItemStack(offset: IntOffset, size: IntSize, stack: ItemStack) {
        val minecraftStack = ((stack as? ItemStackImpl) ?: return).inner
        drawContext.matrices.scale(size.width.toFloat() / 16f, size.height.toFloat() / 16f, 1f)
        pushState()
        drawContext.drawItem(minecraftStack, offset.x, offset.y)
        popState()
    }

    override fun enableBlend() {
        RenderSystem.enableBlend()
    }

    override fun disableBlend() {
        RenderSystem.disableBlend()
    }

    override fun blendFunction(func: BlendFunction) {
        fun BlendFactor.toSrcFactor() =
            when (this) {
                BlendFactor.ONE -> GlStateManager.SrcFactor.ONE
                BlendFactor.ZERO -> GlStateManager.SrcFactor.ZERO
                BlendFactor.SRC_COLOR -> GlStateManager.SrcFactor.SRC_COLOR
                BlendFactor.SRC_ALPHA -> GlStateManager.SrcFactor.SRC_ALPHA
                BlendFactor.ONE_MINUS_SRC_ALPHA -> GlStateManager.SrcFactor.ONE_MINUS_SRC_ALPHA
                BlendFactor.ONE_MINUS_SRC_COLOR -> GlStateManager.SrcFactor.ONE_MINUS_SRC_COLOR
                BlendFactor.DST_COLOR -> GlStateManager.SrcFactor.DST_COLOR
                BlendFactor.DST_ALPHA -> GlStateManager.SrcFactor.DST_ALPHA
                BlendFactor.ONE_MINUS_DST_ALPHA -> GlStateManager.SrcFactor.ONE_MINUS_DST_ALPHA
                BlendFactor.ONE_MINUS_DST_COLOR -> GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR
            }

        fun BlendFactor.toDstFactor() =
            when (this) {
                BlendFactor.ONE -> GlStateManager.DstFactor.ONE
                BlendFactor.ZERO -> GlStateManager.DstFactor.ZERO
                BlendFactor.SRC_COLOR -> GlStateManager.DstFactor.SRC_COLOR
                BlendFactor.SRC_ALPHA -> GlStateManager.DstFactor.SRC_ALPHA
                BlendFactor.ONE_MINUS_SRC_ALPHA -> GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA
                BlendFactor.ONE_MINUS_SRC_COLOR -> GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR
                BlendFactor.DST_COLOR -> GlStateManager.DstFactor.DST_COLOR
                BlendFactor.DST_ALPHA -> GlStateManager.DstFactor.DST_ALPHA
                BlendFactor.ONE_MINUS_DST_ALPHA -> GlStateManager.DstFactor.ONE_MINUS_DST_ALPHA
                BlendFactor.ONE_MINUS_DST_COLOR -> GlStateManager.DstFactor.ONE_MINUS_DST_COLOR
            }

        RenderSystem.blendFuncSeparate(
            func.srcFactor.toSrcFactor(),
            func.dstFactor.toDstFactor(),
            func.srcAlpha.toSrcFactor(),
            func.dstAlpha.toDstFactor()
        )
    }

    override fun defaultBlendFunction() {
        RenderSystem.defaultBlendFunc()
    }

    override fun pushClip(absoluteArea: IntRect, relativeArea: IntRect) {
        drawContext.enableScissor(relativeArea.left, relativeArea.top, relativeArea.right, relativeArea.bottom)
    }

    override fun popClip() {
        drawContext.disableScissor()
    }
}