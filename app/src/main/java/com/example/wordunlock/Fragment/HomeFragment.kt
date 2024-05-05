package com.example.wordunlock.Fragment

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wordunlock.R
import com.example.wordunlock.adapters.HomeAdapter
import com.example.wordunlock.adapters.JsonFileListAdapter
import com.example.wordunlock.interfaces.SelectedPositionsProvider
import com.example.wordunlock.models.JsonFileItem
import com.google.gson.Gson
import android.view.MenuInflater as MenuInflater1


class HomeFragment : Fragment(), SelectedPositionsProvider{
    private lateinit var recyclerView: RecyclerView
    private lateinit var homeAdapter: HomeAdapter
    private var selectedPositions = mutableSetOf<Int>()
    private var selectedPositionsProvider: SelectedPositionsProvider? = null
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
        homeAdapter = HomeAdapter( files)
        recyclerView.adapter = homeAdapter
        // 当数据加载完成或更新时调用
        homeAdapter.notifyDataSetChanged()

        restoreSelectedPositions()
        selectedPositionsProvider = this
    }

    override fun onPause() {
        super.onPause()
        // 保存多选状态
        saveSelectedPositions()
    }

    private fun restoreSelectedPositions() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val positionsJson = preferences.getString("selected_positions", "[]")
        if (positionsJson != null) {
            if (positionsJson.isNotBlank()) {
                val restoredPositions = Gson().fromJson(positionsJson, Array<Int>::class.java).toMutableSet()
                restoredPositions.forEach { position ->
                    if (position in homeAdapter.dataList.indices) {
                        homeAdapter.dataList[position].isChecked = true
                        homeAdapter.notifyItemChanged(position)
                    }
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
        val allItemsShouldBeChecked = !homeAdapter.dataList.any { it.isChecked }
        homeAdapter.dataList.forEachIndexed { index, item ->
            item.isChecked = allItemsShouldBeChecked
            selectedPositions = if (allItemsShouldBeChecked) homeAdapter.dataList.indices.toMutableSet() else mutableSetOf()
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
    override fun getSelectedPositions(): Set<Int> {
        return selectedPositions.toSet()
    }

    override fun setSelectedPositions(positions: Set<Int>) {
        this.selectedPositions = positions.toMutableSet()
    }


        companion object {
        @JvmStatic
        fun newInstance(): Fragment? {
            return HomeFragment()
        }
    }
}






