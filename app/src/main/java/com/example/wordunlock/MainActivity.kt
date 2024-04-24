package com.example.wordunlock

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.wordunlock.ui.theme.WordUnlockTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val REQUEST_CODE_OVERLAY_PERMISSION = 100
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 检查辅助功能是否开启
        if (!isAccessibilityServiceEnabled()) {
            // 弹出对话框提示用户开启辅助功能
            AlertDialog.Builder(this)
                .setTitle("开启辅助功能")
                .setMessage("请开启辅助功能以使用此应用")
                .setPositiveButton("前往设置") { dialog, _ ->
                    dialog.dismiss()
                    openAccessibilitySettings()
                }
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                    finish() // 关闭应用
                }
                .show()
        } else {
            // 辅助功能已开启，执行其他逻辑


        }

        // 检查并请求 SYSTEM_ALERT_WINDOW 权限
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"))
            startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION)
        }
    }

    // 检查辅助功能是否开启
    private fun Context.isAccessibilityServiceEnabled(): Boolean {
        val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        return enabledServices.any { it.resolveInfo.serviceInfo.name == WordUnlockService::class.java.name }
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    // 处理权限请求结果 (需要在 Activity 中添加)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
            // 检查权限是否已授予
            if (Settings.canDrawOverlays(this)) {
                // 权限已授予，执行相关操作
                val intent = Intent(this, WordUnlockForegroundService::class.java)
                startService(intent)
            } else {
                // 权限未授予，提示用户或执行其他操作
                System.out.println("fail")
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WordUnlockTheme {
        Greeting("Android")
    }
}