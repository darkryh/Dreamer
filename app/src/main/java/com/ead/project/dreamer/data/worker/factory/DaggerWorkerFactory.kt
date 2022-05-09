package com.ead.project.dreamer.data.worker.factory

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.data.utils.receiver.DreamerNotifier
import com.ead.project.dreamer.data.worker.*

class DaggerWorkerFactory  (
    private val repository: AnimeRepository,
    private val webProvider: WebProvider,
    private val notifier: DreamerNotifier
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val workerClass = Class.forName(workerClassName)
            .asSubclass(CoroutineWorker::class.java)
        val constructor = workerClass
            .getDeclaredConstructor(Context::class.java, WorkerParameters::class.java)
        val instance = constructor.newInstance(appContext, workerParameters)

        when (instance) {
            is HomeWorker -> {
                instance.repository = repository
                instance.webProvider = webProvider
            }
            is DirectoryWorker -> {
                instance.repository = repository
                instance.webProvider = webProvider
            }
            is ProfileCachingWorker -> {
                instance.repository = repository
                instance.webProvider = webProvider
            }
            is ProfileRepositoryWorker -> {
                instance.repository = repository
                instance.webProvider = webProvider
            }
            is ChaptersCachingWorker -> {
                instance.repository = repository
                instance.webProvider = webProvider
            }
            is NewContentWorker -> {
                instance.repository = repository
            }
            is UpdateReleasesWorker -> {
                instance.repository = repository
                instance.webProvider = webProvider
            }
            is NotificationWorker -> {
                instance.repository = repository
                instance.webProvider = webProvider
                instance.notifier = notifier
            }
            // optionally, handle other workers
        }
        return instance
    }
}