package com.ead.project.dreamer.data.utils

import androidx.recyclerview.widget.DiffUtil

open class DreamerAsyncDiffUtil <T : Any>: DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return try {
            return if (oldItem is DiffUtilEquality) {
                (oldItem as DiffUtilEquality).equalsHeader(newItem)
            } else
                true
        }
        catch (e : Exception) {
            false
        }
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return try {
            return if (oldItem is DiffUtilEquality) {
                return (oldItem as DiffUtilEquality).equalsContent(newItem)
            }
            else
                true
        }
        catch (e : Exception) {
            false
        }
    }
}