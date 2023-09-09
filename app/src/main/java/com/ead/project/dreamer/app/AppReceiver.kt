package com.ead.project.dreamer.app

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.ead.project.dreamer.app.data.downloads.DownloadHandler
import com.ead.project.dreamer.app.data.downloads.DownloadStore
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.utils.Run
import com.ead.project.dreamer.domain.PreferenceUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppReceiver : BroadcastReceiver() {

    @Inject lateinit var repository: AnimeRepository
    @Inject lateinit var downloadStore : DownloadStore
    @Inject lateinit var preferenceUseCase: PreferenceUseCase

    override fun onReceive(context: Context, intent: Intent) {
        Run.catching {
            when(intent.action) {
                DownloadManager.ACTION_DOWNLOAD_COMPLETE -> {
                    DownloadHandler.repository = repository
                    DownloadHandler.downloadStore = downloadStore
                    DownloadHandler.playerPreferences = preferenceUseCase.playerPreferences
                    DownloadHandler.on(intent)
                }
                Intent.ACTION_PACKAGE_REPLACED -> {
                    val updateFile = preferenceUseCase.appBuildPreferences.getLastVersionFile()
                    //Apk.install(context,updateFile.path)
                }
            }
        }
    }

    companion object {

        fun register(context: Context,appReceiver: AppReceiver,intentFilter: IntentFilter) {
            Run.catching {
                ContextCompat.registerReceiver(
                    context,
                    appReceiver,
                    intentFilter,
                    ContextCompat.RECEIVER_NOT_EXPORTED
                )
            }
        }

        fun unregister(context: Context,appReceiver: AppReceiver) {
            Run.catching {
                context.unregisterReceiver(appReceiver)
            }
        }
    }
}