package ui.anwesome.com.kotlinwhiteredcircleview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.whiteredcircleview.WhiteRedCircleView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WhiteRedCircleView.create(this)
    }
}
