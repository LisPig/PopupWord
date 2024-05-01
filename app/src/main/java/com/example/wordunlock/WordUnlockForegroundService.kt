package com.example.wordunlock

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.text.InputType
import android.view.Gravity
import android.view.KeyEvent
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.wordunlock.util.SoftKeyboardListener
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.interfaces.OnInvokeView
import com.lzf.easyfloat.utils.DisplayUtils
import com.lzf.easyfloat.utils.InputMethodUtils


class WordUnlockForegroundService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 100
        private const val CHANNEL_ID = "word_unlock_channel"
    }

    private var keyboardListener: SoftKeyboardListener? = null
    private var wordTextView: TextView? = null
    private var commentTextView: TextView? = null
    private var inputEditText: EditText? = null
    override fun onBind(intent: Intent?): IBinder? = null



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 创建通知渠道 (Android 8.0 以上)
        createNotificationChannel()
        // 创建通知
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            //.setSmallIcon(R.drawable.ic_notification) // 替换为您的通知图标
            .setContentTitle("单词解锁服务")
            .setContentText("服务正在运行")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // 初始化键盘监听器，并在Activity中注册接收器

        // 注册键盘状态广播接收器（如果需要在服务内部处理某些逻辑）
        //registerReceiver(keyboardStateReceiver, IntentFilter("KEYBOARD_STATE_CHANGED"))

        // 启动前台服务
        startForeground(NOTIFICATION_ID, notification)
        val word = intent?.getStringExtra("word") ?: "example"
        val definition = intent?.getStringExtra("definition") ?: "示例"
        // 创建并显示悬浮窗
        showFloatingWindow(word,definition)
        return START_STICKY
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "单词解锁",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun showFloatingWindow(word: String, definition: String) {

        EasyFloat.with(this)
            .setTag("float_word_input")
            .setLayout(R.layout.float_word_input,OnInvokeView { view ->
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                // 在这里获取并保存对 TextView 的引用
                wordTextView = view.findViewById(R.id.wordTextView)
                commentTextView = view.findViewById(R.id.commentTextView)
                wordTextView?.text = word
                commentTextView?.text = definition
                val confirmButton: Button = view.findViewById<Button?>(R.id.confirmButton)
                val inputEditText: EditText = view.findViewById(R.id.inputEditText)
                inputEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS

                // 为 EditText 设置 OnClickListener
                inputEditText?.setOnClickListener {
                    InputMethodUtils.openInputMethod(inputEditText)
                    //EasyFloat.updateFloat("float_word_input",-1,-1,-1,-3)
                }
                inputEditText.setOnEditorActionListener(
                    OnEditorActionListener { v, actionId, event ->
                        if (actionId == EditorInfo.IME_ACTION_DONE || event != null && event.keyCode === KeyEvent.KEYCODE_ENTER) {
                            // 如果IME_ACTION_DONE被触发，或者用户按下了回车键
                            // 这里模拟点击confirmButton
                            confirmButton.performClick()
                            return@OnEditorActionListener true // 消费掉这个事件，防止软键盘响应其他动作
                        }
                        false
                    }
                )


                confirmButton.setOnClickListener {
                    val word = wordTextView?.text.toString()
                    val input = inputEditText?.text.toString()

                    if (word.equals(input.trim(), ignoreCase = true) || input.trim().contains(" ")) {
                        // 如果输入正确，关闭悬浮窗口
                        EasyFloat.dismiss("float_word_input",true)
                    } else {
                        // 如果输入不正确，清空 EditText 并添加晃动动画
                        inputEditText?.text?.clear()

                        val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
                        inputEditText?.startAnimation(shake)
                    }
                }

                val closeButton: ImageView = view.findViewById<ImageView?>(R.id.close_button)
                closeButton.setOnClickListener{
                    EasyFloat.dismiss("float_word_input",true)
                }
            }) // 设置悬浮窗布局
            .setShowPattern(ShowPattern.ALL_TIME) // 设置显示模式 (例如始终显示)
            .setGravity(Gravity.CENTER) // 设置悬浮窗位置 (例如居中)
            .setDragEnable(false)
            .hasEditText(true)
            .setDisplayHeight { context -> DisplayUtils.rejectedNavHeight(context) }
            // ... 设置其他参数 (例如大小、拖动行为等)
            .show()
    }
    override fun onDestroy() {
        super.onDestroy()
    }

}