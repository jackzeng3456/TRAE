package com.tvlive.player

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "TVLivePlayer"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            Log.d(TAG, "onCreate 开始")
            
            val textView = TextView(this)
            textView.text = "电视直播播放器\n\n测试成功！"
            textView.textSize = 28f
            textView.setPadding(50, 100, 50, 100)
            textView.gravity = android.view.Gravity.CENTER
            
            setContentView(textView)
            
            Log.d(TAG, "onCreate 成功完成")
        } catch (e: Exception) {
            Log.e(TAG, "onCreate 出错了", e)
            
            val errorText = TextView(this)
            errorText.text = "错误：${e.message}"
            errorText.textSize = 20f
            errorText.setPadding(50, 100, 50, 100)
            setContentView(errorText)
        }
    }
}
