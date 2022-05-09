package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.data.network.WebProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

class HomeWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context,workerParameters) {

    lateinit var repository: AnimeRepository
    lateinit var webProvider: WebProvider

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val chapterHomeList = repository.getChaptersHome()

                if (chapterHomeList.isEmpty()) {
                    val homeData = async { webProvider.getChaptersHome(ChapterHome.fake()) }
                    homeData.await().apply {
                        repository.insertAllChaptersHome(this)
                        Result.success()
                    }
                }
                else {
                    val localChapter = repository.getChaptersHome().last()
                    val homeData = async { webProvider.getChaptersHome(localChapter) }
                    homeData.await().apply {
                        repository.updateHome(this)
                        Result.success()
                    }
                }
                Result.success()
            }
            catch (ex : IOException) {
                ex.printStackTrace()
                Result.failure()
            }
        }
    }
}