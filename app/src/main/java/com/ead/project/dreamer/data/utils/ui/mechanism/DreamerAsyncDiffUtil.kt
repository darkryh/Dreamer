package com.ead.project.dreamer.data.utils.ui.mechanism

import androidx.recyclerview.widget.DiffUtil

open class DreamerAsyncDiffUtil <T : Any>: DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return try {
            return if (oldItem is EqualsDiffUtil) {
                (oldItem as EqualsDiffUtil).equalsHeader(newItem)
            } else true
        }
        catch (e : Exception) { false }
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return try {
            return if (oldItem is EqualsDiffUtil) {
                return (oldItem as EqualsDiffUtil).equalsContent(newItem)
            }
            else true
        }
        catch (e : Exception) { false }
    }
}