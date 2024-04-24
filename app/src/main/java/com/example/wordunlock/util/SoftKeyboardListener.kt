package com.example.wordunlock.util

import android.app.Activity
import android.graphics.Rect
import android.view.ViewTreeObserver

class SoftKeyboardListener(activity: Activity) {
    private val rootView = activity.window.decorView.rootView
    private var listener: ((Boolean) -> Unit)? = null
    private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val screenHeight = rootView.height
        val keypadHeight = screenHeight - r.bottom
        val isKeyboardShowing = keypadHeight > screenHeight * 0.15
        listener?.invoke(isKeyboardShowing)
    }

    init {
        rootView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }

    fun setKeyboardListener(listener: (Boolean) -> Unit) {
        this.listener = listener
    }

    fun removeKeyboardListener() {
        rootView.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
    }
}
