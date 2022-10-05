package com.ead.project.dreamer.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.app.model.scrapping.*
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.network.DreamerWebView
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class ScrapperWorker  @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context,workerParameters) {

    lateinit var repository: AnimeRepository

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {

                val animeBaseScrap = AnimeBaseScrap.get()
                val animeProfileScrap = AnimeProfileScrap.get()
                val chapterHomeScrap = ChapterHomeScrap.get()
                val chapterScrap = ChapterScrap.get()
                val newsItemScrap = NewsItemScrap.get()
                val newsItemWebScrap = NewsItemWebScrap.get()

                val animeBaseScrapApi = repository.getAnimeBaseScrap()
                val animeProfileScrapApi = repository.getAnimeProfileScrap()
                val chapterHomeScrapApi = repository.getChapterHomeScrap()
                val chapterScrapApi = repository.getChapterScrap()
                val newsItemScrapApi = repository.getNewsItemScrap()
                val newsItemWebScrapApi = repository.getNewsItemWebScrap()
                val serverScript = repository.getServerScript()

                if (DreamerWebView.getServerScript() != serverScript) DreamerWebView.setServerScript(serverScript)
                if (animeBaseScrap != animeBaseScrapApi) AnimeBaseScrap.set(animeBaseScrapApi)
                if (animeProfileScrap != animeProfileScrapApi) AnimeProfileScrap.set(animeProfileScrapApi)
                if (chapterHomeScrap != chapterHomeScrapApi) ChapterHomeScrap.set(chapterHomeScrapApi)
                if (chapterScrap != chapterScrapApi) ChapterScrap.set(chapterScrapApi)
                if (newsItemScrap != newsItemScrapApi) NewsItemScrap.set(newsItemScrapApi)
                if (newsItemWebScrap != newsItemWebScrapApi) NewsItemWebScrap.set(newsItemWebScrapApi)

                Result.success()
            } catch (ex: IOException) {
                ex.printStackTrace()
                Log.d("testing", "doWork: ${ex.cause}")
                Result.failure()
            }
        }
    }
}