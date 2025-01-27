package top.fifthlight.combine.platform

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.IVertexBuilder
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.AbstractGui
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.WorldVertexBufferUploader
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.vector.Quaternion
import net.minecraft.util.math.vector.Vector3f
import net.minecraft.util.text.ITextComponent
import org.lwjgl.opengl.GL11
import top.fifthlight.combine.data.ItemStack
import top.fifthlight.combine.data.Texture
import top.fifthlight.combine.paint.*
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntRect
import top.fifthlight.data.IntSize
import top.fifthlight.data.Rect
import top.fifthlight.touchcontroller.mixin.Matrix4fAccessor
import top.fifthlight.combine.data.Text as CombineText

fun IVertexBuilder.color(color: Color): IVertexBuilder = color(color.r, color.g, color.b, color.a)

class CanvasImpl(
    val matrices: MatrixStack,
    val textRenderer: FontRenderer,
) : Canvas, AbstractGui() {
    private val client = Minecraft.getInstance()
    override val textMeasurer: TextMeasurer = TextMeasurerImpl(textRenderer)

    override fun pushState() {
        matrices.pushPose()
    }

    override fun popState() {
        matrices.popPose()
    }

    override fun translate(x: Int, y: Int) {
        matrices.translate(x.toDouble(), y.toDouble(), 0.0)
    }

    override fun translate(x: Float, y: Float) {
        matrices.translate(x.toDouble(), y.toDouble(), 0.0)
    }

    override fun rotate(degrees: Float) {
        matrices.mulPose(Quaternion(Vector3f.ZP, degrees, true))
    }

    override fun scale(x: Float, y: Float) {
        matrices.scale(x, y, 1f)
    }

    override fun fillRect(offset: IntOffset, size: IntSize, color: Color) {
        fill(matrices, offset.x, offset.y, offset.x + size.width, offset.y + size.height, color.value)
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
        textRenderer.draw(matrices, text, offset.x.toFloat(), offset.y.toFloat(), color.value)
    }

    override fun drawText(offset: IntOffset, width: Int, text: String, color: Color) {
        var y = offset.y.toFloat()
        for (line in textRenderer.split(ITextComponent.nullToEmpty(text), width)) {
            textRenderer.draw(matrices, line, offset.x.toFloat(), y, color.value)
            y += textRenderer.lineHeight
        }
    }

    override fun drawText(offset: IntOffset, text: CombineText, color: Color) {
        textRenderer.draw(matrices, text.toMinecraft(), offset.x.toFloat(), offset.y.toFloat(), color.value)
    }

    override fun drawText(offset: IntOffset, width: Int, text: CombineText, color: Color) {
        var y = offset.y.toFloat()
        for (line in textRenderer.split(text.toMinecraft(), width)) {
            textRenderer.draw(matrices, line, offset.x.toFloat(), y, color.value)
            y += textRenderer.lineHeight
        }
    }

    override fun drawTextWithShadow(offset: IntOffset, text: String, color: Color) {
        textRenderer.drawShadow(matrices, text, offset.x.toFloat(), offset.y.toFloat(), color.value)
    }

    override fun drawTextWithShadow(offset: IntOffset, width: Int, text: String, color: Color) {
        var y = offset.y.toFloat()
        for (line in textRenderer.split(ITextComponent.nullToEmpty(text), width)) {
            textRenderer.drawShadow(matrices, line, offset.x.toFloat(), y, color.value)
            y += textRenderer.lineHeight
        }
    }

    override fun drawTextWithShadow(offset: IntOffset, text: CombineText, color: Color) {
        textRenderer.drawShadow(matrices, text.toMinecraft(), offset.x.toFloat(), offset.y.toFloat(), color.value)
    }

    override fun drawTextWithShadow(offset: IntOffset, width: Int, text: CombineText, color: Color) {
        var y = offset.y.toFloat()
        for (line in textRenderer.split(text.toMinecraft(), width)) {
            textRenderer.drawShadow(matrices, line, offset.x.toFloat(), y, color.value)
            y += textRenderer.lineHeight
        }
    }

    companion object {
        private val IDENTIFIER_WIDGETS = ResourceLocation("textures/gui/widgets.png")
    }

    override fun drawTexture(texture: Texture, dstRect: Rect, uvRect: Rect, tint: Color) {
        this.client.textureManager.bind(texture.identifier.toMinecraft())
        val matrix = matrices.last().pose()
        val bufferBuilder = Tessellator.getInstance().builder
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX)
        bufferBuilder
            .vertex(matrix, dstRect.left, dstRect.top, 0f)
            .color(tint)
            .uv(uvRect.left, uvRect.top)
            .endVertex()
        bufferBuilder
            .vertex(matrix, dstRect.left, dstRect.bottom, 0f)
            .color(tint)
            .uv(uvRect.left, uvRect.bottom)
            .endVertex()
        bufferBuilder
            .vertex(matrix, dstRect.right, dstRect.bottom, 0f)
            .color(tint)
            .uv(uvRect.right, uvRect.bottom)
            .endVertex()
        bufferBuilder
            .vertex(matrix, dstRect.right, dstRect.top, 0f)
            .color(tint)
            .uv(uvRect.right, uvRect.top)
            .endVertex()
        bufferBuilder.end()
        WorldVertexBufferUploader.end(bufferBuilder)
    }

    private fun drawButtonTexture(dstRect: IntRect, textureY: Int) {
        client.textureManager.bind(IDENTIFIER_WIDGETS)
        blit(
            matrices,
            dstRect.offset.x,
            dstRect.offset.y,
            0,
            textureY,
            dstRect.size.width / 2,
            dstRect.size.height,
        )
        blit(
            matrices,
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
        val matrix = matrices.last().pose()
        // TODO apply matrix when rendering
        @Suppress("CAST_NEVER_SUCCEEDS")
        val accessor = matrix as Matrix4fAccessor
        client.itemRenderer.renderGuiItem(
            stack.toVanilla(),
            accessor.a03.toInt() + offset.left,
            accessor.a13.toInt() + offset.top
        )
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

    private val clipStack = arrayListOf<IntRect>()

    override fun pushClip(absoluteArea: IntRect, relativeArea: IntRect) {
        val scaleFactor = client.window.guiScale.toInt()
        val rect = IntRect(
            offset = absoluteArea.offset * scaleFactor,
            size = absoluteArea.size * scaleFactor,
        )
        RenderSystem.enableScissor(rect.left, client.window.height - rect.bottom, rect.size.width, rect.size.height)
        clipStack.add(rect)
    }

    override fun popClip() {
        if (clipStack.isEmpty()) {
            return
        } else if (clipStack.size == 1) {
            clipStack.clear()
            RenderSystem.disableScissor()
        } else {
            val item = clipStack.removeLast<IntRect>()
            RenderSystem.enableScissor(item.left, client.window.height - item.bottom, item.size.width, item.size.height)
        }
    }
}