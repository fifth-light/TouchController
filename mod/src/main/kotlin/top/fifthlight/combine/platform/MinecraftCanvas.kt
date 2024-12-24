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
import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.paint.Canvas
import top.fifthlight.combine.paint.Color
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntRect
import top.fifthlight.data.IntSize
import top.fifthlight.data.Rect

private inline fun withBlend(crossinline block: () -> Unit) {
    RenderSystem.enableBlend()
    block()
    RenderSystem.disableBlend()
}

private inline fun withBlendFunction(
    srcFactor: GlStateManager.SrcFactor,
    dstFactor: GlStateManager.DstFactor,
    srcAlpha: GlStateManager.SrcFactor,
    dstAlpha: GlStateManager.DstFactor,
    crossinline block: () -> Unit
) {
    RenderSystem.blendFuncSeparate(srcFactor, dstFactor, srcAlpha, dstAlpha)
    block()
    RenderSystem.defaultBlendFunc()
}

private inline fun withShader(program: ShaderProgramKey, crossinline block: () -> Unit) {
    val originalShader = RenderSystem.getShader()
    RenderSystem.setShader(program)
    block()
    originalShader?.let {
        RenderSystem.setShader(originalShader)
    }
}

class MinecraftCanvas(
    private val drawContext: DrawContext,
    private val textRenderer: TextRenderer,
) : Canvas {
    override fun pushState() {
        drawContext.matrices.push()
    }

    override fun popState() {
        drawContext.matrices.pop()
    }

    override fun transform(x: Int, y: Int) {
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

    override fun drawText(offset: IntOffset, width: Int, text: String, color: Color) {
        drawContext.drawTextWrapped(textRenderer, Text.literal(text), offset.x, offset.y, width, color.value)
    }

    override fun drawTextWithShadow(offset: IntOffset, text: String, color: Color) {
        // TODO wrap text
        drawContext.drawText(textRenderer, text, offset.x, offset.y, color.value, true)
    }

    override fun drawTexture(id: Identifier, dstRect: Rect, uvRect: Rect) {
        RenderSystem.setShaderTexture(0, id.toMinecraft())
        withShader(ShaderProgramKeys.POSITION_TEX) {
            val matrix = drawContext.matrices.peek().positionMatrix
            val bufferBuilder =
                Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
            bufferBuilder.vertex(matrix, dstRect.left, dstRect.top, 0f).texture(uvRect.left, uvRect.top)
            bufferBuilder.vertex(matrix, dstRect.left, dstRect.bottom, 0f).texture(uvRect.left, uvRect.bottom)
            bufferBuilder.vertex(matrix, dstRect.right, dstRect.bottom, 0f).texture(uvRect.right, uvRect.bottom)
            bufferBuilder.vertex(matrix, dstRect.right, dstRect.top, 0f).texture(uvRect.right, uvRect.top)
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        }
    }

    override fun drawGuiTexture(sprite: Identifier, dstRect: IntRect) {
        drawContext.drawGuiTexture(
            RenderLayer::getGuiTextured,
            sprite.toMinecraft(),
            dstRect.left,
            dstRect.top,
            dstRect.size.width,
            dstRect.size.height
        )
    }

    override fun pushClip(area: IntRect) {
        drawContext.enableScissor(area.left, area.top, area.right, area.bottom)
    }

    override fun popClip() {
        drawContext.disableScissor()
    }
}