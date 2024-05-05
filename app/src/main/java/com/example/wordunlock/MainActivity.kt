package com.example.wordunlock


import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.accessibility.AccessibilityManager
import android.widget.AbsListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wordunlock.adapters.JsonFileListAdapter
import com.example.wordunlock.models.WordList
import com.example.wordunlock.ui.theme.WordUnlockTheme
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var wordLists: MutableList<WordList>
    private lateinit var jsonFileListAdapter: JsonFileListAdapter


    companion object {
        private const val REQUEST_CODE_OVERLAY_PERMISSION = 100
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.frame_layout_container)
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        setupActionBarWithNavController(navController)
        bottomNavView.setupWithNavController(navController)
        /*val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)*/

        /*recyclerView = this.findViewById(R.id.recycler_view)

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val fileNames = getRawFileNames(this)
        val assetManager = assets
        val files = assetManager.list("json")
        jsonFileListAdapter = JsonFileListAdapter(this, files)
        recyclerView.adapter = jsonFileListAdapter*/

        // 检查并请求 SYSTEM_ALERT_WINDOW 权限
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"))
            startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION)
        }
    }


    // 检查辅助功能是否开启
    private fun Context.isAccessibilityServiceEnabled(): Boolean {
        val am = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
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

    fun getRawFileNames(context: Context): List<String> {
        val rawClass = R.raw::class.java
        return rawClass.fields.map { it.name }
    }

    //}
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout_container)
        return if (currentFragment != null && currentFragment.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
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
                .setPositiveButton("前往设置") { dialog, _ ->
                    dialog.dismiss()
                    openAccessibilitySettings()
                }
                .setCancelable(false) // 避免用户点击外部关闭而不做处理
                .show()
        } else {
            // 辅助功能已开启，执行其他逻辑
            //setContentView(R.layout.activity_main)
            val navController = findNavController(R.id.frame_layout_container)
            val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            bottomNavView.setupWithNavController(navController)
            // val fileList = assets.list("raw")
            /*val raw_read_string = applicationContext.resources.openRawResource(R.raw.sixlevel).bufferedReader().use{
                it.readText()
            }*/
            /*recyclerView = this.findViewById(R.id.recycler_view)
            val layoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutManager

            val fileNames = getRawFileNames(this)
            jsonFileListAdapter = JsonFileListAdapter(this, fileNames)
            recyclerView.adapter = jsonFileListAdapter*/
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