package top.fifthlight.touchcontroller.gal

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.*
import org.lwjgl.opengl.GL11
import top.fifthlight.combine.paint.Canvas
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.platform.CanvasImpl
import top.fifthlight.combine.platform.color
import top.fifthlight.data.Offset
import top.fifthlight.touchcontroller.config.CrosshairConfig
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val CROSSHAIR_CIRCLE_PARTS = 24
private const val CROSSHAIR_CIRCLE_ANGLE = 2 * PI.toFloat() / CROSSHAIR_CIRCLE_PARTS

private fun point(angle: Float, radius: Float) = Offset(
    x = cos(angle) * radius,
    y = sin(angle) * radius
)

object CrosshairRendererImpl : CrosshairRenderer {
    override fun renderOuter(canvas: Canvas, config: CrosshairConfig) {
        val matrices = (canvas as CanvasImpl).matrices
        val matrix = matrices.peek().model
        val bufferBuilder = Tessellator.getInstance().buffer
        bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR)
        val innerRadius = config.radius.toFloat()
        val outerRadius = (config.radius + config.outerRadius).toFloat()
        var angle = -PI.toFloat() / 2f
        for (i in 0 until CROSSHAIR_CIRCLE_PARTS) {
            val endAngle = angle + CROSSHAIR_CIRCLE_ANGLE
            val point0 = point(angle, outerRadius)
            val point1 = point(endAngle, outerRadius)
            val point2 = point(angle, innerRadius)
            val point3 = point(endAngle, innerRadius)
            angle = endAngle

            bufferBuilder.vertex(matrix, point0.x, point0.y, 0f).color(Colors.WHITE).next()
            bufferBuilder.vertex(matrix, point2.x, point2.y, 0f).color(Colors.WHITE).next()
            bufferBuilder.vertex(matrix, point3.x, point3.y, 0f).color(Colors.WHITE).next()
            bufferBuilder.vertex(matrix, point1.x, point1.y, 0f).color(Colors.WHITE).next()
        }
        bufferBuilder.end()
        RenderSystem.disableTexture()
        BufferRenderer.draw(bufferBuilder)
        RenderSystem.enableTexture()
    }

    override fun renderInner(canvas: Canvas, config: CrosshairConfig, progress: Float) {
        val matrices = (canvas as CanvasImpl).matrices
        val matrix = matrices.peek().model
        val bufferBuilder = Tessellator.getInstance().buffer
        bufferBuilder.begin(GL11.GL_TRIANGLE_FAN, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, 0f, 0f, 0f).color(Colors.WHITE).next()

        var angle = 0f
        for (i in 0..CROSSHAIR_CIRCLE_PARTS) {
            val point = point(angle, config.radius * progress)
            angle -= CROSSHAIR_CIRCLE_ANGLE

            bufferBuilder.vertex(matrix, point.x, point.y, 0f).color(Colors.WHITE).next()
        }
        bufferBuilder.end()
        RenderSystem.disableTexture()
        BufferRenderer.draw(bufferBuilder)
        RenderSystem.enableTexture()
    }
}