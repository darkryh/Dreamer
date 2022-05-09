package com.ead.project.dreamer.data.utils

import androidx.recyclerview.widget.DiffUtil

class DreamerDiffUtil <T>(
    private val oldList: List<T>,
    private val newList: List<T>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return try {
            return if (oldList[oldItemPosition] is DiffUtilEquality) {
                (oldList[oldItemPosition] as DiffUtilEquality)
                    .equalsHeader((newList[oldItemPosition] as DiffUtilEquality))
            } else
                true
        } catch (e : Exception) {
            false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return try {
            return if (oldList[oldItemPosition] is DiffUtilEquality) {
                (oldList[oldItemPosition] as DiffUtilEquality)
                    .equalsContent((newList[oldItemPosition] as DiffUtilEquality))
            } else
                true
        } catch (e : Exception) {
            false
        }
    }
}