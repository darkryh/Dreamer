package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.app.data.server.Server
import com.ead.project.dreamer.app.model.scraper.AnimeBaseScrap
import com.ead.project.dreamer.app.model.scraper.AnimeProfileScrap
import com.ead.project.dreamer.app.model.scraper.ChapterHomeScrap
import com.ead.project.dreamer.app.model.scraper.ChapterScrap
import com.ead.project.dreamer.app.model.scraper.NewsItemScrap
import com.ead.project.dreamer.app.model.scraper.NewsItemWebScrap
import com.ead.project.dreamer.domain.ChapterUseCase
import com.ead.project.dreamer.domain.DirectoryUseCase
import com.ead.project.dreamer.domain.HomeUseCase
import com.ead.project.dreamer.domain.NewsUseCase
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.ead.project.dreamer.domain.ProfileUseCase
import com.ead.project.dreamer.domain.ServerUseCase
import com.google.gson.Gson
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
    private val profileUseCase: ProfileUseCase,
    private val gson: Gson,
    preferenceUseCase: PreferenceUseCase
) : CoroutineWorker(context,workerParameters) {

    private val preferences = preferenceUseCase.preferences
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                setServerScript(serverUseCase.serverScript.fromApi())
                setAnimeBaseScrap(directoryUseCase.getDirectoryScrap.fromApi())
                setAnimeProfileScrap(profileUseCase.getProfileScrap.fromApi())
                setChapterHomeScrap(homeUseCase.getHomeScrap.fromApi())
                setChapterScrap(chapterUseCase.getChapterScrap.fromApi())
                setNewsItemScrap(newsUseCase.getNewsItemScrap.fromApi())
                setNewsItemWebScrap(newsUseCase.getNewsItemWebScrap.fromApi())

                Result.success()
            } catch (ex: IOException) {
                ex.printStackTrace()
                Result.failure()
            }
        }
    }

    private suspend fun setServerScript(serverCode : String) {
        preferences.set(Server.PREFERENCE_SERVER_SCRIPT,serverCode)
    }

    private suspend fun setAnimeBaseScrap(animeBaseScrap: AnimeBaseScrap) {
        preferences.set(AnimeBaseScrap.INSTANCE,gson.toJson(animeBaseScrap))
    }

    private suspend fun setAnimeProfileScrap(animeProfileScrap: AnimeProfileScrap) {
        preferences.set(AnimeProfileScrap.INSTANCE,gson.toJson(animeProfileScrap))
    }

    private suspend fun setChapterHomeScrap(chapterHomeScrap: ChapterHomeScrap) {
        preferences.set(ChapterHomeScrap.INSTANCE,gson.toJson(chapterHomeScrap))
    }

    private suspend fun setChapterScrap(chapterScrap: ChapterScrap) {
        preferences.set(ChapterScrap.INSTANCE,gson.toJson(chapterScrap))
    }

    private suspend fun setNewsItemScrap(newsItemScrap: NewsItemScrap) {
        preferences.set(NewsItemScrap.INSTANCE,gson.toJson(newsItemScrap))
    }

    private suspend fun setNewsItemWebScrap(newsItemWebScrap: NewsItemWebScrap) {
        preferences.set(NewsItemWebScrap.INSTANCE,gson.toJson(newsItemWebScrap))
    }
}