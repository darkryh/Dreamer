package com.ead.project.dreamer.data.utils.ui.mechanism

import androidx.recyclerview.widget.DiffUtil

class DreamerDiffUtil <T>(
    private val oldList: List<T>,
    private val newList: List<T>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return try {
            return if (oldList[oldItemPosition] is EqualsDiffUtil) {
                (oldList[oldItemPosition] as EqualsDiffUtil)
                    .equalsHeader((newList[oldItemPosition] as EqualsDiffUtil))
            } else true
        } catch (e : Exception) { false }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return try {
            return if (oldList[oldItemPosition] is EqualsDiffUtil) {
                (oldList[oldItemPosition] as EqualsDiffUtil)
                    .equalsContent((newList[oldItemPosition] as EqualsDiffUtil))
            } else true
        } catch (e : Exception) { false }
    }
}