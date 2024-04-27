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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wordunlock.adapters.JsonFileListAdapter
import com.example.wordunlock.adapters.WordListAdapter
import com.example.wordunlock.models.WordList
import com.example.wordunlock.ui.theme.WordUnlockTheme

class MainActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var wordLists: MutableList<WordList>
    private lateinit var jsonFileListAdapter: JsonFileListAdapter

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
            setContentView(R.layout.activity_main)

            recyclerView = this.findViewById(R.id.recycler_view)
            val layoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutManager

            val fileNames = getRawFileNames(this)
            jsonFileListAdapter = JsonFileListAdapter(this, fileNames)
            recyclerView.adapter = jsonFileListAdapter

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

    // 示例：从 JSON 文件加载词库数据
   // private fun getRawFileNames() {

        fun getRawFileNames(context: Context): List<String> {
            val rawClass = R.raw::class.java
            return rawClass.fields.map { it.name }
        }
    //}

    private fun setupAdapter() {
        val adapter = WordListAdapter(this, wordLists) { wordList, isSelected ->
            wordList.isSelected = isSelected
            recyclerView.adapter?.notifyItemChanged(wordLists.indexOf(wordList))

            // 如果需要持久化存储用户选择，可以在这里保存到SharedPreferences等
        }
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        // 在Activity变为可见时检查辅助功能状态
        if (!isAccessibilityServiceEnabled()) {
            // 如果辅助功能未开启，在这里处理逻辑，比如弹窗提示或直接finish()
            // 注意：此处处理逻辑需谨慎，避免无限循环或用户体验不佳的情况
            // 示例代码可能需要根据实际情况调整
            AlertDialog.Builder(this)
                .setTitle("辅助功能未开启")
                .setMessage("请确保辅助功能已开启以正常使用应用。")
                .setPositiveButton("确定"){ _, _ ->
                    // 这里可以考虑直接finish()或者引导用户去设置，但需避免重复弹窗
                }
                .setCancelable(false) // 避免用户点击外部关闭而不做处理
                .show()
        } else {
            // 辅助功能已开启，执行其他逻辑
            setContentView(R.layout.activity_main)
            // val fileList = assets.list("raw")
            /*val raw_read_string = applicationContext.resources.openRawResource(R.raw.sixlevel).bufferedReader().use{
                it.readText()
            }*/
            recyclerView = this.findViewById(R.id.recycler_view)
            val layoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutManager

            val fileNames = getRawFileNames(this)
            jsonFileListAdapter = JsonFileListAdapter(this, fileNames)
            recyclerView.adapter = jsonFileListAdapter
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