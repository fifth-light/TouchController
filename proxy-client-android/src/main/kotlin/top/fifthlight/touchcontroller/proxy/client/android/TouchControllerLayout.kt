package top.fifthlight.touchcontroller.proxy.client.android

import android.content.Context
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.MotionEvent
import android.widget.FrameLayout
import top.fifthlight.touchcontroller.proxy.client.LauncherProxyClient
import top.fifthlight.touchcontroller.proxy.data.Offset

class TouchControllerLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    var client: LauncherProxyClient? = null
    private val pointerIdMap = SparseIntArray()
    private var nextPointerId = 1

    private fun MotionEvent.getOffset(index: Int) = Offset(
        getX(index) / width,
        getY(index) / height
    )

    private fun handleTouchEvent(event: MotionEvent) {
        val client = client ?: return
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val pointerId = nextPointerId++
                pointerIdMap.put(event.getPointerId(0), pointerId)
                client.addPointer(pointerId, event.getOffset(0))
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                val pointerId = nextPointerId++
                val i = event.actionIndex
                pointerIdMap.put(event.getPointerId(i), pointerId);
                client.addPointer(pointerId, event.getOffset(i))
            }

            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    val pointerId = pointerIdMap.get(event.getPointerId(i))
                    client.addPointer(pointerId, event.getOffset(i))
                }
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                client.clearPointer()
                pointerIdMap.clear()
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val i = event.actionIndex
                val pointerId = pointerIdMap.get(event.getPointerId(i))
                if (pointerId != 0) {
                    pointerIdMap.delete(pointerId)
                    client.removePointer(pointerId)
                }
            }
        }
    }

    @Override
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        handleTouchEvent(event)
        return super.dispatchTouchEvent(event)
    }
}