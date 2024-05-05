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
import com.google.gson.Gson
import android.view.MenuInflater as MenuInflater1


class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var homeAdapter: HomeAdapter
    //private lateinit var jsonFileListAdapter: JsonFileListAdapter
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
        val adapter = HomeAdapter( files)
        recyclerView.adapter = adapter
        // 当数据加载完成或更新时调用
        adapter.notifyDataSetChanged()
        /*homeAdapter = HomeAdapter(
            object : (Int, Boolean?) -> Unit {
                override fun invoke(position: Int, isChecked: Boolean?) {
                    if (isChecked == true) {
                        selectedPositions.add(position)
                    } else {
                        selectedPositions.remove(position)
                    }
                    recyclerView.adapter?.notifyItemChanged(position)
                }
            },
            selectedPositions,
            files
        )
        recyclerView.adapter = homeAdapter*/

        restoreSelectedPositions()
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
                selectedPositions.clear()
                selectedPositions.addAll(restoredPositions)
                selectedPositions.forEach { recyclerView.adapter?.notifyItemChanged(it) }
            }
        }
    }

    private fun notifyItemChanged(it: Int) {
        homeAdapter?.notifyItemChanged(it)
    }

    private fun saveSelectedPositions() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor = preferences.edit()
        val positionsJson = Gson().toJson(selectedPositions.toTypedArray())
        editor.putString("selected_positions", positionsJson)
        editor.apply()
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
        val itemCount = recyclerView.adapter?.itemCount ?: return
        for (i in 0 until itemCount) {
            selectedPositions.add(i)
        }
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun deselectAllItems() {
        selectedPositions.clear()
        recyclerView.adapter?.notifyDataSetChanged()
    }



    companion object {
        @JvmStatic
        fun newInstance(): Fragment? {
            return HomeFragment()
        }
    }
}





