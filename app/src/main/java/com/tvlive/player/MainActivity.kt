package com.tvlive.player

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "TestMainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            Log.d(TAG, "=== App Started ===")
            
            val textView = TextView(this)
            textView.text = "电视直播播放器\n\n按返回键退出"
            textView.textSize = 24f
            textView.setPadding(50, 100, 50, 100)
            textView.gravity = android.view.Gravity.CENTER
            
            setContentView(textView)
            
            Log.d(TAG, "=== App Loaded Successfully ===")
        } catch (e: Exception) {
            Log.e(TAG, "=== Error ===", e)
        }
    }
}
