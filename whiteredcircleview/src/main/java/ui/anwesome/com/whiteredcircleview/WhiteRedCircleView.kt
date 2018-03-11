package ui.anwesome.com.whiteredcircleview

/**
 * Created by anweshmishra on 11/03/18.
 */
import android.view.*
import android.content.*
import android.graphics.*
import java.util.concurrent.ConcurrentLinkedQueue

class WhiteRedCircleView(ctx : Context, var n : Int = 6) : View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val renderer = Renderer(this)
    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }
    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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
    data class RedWhiteCircle(var i : Int) {
        val state = State()
        fun draw(canvas : Canvas, paint : Paint, x : Float, y : Float, a : Float, r : Float, deg : Float) {
            val col_factor = (255 * (1 - state.scale)).toInt()
            paint.color = Color.rgb(255, col_factor, col_factor)
            canvas.save()
            canvas.translate(x, y)
            canvas.rotate(i * deg)
            canvas.drawCircle(a, 0f, r, paint)
            canvas.restore()
        }
        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }
        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }
    }
    data class RedWhiteCircleContainer(var n : Int) {
        val containerState = ContainerState(n)
        val circles : ConcurrentLinkedQueue<RedWhiteCircle> = ConcurrentLinkedQueue()
        init {
            for(i in 0..n-1) {
                circles.add(RedWhiteCircle(i))
            }
        }
        fun draw(canvas : Canvas, paint : Paint) {
            val w = canvas.width.toFloat()
            val h = canvas.height.toFloat()
            paint.color = Color.WHITE
            canvas.drawCircle(w / 2, h / 2, Math.min(w,h) / 18, paint)
            circles.forEach {
                it.draw(canvas, paint, w / 2, h / 2, Math.min(w,h) / 4, Math.min(w,h) / 18, (360f) / n)
            }
        }
        fun update(stopcb : (Float,Int) -> Unit) {
            containerState.executeCb {
                circles.at(it)?.update { scale ->
                    containerState.executeCb{
                        stopcb(scale, it)
                    }
                    containerState.incrementCounter()
                }
            }
        }
        fun startUpdating(startcb : () -> Unit) {
            containerState.executeCb {
                circles.at(it)?.startUpdating(startcb)
            }
        }
    }
    data class Animator(var view : View, var animated : Boolean = false) {
        fun animate(updatecb : () -> Unit) {
            if(animated) {
                try {
                    updatecb()
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch(ex : Exception) {

                }
            }
        }
        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }
        fun stop() {
            if(animated) {
                animated = false
            }
        }
    }
    data class Renderer(var view : WhiteRedCircleView) {
        val container : RedWhiteCircleContainer = RedWhiteCircleContainer(view.n)
        val animator : Animator = Animator(view)
        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            container.draw(canvas, paint)
            animator.animate {
                container.update {scale, i ->
                    animator.stop()
                }
            }
        }
        fun handleTap () {
            container.startUpdating {
                animator.start()
            }
        }
    }
}
fun ConcurrentLinkedQueue<WhiteRedCircleView.RedWhiteCircle>.at(i : Int) : WhiteRedCircleView.RedWhiteCircle? {
    var j = 0
    forEach {
        if(i == j) {
            return it
        }
        j++
    }
    return null
}