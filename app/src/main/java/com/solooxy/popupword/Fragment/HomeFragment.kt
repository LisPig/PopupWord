package com.solooxy.popupword.Fragment

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.solooxy.popupword.R
import com.solooxy.popupword.WordUnlockApplication
import com.solooxy.popupword.adapters.HomeAdapter
import com.google.gson.Gson


class HomeFragment : Fragment(){
    private lateinit var recyclerView: RecyclerView
    private lateinit var homeAdapter: HomeAdapter
    private var selectedPositions = mutableSetOf<Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        val assetManager = requireContext().assets
        val files = assetManager.list("json") ?: emptyArray()
        homeAdapter = HomeAdapter(activity?.application as WordUnlockApplication, files)
        recyclerView.adapter = homeAdapter
        // 当数据加载完成或更新时调用

        homeAdapter.notifyDataSetChanged()
        restoreSelectedPositions()
        homeAdapter.setDefaultSelection()
    }

    override fun onPause() {
        super.onPause()
        // 保存多选状态
        saveSelectedPositions()
    }

    private fun restoreSelectedPositions() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val positionsJson = preferences.getString("selected_positions", "[]")
        if (positionsJson != null && positionsJson.isNotBlank()) {
            val restoredPositions = Gson().fromJson(positionsJson, Array<Int>::class.java).toMutableSet()
            selectedPositions = restoredPositions
            restoredPositions.forEach { position ->
                if (position in homeAdapter.dataList.indices) {
                    homeAdapter.dataList[position].isChecked = true
                    homeAdapter.notifyItemChanged(position)
                }
            }
        }
    }

    private fun notifyItemChanged(it: Int) {
        homeAdapter?.notifyItemChanged(it)
    }

    private fun saveSelectedPositions() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val selectedPositions = homeAdapter.dataList.mapIndexed { index, item -> if (item.isChecked) index else -1 }.filter { it >= 0 }
        val positionsJson = Gson().toJson(selectedPositions.toIntArray())
        preferences.edit().putString("selected_positions", positionsJson).apply()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_select_all -> {
                // 处理全选逻辑
                selectAllItems()
                true
            }
            R.id.action_deselect_all -> {
                // 处理取消全选逻辑
                deselectAllItems()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun selectAllItems() {
        //val allItemsShouldBeChecked = !homeAdapter.dataList.any { it.isChecked }
        homeAdapter.dataList.forEachIndexed { index, item ->
            item.isChecked = true
            homeAdapter.updateItemCheckedStatus(index, item.isChecked)
        }


    }

    private fun deselectAllItems() {
        homeAdapter.dataList.forEachIndexed { index, item ->
            item.isChecked = false
            selectedPositions.clear()
            homeAdapter.updateItemCheckedStatus(index, item.isChecked)
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(): Fragment? {
            return HomeFragment()
        }
    }
}






