package top.fifthlight.combine.platform

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import org.joml.Quaternionf
import top.fifthlight.combine.data.ItemStack
import top.fifthlight.combine.data.Texture
import top.fifthlight.combine.paint.*
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntRect
import top.fifthlight.data.IntSize
import top.fifthlight.data.Rect
import java.util.function.Supplier
import top.fifthlight.combine.data.Text as CombineText

inline fun withShader(program: Supplier<ShaderInstance>, crossinline block: () -> Unit) {
    val originalShader = RenderSystem.getShader()
    RenderSystem.setShader(program)
    block()
    originalShader?.let {
        RenderSystem.setShader { originalShader }
    }
}

class CanvasImpl(
    val drawContext: GuiGraphics,
    val textRenderer: Font,
) : Canvas {
    override val textMeasurer: TextMeasurer = TextMeasurerImpl(textRenderer)

    override fun pushState() {
        drawContext.pose().pushPose()
    }

    override fun popState() {
        drawContext.pose().popPose()
    }

    override fun translate(x: Int, y: Int) {
        drawContext.pose().translate(x.toDouble(), y.toDouble(), 0.0)
    }

    override fun translate(x: Float, y: Float) {
        drawContext.pose().translate(x.toDouble(), y.toDouble(), 0.0)
    }

    override fun rotate(degrees: Float) {
        Quaternionf().apply {
            rotateZ(Math.toRadians(degrees.toDouble()).toFloat())
            drawContext.pose().mulPose(this)
        }
    }

    override fun scale(x: Float, y: Float) {
        drawContext.pose().scale(x, y, 1f)
    }

    override fun fillRect(offset: IntOffset, size: IntSize, color: Color) {
        drawContext.fill(offset.x, offset.y, offset.x + size.width, offset.y + size.height, color.value)
    }

    override fun drawRect(offset: IntOffset, size: IntSize, color: Color) {
        drawContext.renderOutline(offset.x, offset.y, size.width, size.height, color.value)
    }

    override fun drawText(offset: IntOffset, text: String, color: Color) {
        drawContext.drawString(textRenderer, text, offset.x, offset.y, color.value, false)
    }

    override fun drawText(offset: IntOffset, width: Int, text: String, color: Color) {
        drawContext.drawWordWrap(textRenderer, Component.literal(text), offset.x, offset.y, width, color.value)
    }

    override fun drawText(offset: IntOffset, text: CombineText, color: Color) {
        drawContext.drawString(textRenderer, text.toMinecraft(), offset.x, offset.y, color.value, false)
    }

    override fun drawText(offset: IntOffset, width: Int, text: CombineText, color: Color) {
        drawContext.drawWordWrap(textRenderer, text.toMinecraft(), offset.x, offset.y, width, color.value)
    }

    override fun drawTextWithShadow(offset: IntOffset, text: String, color: Color) {
        drawContext.drawString(textRenderer, text, offset.x, offset.y, color.value, true)
    }

    override fun drawTextWithShadow(offset: IntOffset, width: Int, text: String, color: Color) {
        // TODO wrap text
        drawContext.drawString(textRenderer, text, offset.x, offset.y, color.value, true)
    }

    override fun drawTextWithShadow(offset: IntOffset, text: CombineText, color: Color) {
        drawContext.drawString(textRenderer, text.toMinecraft(), offset.x, offset.y, color.value, true)
    }

    override fun drawTextWithShadow(offset: IntOffset, width: Int, text: CombineText, color: Color) {
        // TODO wrap text
        drawContext.drawString(textRenderer, text.toMinecraft(), offset.x, offset.y, color.value, true)
    }

    companion object {
        private val IDENTIFIER_WIDGETS =
            ResourceLocation(ResourceLocation.DEFAULT_NAMESPACE, "textures/gui/widgets.png")
    }

    override fun drawTexture(texture: Texture, dstRect: Rect, uvRect: Rect, tint: Color) {
        RenderSystem.setShaderTexture(0, texture.identifier.toMinecraft())
        withShader({ GameRenderer.getPositionTexColorShader()!! }) {
            val matrix = drawContext.pose().last().pose()
            val bufferBuilder = Tesselator.getInstance().builder
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)
            bufferBuilder
                .vertex(matrix, dstRect.left, dstRect.top, 0f)
                .uv(uvRect.left, uvRect.top)
                .color(tint.value)
                .endVertex()
            bufferBuilder
                .vertex(matrix, dstRect.left, dstRect.bottom, 0f)
                .uv(uvRect.left, uvRect.bottom)
                .color(tint.value)
                .endVertex()
            bufferBuilder
                .vertex(matrix, dstRect.right, dstRect.bottom, 0f)
                .uv(uvRect.right, uvRect.bottom)
                .color(tint.value)
                .endVertex()
            bufferBuilder
                .vertex(matrix, dstRect.right, dstRect.top, 0f)
                .uv(uvRect.right, uvRect.top)
                .color(tint.value)
                .endVertex()
            BufferUploader.drawWithShader(bufferBuilder.end())
        }
    }

    private fun drawButtonTexture(dstRect: IntRect, textureY: Int) {
        drawContext.blitNineSliced(
            IDENTIFIER_WIDGETS,
            dstRect.left,
            dstRect.top,
            dstRect.size.width,
            dstRect.size.height,
            20,
            4,
            200,
            20,
            0,
            textureY,
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
        drawContext.pose().scale(size.width.toFloat() / 16f, size.height.toFloat() / 16f, 1f)
        pushState()
        drawContext.renderItem(minecraftStack, offset.x, offset.y)
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

    override fun pushClip(absoluteArea: IntRect, relativeArea: IntRect) {
        drawContext.enableScissor(absoluteArea.left, absoluteArea.top, absoluteArea.right, absoluteArea.bottom)
    }

    override fun popClip() {
        drawContext.disableScissor()
    }
}