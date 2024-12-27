package top.fifthlight.touchcontroller.layout

import net.minecraft.client.render.*
import top.fifthlight.combine.paint.Canvas
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.platform.CanvasImpl
import top.fifthlight.combine.platform.withShader
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
        val drawContext = (canvas as CanvasImpl).drawContext
        withShader({ GameRenderer.getPositionColorProgram()!! }) {
            val matrix = drawContext.matrices.peek().positionMatrix
            val bufferBuilder =
                Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
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

                bufferBuilder.vertex(matrix, point0.x, point0.y, 0f).color(Colors.WHITE.value)
                bufferBuilder.vertex(matrix, point2.x, point2.y, 0f).color(Colors.WHITE.value)
                bufferBuilder.vertex(matrix, point3.x, point3.y, 0f).color(Colors.WHITE.value)
                bufferBuilder.vertex(matrix, point1.x, point1.y, 0f).color(Colors.WHITE.value)
            }

            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        }
    }

    override fun renderInner(canvas: Canvas, config: CrosshairConfig, progress: Float) {
        val drawContext = (canvas as CanvasImpl).drawContext
        withShader({ GameRenderer.getPositionColorProgram()!! }) {
            val matrix = drawContext.matrices.peek().positionMatrix
            val bufferBuilder =
                Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR)
            bufferBuilder.vertex(matrix, 0f, 0f, 0f).color(Colors.WHITE.value)

            var angle = 0f
            for (i in 0..CROSSHAIR_CIRCLE_PARTS) {
                val point = point(angle, config.radius * progress)
                angle -= CROSSHAIR_CIRCLE_ANGLE

                bufferBuilder.vertex(matrix, point.x, point.y, 0f).color(Colors.WHITE.value)
            }

            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        }
    }
}