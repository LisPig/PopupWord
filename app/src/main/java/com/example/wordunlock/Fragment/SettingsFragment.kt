package com.example.wordunlock.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.wordunlock.R

class SettingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
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