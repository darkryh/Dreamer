package com.ead.project.dreamer.domain.downloads

import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import com.ead.project.dreamer.app.AppReceiver
import com.ead.project.dreamer.app.data.util.Apk
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.Update
import javax.inject.Inject

class EnqueueDownload @Inject constructor(
    private val generateDownload: GenerateDownload,
    private val configureDownload: ConfigureDownload,
    private val appReceiver: AppReceiver,
    private val context: Context
) {

    operator fun invoke(chapter : Chapter?,url : String) : Long {
        registerReceiver(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        return generateDownload(configureDownload(chapter?:return -1,url),chapter.id)
    }

    operator fun invoke(update : Update, url: String) : Long {
        return generateDownload(configureDownload(update, url))
    }

    private fun registerReceiver(action : String) {
        val intentFilter = IntentFilter(action)
        AppReceiver.register(context,appReceiver,intentFilter)
    }
}