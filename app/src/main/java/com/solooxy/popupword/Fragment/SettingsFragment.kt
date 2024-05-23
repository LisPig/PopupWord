package com.solooxy.popupword.Fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.solooxy.popupword.R

class SettingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        var forceMode = view.findViewById<Switch>(R.id.switch_force_mode)
        forceMode.setOnClickListener {
            if (forceMode.isChecked) {
                // 切换到强制模式
                // 显示提示信息
                //弹窗提示
                val toastMessage = "已开启强制模式。关闭后弹窗将有关闭按钮，需输入正确单词即可关闭"
                AlertDialog.Builder(requireContext())
                    .setTitle("开启强制模式")
                    .setMessage("开启后弹窗将无关闭按钮，需强制输入正确单词即可关闭。")
                    .setPositiveButton("确定") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(false) // 避免用户点击外部关闭而不做处理
                    .show()
            } else {
                // 切换到普通模式
                // 隐藏提示信息
                // ...
            }
        }

        val collectionView = view.findViewById<ImageView>(R.id.image_view_favorites)
        collectionView.setOnClickListener {
            // 点击跳转到收藏夹页面
            val intent = Intent(context, FavoriteListActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(): Fragment? {
            return SettingsFragment()
        }
    }
}