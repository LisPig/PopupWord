package com.example.wordunlock

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.KeyEvent
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import com.example.wordunlock.util.SoftKeyboardListener
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.interfaces.OnInvokeView
import com.lzf.easyfloat.utils.InputMethodUtils
import com.lzf.easyfloat.utils.InputMethodUtils.closedInputMethod


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

    fun updateView(word: String, definition: String) {
        // 使用保存的引用来更新 TextView
        wordTextView?.text = word
        commentTextView?.text = definition
    }

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
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

                val inputEditText: EditText = view.findViewById(R.id.inputEditText)
                //InputMethodUtils.openInputMethod(inputEditText)

                // 为 EditText 设置 OnClickListener
                inputEditText?.setOnClickListener {focusState ->
                    if (focusState.isFocused) {
                        focusState.requestFocus()
                        InputMethodUtils.openInputMethod(inputEditText)
                        /*val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)*/
                    }
                }

                val confirmButton: Button = view.findViewById(R.id.confirmButton)
                confirmButton.setOnClickListener {
                    val word = wordTextView?.text.toString()
                    val input = inputEditText?.text.toString()

                    if (word.equals(input, ignoreCase = true)) {
                        // 如果输入正确，关闭悬浮窗口
                        EasyFloat.dismiss("float_word_input",true)
                    } else {
                        // 如果输入不正确，清空 EditText 并添加晃动动画
                        inputEditText?.text?.clear()

                        val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
                        inputEditText?.startAnimation(shake)
                    }
                }
            }) // 设置悬浮窗布局
            .setShowPattern(ShowPattern.ALL_TIME) // 设置显示模式 (例如始终显示)
            .setGravity(Gravity.CENTER) // 设置悬浮窗位置 (例如居中)
            .setDragEnable(false)
            .hasEditText(true)
            // ... 设置其他参数 (例如大小、拖动行为等)
            .show()



    }



    override fun onDestroy() {
        super.onDestroy()
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun WordInputView() {
        var userInput by remember { mutableStateOf("") }
        val context = LocalContext.current
        val view = LocalView.current

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "单词：apple", style = MaterialTheme.typography.headlineMedium) // 替换为实际单词
            Text(text = "定义：苹果", style = MaterialTheme.typography.bodyMedium) // 替换为实际定义
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                label = { Text("请输入单词") },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            val inputMethodManager =
                                context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
                        }
                    }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* 处理提交逻辑 */ }) {
                Text("提交")
            }
        }
    }
}