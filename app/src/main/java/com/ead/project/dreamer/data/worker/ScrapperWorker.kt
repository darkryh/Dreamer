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
    private val serverUseCase: ServerUseCase,
    private val chapterUseCase: ChapterUseCase,
    private val directoryUseCase: DirectoryUseCase,
    private val homeUseCase: HomeUseCase,
    private val newsUseCase: NewsUseCase,
    private val profileUseCase: ProfileUseCase
) : CoroutineWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {

                DreamerWebView.setServerScript(serverUseCase.getServerScript())
                AnimeBaseScrap.set(directoryUseCase.getDirectoryScrap.fromApi())
                AnimeProfileScrap.set(profileUseCase.getProfileScrap.fromApi())
                ChapterHomeScrap.set(homeUseCase.getHomeScrap.fromApi())
                ChapterScrap.set(chapterUseCase.getChapterScrap.fromApi())
                NewsItemScrap.set(newsUseCase.getNewsItemScrap.fromApi())
                NewsItemWebScrap.set(newsUseCase.getNewsItemWebScrap.fromApi())

                Result.success()
            } catch (ex: IOException) {
                ex.printStackTrace()
                Result.failure()
            }
        }
    }
}