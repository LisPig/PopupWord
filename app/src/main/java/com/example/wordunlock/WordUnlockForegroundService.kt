package com.example.wordunlock

import android.app.*
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.InputType
import android.util.Log
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
import com.example.wordunlock.models.WordDefinition
import com.example.wordunlock.util.SoftKeyboardListener
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.interfaces.OnInvokeView
import com.lzf.easyfloat.utils.DisplayUtils
import com.lzf.easyfloat.utils.InputMethodUtils
import java.util.Locale


class WordUnlockForegroundService : Service(), TextToSpeech.OnInitListener {

    companion object {
        private const val NOTIFICATION_ID = 100
        private const val CHANNEL_ID = "word_unlock_channel"
    }

    private var keyboardListener: SoftKeyboardListener? = null
    private var wordTextView: TextView? = null
    private var soundMarkTextVIew: TextView? = null
    private var commentTextView: TextView? = null
    private var inputEditText: EditText? = null
    private var tts: TextToSpeech? = null
    override fun onBind(intent: Intent?): IBinder? = null



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        tts = TextToSpeech(this, this)


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
        val wordDefinition = intent?.getParcelableExtra<WordDefinition>("wordDefinition")
        wordDefinition?.let {
            // 使用提取的wordDefinition对象
            val word = it.word
            val uk = it.uk
            var us = it.us
            val definition = it.definition
            // ...处理word和definition
        }
        val word = intent?.getStringExtra("word") ?: "example"
        val definition = intent?.getStringExtra("definition") ?: "示例"
        // 创建并显示悬浮窗
        showFloatingWindow(wordDefinition)
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

    private fun showFloatingWindow(wordDefinition: WordDefinition?) {

        EasyFloat.with(this)
            .setTag("float_word_input")
            .setLayout(R.layout.float_word_input,OnInvokeView { view ->
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                wordTextView = view.findViewById(R.id.wordTextView)
                soundMarkTextVIew = view.findViewById(R.id.soundMarkTextView)
                commentTextView = view.findViewById(R.id.commentTextView)
                wordTextView?.text = wordDefinition?.word
                soundMarkTextVIew?.text = wordDefinition?.us
                commentTextView?.text = wordDefinition?.definition
                val confirmButton: Button = view.findViewById<Button?>(R.id.confirmButton)
                val inputEditText: EditText = view.findViewById(R.id.inputEditText)

                inputEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS

                // 为 EditText 设置 OnClickListener
                inputEditText?.setOnClickListener {
                    InputMethodUtils.openInputMethod(inputEditText)
                    EasyFloat.updateFloat("float_word_input",-1,-1,-1,-2)

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
                //播放按钮
                val playButton: ImageView = view.findViewById(R.id.play_button)
                val animatedVectorDrawable = playButton.drawable as? AnimatedVectorDrawable
                if (animatedVectorDrawable != null) {
                    animatedVectorDrawable.start()
                }
                playButton.setOnClickListener {
                    val word = wordTextView?.text.toString()
                    val animatedVectorDrawable = playButton.drawable as? AnimatedVectorDrawable
                    if (animatedVectorDrawable != null) {
                        if (animatedVectorDrawable.isRunning) {
                            animatedVectorDrawable.stop()
                            // 播放按钮被点击，暂停动画
                        } else {
                            animatedVectorDrawable.start()
                            // 暂停按钮被点击，播放动画
                            speak(word)
                        }
                    }
                    speak(word)
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

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.getDefault())
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This language is not supported")
            } else {
                // 示例：播放一段文字
                Log.d("test","Hello, this is a test.")
            }
        } else {
            Log.e("TTS", "Initialization failed!")
        }
    }

    fun speak(text: String) {
        tts?.apply {
            // 设置utteranceId用于跟踪发音完成状态
            val utteranceId = this.hashCode().toString()
            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
            }
            // 开始播放
            speak(text, TextToSpeech.QUEUE_FLUSH, params, null)
            // 可选：监听发音进度
            setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    Log.d("TTS", "Speaking started: $utteranceId")
                }

                override fun onError(utteranceId: String?) {
                    Log.e("TTS", "Error during speech: $utteranceId")
                }

                override fun onDone(utteranceId: String?) {
                    Log.d("TTS", "Speech completed: $utteranceId")
                }
            })
        }
    }


}