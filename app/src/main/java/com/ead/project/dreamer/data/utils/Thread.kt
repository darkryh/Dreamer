package com.ead.project.dreamer.data.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.lang.Exception
import kotlin.concurrent.thread

object Thread {

    private val handler = Handler(Looper.getMainLooper())

    fun runInMs(task: () -> Unit, ms: Long) {
        try {
            handler.postDelayed(task,ms)
        } catch (ex : Exception) {
            Log.e("error", "runInMs: $ex")
        }
    }

    fun launch(task: () -> Unit) {
        try {
            thread { task() }
        } catch (ex : Exception) {
            Log.e("error", "execute: $ex")
        }
    }

    fun onUi(action: () -> Unit) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            handler.post(action)
        } else {
            action.invoke()
        }
    }
}