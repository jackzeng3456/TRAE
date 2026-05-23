package com.tvlive.player

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val textView = TextView(this)
        textView.text = "电视直播播放器\n\n测试成功！"
        textView.textSize = 28f
        textView.setPadding(50, 100, 50, 100)
        textView.gravity = android.view.Gravity.CENTER
        
        setContentView(textView)
    }
}
