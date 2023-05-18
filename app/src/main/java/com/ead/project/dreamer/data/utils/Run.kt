package com.ead.project.dreamer.data.utils

object Run {

    fun catching(task: () -> Unit) {
        try {
            task()
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }
}