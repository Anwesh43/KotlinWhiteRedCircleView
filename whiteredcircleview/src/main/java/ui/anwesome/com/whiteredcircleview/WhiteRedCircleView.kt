package ui.anwesome.com.whiteredcircleview

/**
 * Created by anweshmishra on 11/03/18.
 */
import android.view.*
import android.content.*
import android.graphics.*
class WhiteRedCircleView(ctx : Context) : View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas : Canvas) {

    }
    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {
        fun update(stopcb : (Float) -> Unit) {
            scale += 0.1f * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                stopcb(scale)
            }
        }
        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * scale
                startcb()
            }
        }
    }
    data class ContainerState(var n : Int, var j : Int = 0, var dir : Int = 1) {
        fun incrementCounter() {
            j += dir
            if (j == n || j == -1) {
                dir *= -1
            }
        }
        fun executeCb(cb : (Int) -> Unit) {
            cb(j)
        }
    }
}