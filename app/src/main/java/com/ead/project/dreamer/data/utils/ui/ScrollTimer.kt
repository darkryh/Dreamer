package com.ead.project.dreamer.data.utils.ui

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

open class ScrollTimer (
    private val layoutManager: LinearLayoutManager,
    private val adapter : RecyclerView.Adapter<*>,
    private val rcvTarget : RecyclerView,
    private var isFirstTime : Boolean) : TimerTask() {
    override fun run() {
        if (layoutManager.findLastCompletelyVisibleItemPosition() < adapter.itemCount-1) {
            layoutManager.smoothScrollToPosition(
                rcvTarget,
                RecyclerView.State(),
                layoutManager.findLastCompletelyVisibleItemPosition() + 1)
        }
        else {
            layoutManager.smoothScrollToPosition(
                rcvTarget,
                RecyclerView.State(),
                0)
        }
        isFirstTime = false
    }
}