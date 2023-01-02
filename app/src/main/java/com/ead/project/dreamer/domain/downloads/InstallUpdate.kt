package com.ead.project.dreamer.domain.downloads

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import com.ead.project.dreamer.data.utils.receiver.InstallerReceiver
import javax.inject.Inject

class InstallUpdate @Inject constructor(private val context: Context) {

    private var broadcastReceiver : BroadcastReceiver?= null

    operator fun invoke() {
        broadcastReceiver = InstallerReceiver()
        registerReceiver()
    }

    private fun registerReceiver() =
        try { context.registerReceiver(broadcastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        } catch (e : IllegalArgumentException) { e.printStackTrace() }
}