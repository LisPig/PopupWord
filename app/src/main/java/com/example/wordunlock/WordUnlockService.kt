package com.example.wordunlock

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import com.example.wordunlock.models.WordDefinition
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random

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
        val homeIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val resolveInfo = packageManager.resolveActivity(homeIntent, PackageManager.MATCH_DEFAULT_ONLY)
        val launcherPackageName = resolveInfo?.activityInfo?.packageName
        return launcherPackageName != null && packageName == launcherPackageName
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
            val randomWordDefinition = getRandomWordDefinitionFromJson(this)
            randomWordDefinition?.let {
                val word = it.word
                var definition = it.definition

                intent.putExtra("word", word) // 传递单词数据
                intent.putExtra("definition", definition) // 传递单词定义
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startService(intent)
            }
        }
    }


    fun getRandomWordDefinitionFromJson(context: Context): WordDefinition? {
        val resourceId = context.resources.getIdentifier("sixlevel", "raw", context.packageName)
        if (resourceId == 0) {
            // 文件不存在或命名错误
            return null
        }

        val inputStream = context.resources.openRawResource(resourceId)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val sb = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            sb.append(line)
        }

        val gson = Gson()
        val type = object : TypeToken<List<WordDefinition>>() {}.type
        val wordDefinitions: List<WordDefinition> = gson.fromJson(sb.toString(), type)

        val random = java.util.Random()
        return wordDefinitions.randomOrNull() ?: return null
    }
}