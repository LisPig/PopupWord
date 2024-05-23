package com.solooxy.popupword


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
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.solooxy.popupword.adapters.JsonFileListAdapter
import com.solooxy.popupword.models.WordList
import com.solooxy.popupword.ui.theme.WordUnlockTheme
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

        //setupActionBarWithNavController(navController)
        // 修改这里，使用一个匿名的NavHostFragment的OnDestinationChangedListener
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            supportActionBar?.title = destination.label.toString()
            if (destination.id == R.id.navigation_settings) {
                // 设置ActionBar标题

                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                supportActionBar?.setDisplayShowHomeEnabled(false)
            }
        }

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
        }
        bottomNavView.setupWithNavController(navController)



        // 检查并请求 SYSTEM_ALERT_WINDOW 权限
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"))
            startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.frame_layout_container)
        return navController.navigateUp() || super.onSupportNavigateUp()
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
                val intent = Intent(this, WordUnlockService::class.java)
                startService(intent)
            } else {
                // 权限未授予，提示用户或执行其他操作
                System.out.println("fail")
            }
        }
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