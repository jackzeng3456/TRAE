package com.tvlive.player;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView textView = new TextView(this);
        textView.setText("电视直播播放器\n\n测试成功！");
        textView.setTextSize(28);
        textView.setPadding(50, 100, 50, 100);
        textView.setGravity(android.view.Gravity.CENTER);
        
        setContentView(textView);
    }
}
