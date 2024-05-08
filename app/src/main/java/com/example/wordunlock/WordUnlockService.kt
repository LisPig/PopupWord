package com.example.wordunlock

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.KeyguardManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.wordunlock.adapters.JsonFileListAdapter
import com.example.wordunlock.models.WordDefinition
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.random.Random


class WordUnlockService : AccessibilityService() {

    private var wasScreenUnlocked = false // 上次事件时屏幕是否解锁
    private var lastUnlockTime: Long = 0 // 上次解锁时间
    private var lastNonDesktopPackageName: String? = null // 上次非桌面应用的包名
    private var firstDesktopReturnHandled = false // 首次从非桌面应用返回桌面的逻辑是否已处理
    // 在 WordUnlockService 中注册事件类型
    override fun onServiceConnected() {
        val info = AccessibilityServiceInfo()

        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        serviceInfo = info


    }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        val eventType = event.eventType
        val packageName = event.packageName.toString()
        val currentTime = System.currentTimeMillis()

        // 新增逻辑：排除后台应用列表和应用抽屉
        val isRecentsOrAppDrawer = when (eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                // 这里可以根据实际情况调整，通常后台应用列表和应用抽屉会有特定的类名或包名
                // 以下为示例逻辑，您可能需要根据具体设备和ROM进行调整
                packageName.startsWith("com.android.systemui") && packageName.endsWith(".recents") ||
                        packageName.startsWith("com.android.launcher3") && event.className?.contains("AllAppsContainerView") == true
            }
            else -> false
        }
        if (isRecentsOrAppDrawer) return // 如果是后台应用列表或应用抽屉，则直接返回不处理

        // 检查是否解锁
        val isUnlockEvent = eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && isUnlockEvent(event)

        // 检查是否切换到桌面
        val isSwitchToDesktop = isLauncherPackage(packageName)
        when {
             isUnlockEvent  && isSwitchToDesktop -> {
                lastUnlockTime = currentTime // 记录解锁时间
                if (!firstDesktopReturnHandled && isSwitchToDesktop) {
                    // 如果是首次安装后首次解锁切回桌面，触发弹窗
                    showWordInputActivity()
                    firstDesktopReturnHandled = true
                }

            }
            isSwitchToDesktop && lastUnlockTime > 0 && currentTime - lastUnlockTime < 500 -> {
                // 从非桌面应用切回桌面，并且在解锁后的短时间内
                if (!firstDesktopReturnHandled || lastNonDesktopPackageName != null) {
                    // 首次或非首次（但符合解锁后短时间内）从非桌面应用返回桌面，触发弹窗
                    showWordInputActivity()
                    if (!firstDesktopReturnHandled) {
                        firstDesktopReturnHandled = true
                    }
                }
            }

            eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !isLauncherPackage(packageName) -> {
                // 记录离开桌面时的包名，用于后续判断是否是从非桌面应用返回桌面
                lastNonDesktopPackageName = packageName
            }
        }
        // 更新屏幕解锁状态
        wasScreenUnlocked = isSwitchToDesktop
    }

    private fun isUnlockEvent(event: AccessibilityEvent): Boolean {
        // 实现检查是否解锁的逻辑，例如通过KeyguardManager或检查特定事件属性
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return !keyguardManager.isKeyguardLocked
    }
    private fun isLauncherPackage(packageName: String): Boolean {
        try {
            // 获取设备的 Launcher 应用的包名
            val homeIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
            val resolveInfo = packageManager.resolveActivity(homeIntent, PackageManager.MATCH_DEFAULT_ONLY)
            val launcherPackageName = resolveInfo?.activityInfo?.packageName
            return launcherPackageName != null && packageName == launcherPackageName
        } catch (e: Exception) {
            Log.e(TAG, "Error occurred while checking for launcher package", e)
            return false // 或者根据需要处理异常情况
        }
    }

    private fun isScreenUnlocked(): Boolean {
        // 实现检查屏幕是否解锁的逻辑
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return !keyguardManager.isKeyguardLocked
    }

    private fun isKeyguardLocked(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return keyguardManager.isKeyguardLocked
    }

    override fun onInterrupt() {

    }

    private fun showWordInputActivity() {
       // if (currentActivity == null) {
            val intent = Intent(this, WordUnlockForegroundService::class.java)
            val randomWordDefinition = getRandomWordDefinitionFromJson(this)
            randomWordDefinition?.let {
                intent.putExtra("wordDefinition", it) // 传递单词数据
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startService(intent)
            }
       // }
    }


    fun getRandomWordDefinitionFromJson(context: Context): WordDefinition? {
        val myApplication = applicationContext as WordUnlockApplication
        // 获取带有文件名的列表
        val dataList = myApplication.dataList
        val selectedFiles = dataList.filter { it.isChecked }.map { it.fileName }

        // 如果没有选择的文件，返回null
        if (selectedFiles.isEmpty()) {
            return null
        }

        // 从assets/json目录中随机选择一个文件
        val randomIndex = Random.nextInt(selectedFiles.size)
        val selectedFileName = selectedFiles[randomIndex]

        // 打开并读取选中的JSON文件
        val assetManager = assets
        val inputStream: InputStream
        try {
            inputStream = assetManager.open("json/$selectedFileName")
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        val reader = BufferedReader(InputStreamReader(inputStream))
        val sb = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            sb.append(line)
        }

        val gson = Gson()
        val type = object : TypeToken<List<WordDefinition>>() {}.type
        val wordDefinitions: List<WordDefinition> = gson.fromJson(sb.toString(), type)
        inputStream.close()
        return wordDefinitions.randomOrNull() ?: return null
    }

}