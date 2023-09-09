package com.ead.project.dreamer.app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CastMediaService : Service() {

    companion object {
        private var isRunning = false

        fun isRunning() : Boolean = isRunning

        fun setIsRunning(value : Boolean) {
            isRunning = value
        }
    }
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        setIsRunning(true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        return START_STICKY
    }

    override fun onDestroy() {
        setIsRunning(false)
        super.onDestroy()
    }
}