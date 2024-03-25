package ir.saltech.answersheet.intf.listener

import android.view.View
import android.view.ViewGroup

interface CollapseBarChangedListener {
    fun onClosed(v: View?, parent: ViewGroup?)

    fun onCollapsed(v: View?, parent: ViewGroup?)
    fun onFullscreen(v: View?, parent: ViewGroup?)
    fun onRestored(v: View?, parent: ViewGroup?)
}
