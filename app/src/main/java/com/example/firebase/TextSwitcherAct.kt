package com.example.firebase

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_text_switcher.*
import java.util.*

class TextSwitcherAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_switcher)

        val texts = arrayOf("Morad", "Wasseem", "Aboood", "Nedal", "Mohammed")
        var index = 0

        btnnext.setOnClickListener {

            // Genarate Random button Color
            val rnd = Random()
            it.setBackgroundColor(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)))

            // is Last Index
            if (index == texts.size - 1) {
                index = 0
                textSwitcher.setText(texts[index])
            } else {
                textSwitcher.setText(texts[++index])
            }
        }

        // text for Switcher
        textSwitcher.setFactory {
            val tv = TextView(this@TextSwitcherAct)
            tv.text = "Android Camp"
            tv.setTextColor(Color.BLACK)
            tv.textSize = 60f
            tv.gravity = Gravity.CENTER
            tv.typeface = Typeface.SANS_SERIF
            tv.height = 400

            // Genarate Random Text Color
            val rnd = Random()
            tv.setTextColor(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)))
            tv.setBackgroundColor(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)))

            return@setFactory tv
        }


        // Genarate Random Background  Color
        val rnd = Random()
        continer.setBackgroundColor(Color.argb(100, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)))



    }
}