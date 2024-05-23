package com.solooxy.popupword.interfaces

import android.content.Context
import android.view.View
import android.view.ViewTreeObserver

interface SoftKeyboardListener {
    fun onSoftKeyboardShown(isVisible: Boolean)
}

// 注意：下面的代码仅提供思路，并非直接可运行代码，实际应用中需结合具体场景调整。
class KeyboardStateMonitor(private val context: Context, private val callback: (Boolean) -> Unit) {

    private var layoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var isMonitoring = false
    private var rootView: View? = null


}


