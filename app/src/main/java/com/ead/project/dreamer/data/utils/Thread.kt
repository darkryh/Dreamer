package com.ead.project.dreamer.data.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.lang.Exception
import kotlin.concurrent.thread

object Thread {

    private val handler = Handler(Looper.getMainLooper())

    fun executeIn(ms: Long,task: () -> Unit) {
        try {
            handler.postDelayed(task,ms)
        } catch (ex : Exception) {
            Log.e("error", "runInMs: $ex")
        }
    }

    fun onWebTimeout(task: () -> Unit) {
        executeIn(10000,task)
    }

    fun onCasting(task: () -> Unit) {
        executeIn(1000,task)
    }

    fun onClickEffect(task: () -> Unit) {
        executeIn(175,task)
    }

    fun runInAWhile(task: () -> Unit) {
        executeIn(5000,task)
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