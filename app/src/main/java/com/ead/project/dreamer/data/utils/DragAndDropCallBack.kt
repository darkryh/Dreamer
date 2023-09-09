package com.ead.project.dreamer.data.utils

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

object DragAndDropCallBack : ItemTouchHelper.SimpleCallback(
ItemTouchHelper.UP or ItemTouchHelper.DOWN,0) {

    var itemList : MutableList<String> = mutableListOf()
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {

        val fromPosition = viewHolder.bindingAdapterPosition
        val toPosition = target.bindingAdapterPosition

        Collections.swap(itemList,fromPosition,toPosition)

        recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)

        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) { return }

}