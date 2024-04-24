package com.example.wordunlock

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry


class FloatingWordInputService : Service(),LifecycleOwner {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var lifecycleRegistry: LifecycleRegistry

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        floatingView = createFloatingView()

        val params = createWindowParams()

        windowManager.addView(floatingView, params)
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    private fun createFloatingView(): View {
        // 创建你的自定义悬浮窗视图
        // 例如，你可以使用Compose来创建一个悬浮窗
        return ComposeView(this).apply {
            setContent {
                WordInputView()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun WordInputView() {
        var userInput by remember { mutableStateOf("") }
        val context = LocalContext.current
        val view = LocalView.current

        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
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
                modifier = Modifier.fillMaxWidth().onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

    private fun createWindowParams(): WindowManager.LayoutParams {
        // 设置悬浮窗的参数
        return WindowManager.LayoutParams().apply {
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.CENTER
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            format = PixelFormat.TRANSLUCENT
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        windowManager.removeView(floatingView)
    }
}


