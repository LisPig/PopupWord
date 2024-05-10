package com.example.wordunlock

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.InputType
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.ViewTreeObserver
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
import androidx.core.content.ContextCompat
import com.example.wordunlock.db.AppDatabase
import com.example.wordunlock.models.FavoriteItem
import com.example.wordunlock.models.WordDefinition
import com.example.wordunlock.util.SoftKeyboardListener
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.interfaces.OnInvokeView
import com.lzf.easyfloat.utils.DisplayUtils
import com.lzf.easyfloat.utils.InputMethodUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


class WordUnlockForegroundService : IntentService("WordUnlockService"), TextToSpeech.OnInitListener {

    companion object {
        private const val NOTIFICATION_ID = 100
        private const val CHANNEL_ID = "word_unlock_channel"
    }
    private lateinit var db: AppDatabase
    private var keyboardListener: SoftKeyboardListener? = null
    private var wordTextView: TextView? = null
    private var soundMarkTextVIew: TextView? = null
    private var starButton: ImageView? = null
    private var commentTextView: TextView? = null
    private var inputEditText: EditText? = null
    private var tts: TextToSpeech? = null
    private var isStarred = true // 用于跟踪星星是否被选中

    private val ACTION_ADD_FAVORITE = "ACTION_ADD_FAVORITE"
    private  val ACTION_REMOVE_FAVORITE = "ACTION_REMOVE_FAVORITE"
    private  val EXTRA_FAVORITE_ITEM = "EXTRA_FAVORITE_ITEM"
    override fun onBind(intent: Intent?): IBinder? = null


    @SuppressLint("ServiceCast")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        db = (application as WordUnlockApplication).db
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
        // 启动前台服务
        startForeground(NOTIFICATION_ID, notification)
        val wordDefinition = intent?.getParcelableExtra<WordDefinition>("wordDefinition")
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

    private var floatViewWidth = 0
    private var floatViewHeight = 0
    private  fun showFloatingWindow(wordDefinition: WordDefinition?) {
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
                val viewTreeObserver = view.viewTreeObserver
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        // 视图已测量完成，获取浮窗的尺寸
                        floatViewWidth = view.width
                        floatViewHeight = view.height
                        view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        // 此时可以计算并设置动态位置
                        setLocationAfterMeasurements()
                    }
                })
                inputEditText?.setOnClickListener {
                    InputMethodUtils.openInputMethod(inputEditText,tag="float_word_input")
                    //EasyFloat.updateFloat("float_word_input",-1,-1,-1,-2)
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

                starButton = view.findViewById<ImageView>(R.id.star_button)
                starButton?.setOnClickListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        toggleStarColorAndSaveState(wordDefinition)
                    }
                }


            }) // 设置悬浮窗布局
            .setShowPattern(ShowPattern.ALL_TIME) // 设置显示模式 (例如始终显示)
            .setGravity(Gravity.CENTER) // 设置悬浮窗位置 (例如居中)
            .setDragEnable(false)
            .hasEditText(true)
            .setDragEnable(true)
            .setDisplayHeight { context -> DisplayUtils.rejectedNavHeight(context) }
            .show()
    }

    private fun setLocationAfterMeasurements() {
        val displayMetrics = DisplayMetrics()
        val windowManager: WindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        // 计算中间偏上的坐标
        val yOffset = displayMetrics.heightPixels * 0.2f // 假设偏上20%的高度
        val centerX = (displayMetrics.widthPixels - floatViewWidth) / 2

        EasyFloat.updateFloat("float_word_input", centerX, yOffset.toInt())
    }

    private suspend fun toggleStarColorAndSaveState(wordDefinition: WordDefinition?) {
        val favoriteItem = FavoriteItem(null, wordDefinition?.word, wordDefinition?.uk, wordDefinition?.us, wordDefinition?.definition)
        if (isStarred) {
            // 在协程中调用 saveFavoriteItem
            withContext(Dispatchers.IO) {
                saveFavoriteItem(item = favoriteItem)
            }
            val goldenDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_star_gold_24)
            starButton?.setImageDrawable(goldenDrawable)
        } else {
            val normalDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_star_24)
            starButton?.setImageDrawable(normalDrawable)
            withContext(Dispatchers.IO) {
                removeFavoriteItem(favoriteItem?.word)
            }
        }
        isStarred = !isStarred // 切换状态
    }
    private suspend fun saveFavoriteItem(item: FavoriteItem) {
        withContext(Dispatchers.IO) {
            db.favoriteItemDao().insert(item)
        }
    }

    private suspend fun removeFavoriteItem(item: String?) {
        withContext(Dispatchers.IO) {
            if (item != null) {
                db.favoriteItemDao().delete(item)
            }
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        intent?.let { intent ->
            when (intent.action) {
                ACTION_ADD_FAVORITE -> {
                    val newItem = intent.getParcelableExtra<FavoriteItem>(EXTRA_FAVORITE_ITEM)
                    if (newItem != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            saveFavoriteItem(newItem)
                        }
                    } else {

                    }
                }
                ACTION_REMOVE_FAVORITE -> {
                    val itemToRemove = intent.getStringExtra(EXTRA_FAVORITE_ITEM)
                    if (itemToRemove != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            removeFavoriteItem(itemToRemove)
                        }
                    } else {

                    }
                }
                else -> Log.w("ColloctionOpertion", "Unknown action: ${intent.action}")
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        // 移除 SoftKeyboardListener
        keyboardListener?.removeKeyboardListener()
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