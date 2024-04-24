package com.example.wordunlock // 替换为你自己的包名

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import com.lzf.easyfloat.EasyFloat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WordInputActivity : ComponentActivity() {

    private var isViewAdded = false
    private val overlayPermissionRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        /*if (!hasOverlayPermission()) {
            requestOverlayPermission()
            return
        }*/

        //setupOverlayWindow()

        // 获取单词数据 (例如从 Intent 中)
        val word = intent.getStringExtra("word") ?: "example"
        val definition = intent.getStringExtra("definition") ?: "示例"


        setContent {
            WordInputView(word, definition)
        }


        //EasyFloat.with(this).setLayout(R.layout.float_test).show()


    }

    private fun setupOverlayWindow() {
        // 设置窗口类型为悬浮窗
        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)

        // 获取屏幕尺寸
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        // 设置窗口参数
        val params = WindowManager.LayoutParams().apply {
            width = (minOf(screenWidth, screenHeight) * 0.8f).toInt() // 宽度为屏幕最小边的 80%
            height = WindowManager.LayoutParams.WRAP_CONTENT // 高度自适应
            gravity = Gravity.CENTER // 居中显示
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS // 允许悬浮窗超出屏幕边界
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }

        //windowManager.addView(window.decorView, params)
    }

    private fun hasOverlayPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.SYSTEM_ALERT_WINDOW
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestOverlayPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.SYSTEM_ALERT_WINDOW
            )
        ) {
            // 显示权限请求的解释
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.SYSTEM_ALERT_WINDOW),
                overlayPermissionRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == overlayPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupOverlayWindow()
            } else {
                // 权限被拒绝,处理相应逻辑
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 在 Activity 销毁时移除视图
        if (isViewAdded) {
            //val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.removeView(window.decorView)
            isViewAdded = false
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordInputView(word: String, definition: String) {
    var userInput by remember { mutableStateOf("") }
    val view = LocalView.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = word, style = MaterialTheme.typography.headlineMedium)
        Text(text = definition, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("请输入单词") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { view.clearFocus() }
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* 处理提交逻辑 */ }) {
            Text("提交")
        }
    }
}