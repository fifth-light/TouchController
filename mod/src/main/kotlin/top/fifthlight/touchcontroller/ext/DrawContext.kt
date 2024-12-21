package top.fifthlight.touchcontroller.ext

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gl.ShaderProgramKeys
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.util.Identifier
import top.fifthlight.data.Offset
import top.fifthlight.data.Rect

inline fun <reified T> DrawContext.withTranslate(offset: Offset, crossinline block: () -> T): T =
    matrices.withMatrix {
        matrices.translate(offset.x, offset.y, 0f)
        block()
    }

inline fun <reified T> DrawContext.withTranslate(x: Float, y: Float, crossinline block: () -> T): T =
    matrices.withMatrix {
        matrices.translate(x, y, 0f)
        block()
    }

inline fun <reified T> DrawContext.withScale(scale: Float, crossinline block: () -> T): T =
    withScale(scale, scale, block)

inline fun <reified T> DrawContext.withScale(x: Float, y: Float, crossinline block: () -> T): T =
    matrices.withMatrix {
        matrices.scale(x, y, 1f)
        block()
    }

fun DrawContext.drawTexture(id: Identifier, dstRect: Rect, uvRect: Rect = Rect.ONE) {
    RenderSystem.setShaderTexture(0, id)
    withShader(ShaderProgramKeys.POSITION_TEX) {
        val matrix = matrices.peek().positionMatrix
        val bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
        bufferBuilder.vertex(matrix, dstRect.left, dstRect.top, 0f).texture(uvRect.left, uvRect.top)
        bufferBuilder.vertex(matrix, dstRect.left, dstRect.bottom, 0f).texture(uvRect.left, uvRect.bottom)
        bufferBuilder.vertex(matrix, dstRect.right, dstRect.bottom, 0f).texture(uvRect.right, uvRect.bottom)
        bufferBuilder.vertex(matrix, dstRect.right, dstRect.top, 0f).texture(uvRect.right, uvRect.top)
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
    }
}

fun DrawContext.drawTexture(id: Identifier, dstRect: Rect, uvRect: Rect = Rect.ONE, color: UInt) =
    drawTexture(id, dstRect, uvRect, color.toInt())

fun DrawContext.drawTexture(id: Identifier, dstRect: Rect, uvRect: Rect = Rect.ONE, color: Int) {
    RenderSystem.setShaderTexture(0, id)
    withShader(ShaderProgramKeys.POSITION_TEX_COLOR) {
        val matrix = matrices.peek().positionMatrix
        val bufferBuilder =
            Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR)
        bufferBuilder
            .vertex(matrix, dstRect.left, dstRect.top, 0f)
            .texture(uvRect.left, uvRect.top).color(color)
        bufferBuilder
            .vertex(matrix, dstRect.left, dstRect.bottom, 0f)
            .texture(uvRect.left, uvRect.bottom).color(color)
        bufferBuilder
            .vertex(matrix, dstRect.right, dstRect.bottom, 0f)
            .texture(uvRect.right, uvRect.bottom)
            .color(color)
        bufferBuilder
            .vertex(matrix, dstRect.right, dstRect.top, 0f)
            .texture(uvRect.right, uvRect.top).color(color)
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
    }
}