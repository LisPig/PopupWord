package com.example.wordunlock

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo

class WordUnlockService : AccessibilityService() {

    /*override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }*/

    private var isLockScreenShown = false
    private var currentActivity: Activity? = null
    private var isOpenActivity = false

    // 在 WordUnlockService 中注册事件类型
    override fun onServiceConnected() {
        val info = AccessibilityServiceInfo()

        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        //info.packageNames = arrayOf(packageName)
       // info.canRetrieveWindowContent = true
        serviceInfo = info
    }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        /*val rootNode = rootInActiveWindow // 获取当前窗口的根节点

        if (event != null) {
            if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                isLockScreenShown = isLockScreen(rootNode)
            } else if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
                // 检查当前应用是否是桌面
                if (isHomeScreen(rootNode)) {
                    // 添加延迟或标志，避免重复触发
                    // ...
                    showWordInputActivity()
                }
            }
        }*/
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName.toString()
            if (isLauncherPackage(packageName) && !isOpenActivity) {
                showWordInputActivity()
                isOpenActivity = true
            }else{
                currentActivity?.finish()
                currentActivity = null
            }
        }
    }

    private fun isLauncherPackage(packageName: String): Boolean {
        // 获取设备的 Launcher 应用的包名
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        val launcherPackageName = resolveInfo?.activityInfo?.packageName

        // 检查当前应用的包名是否是 Launcher 应用的包名
        return packageName == launcherPackageName
    }

    private fun isLockScreen(node: AccessibilityNodeInfo?): Boolean {
        if (node == null) return false
        // 使用更通用的方法判断锁屏界面
        // 例如，检查窗口标题、特定视图是否存在等
        // ... 添加您的判断逻辑
        return false // 替换为您的判断结果
    }

    private fun isHomeScreen(node: AccessibilityNodeInfo?): Boolean {
        if (node == null) return false
        // 判断当前界面是否是桌面
        // 例如，检查 launcher 应用的包名或特定视图是否存在
        // ... 添加您的判断逻辑
        return false // 替换为您的判断结果
    }

    override fun onInterrupt() {

    }

    private fun showWordInputActivity() {
        if (currentActivity == null) {
            val intent = Intent(this, WordUnlockForegroundService::class.java)
            intent.putExtra("word", "apple") // 传递单词数据
            intent.putExtra("definition", "苹果") // 传递单词定义
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startService(intent)
        }
    }
}