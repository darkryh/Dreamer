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
            is ChaptersCachingWorker -> {
                instance.repository = repository
                instance.webProvider = webProvider
            }
            is DirectoryWorker -> {
                instance.repository = repository
                instance.webProvider = webProvider
            }
            is FixerChaptersCachingWorker -> {
                instance.repository = repository
                instance.webProvider = webProvider
            }
            is FixerProfileCachingWorker -> {
                instance.repository = repository
                instance.webProvider = webProvider
            }
            is HomeWorker -> {
                instance.repository = repository
                instance.webProvider = webProvider
                instance.notifier = notifier
            }
            is NewContentWorker -> {
                instance.repository = repository
            }
            is NewsWorker ->{
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
            is ScrapperWorker -> {
                instance.repository = repository
            }
            is UpdateReleasesWorker -> {
                instance.repository = repository
                instance.webProvider = webProvider
            }
        }

        return instance
    }
}