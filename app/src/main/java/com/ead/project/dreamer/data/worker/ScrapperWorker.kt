package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.app.model.scrapping.*
import com.ead.project.dreamer.data.network.DreamerWebView
import com.ead.project.dreamer.domain.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

@HiltWorker
class ScrapperWorker  @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val serverManager: ServerManager,
    private val chapterManager: ChapterManager,
    private val directoryManager: DirectoryManager,
    private val homeManager: HomeManager,
    private val newsManager: NewsManager,
    private val profileManager: ProfileManager
) : CoroutineWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {

                DreamerWebView.setServerScript(serverManager.getServerScript())
                AnimeBaseScrap.set(directoryManager.getDirectoryScrap.fromApi())
                AnimeProfileScrap.set(profileManager.getProfileScrap.fromApi())
                ChapterHomeScrap.set(homeManager.getHomeScrap.fromApi())
                ChapterScrap.set(chapterManager.getChapterScrap.fromApi())
                NewsItemScrap.set(newsManager.getNewsItemScrap.fromApi())
                NewsItemWebScrap.set(newsManager.getNewsItemWebScrap.fromApi())

                Result.success()
            } catch (ex: IOException) {
                ex.printStackTrace()
                Result.failure()
            }
        }
    }
}