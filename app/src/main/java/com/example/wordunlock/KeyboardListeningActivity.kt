package com.example.wordunlock

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.wordunlock.interfaces.SoftKeyboardListener


class KeyboardListeningActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        /*val softKeyboardDetector = SoftKeyboardDetector(this, object : SoftKeyboardListener {
            override fun onSoftKeyboardShown(isVisible: Boolean) {
                // 发送广播通知服务
                val intent = Intent("KEYBOARD_STATE_CHANGED").apply {
                    putExtra("isVisible", isVisible)
                }
                sendBroadcast(intent)
            }
        })*/
    }
}