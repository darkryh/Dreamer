package com.ead.project.dreamer.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.app.model.scrapping.NewsItemScrap
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.NewsItem
import com.ead.project.dreamer.data.network.WebProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

@HiltWorker
class NewsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context,workerParameters) {

    lateinit var repository: AnimeRepository
    lateinit var webProvider: WebProvider

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                newsOperator(this)
                Result.success()
            }
            catch (ex : IOException) {
                ex.printStackTrace()
                Result.failure()
            }
        }
    }

    private suspend fun newsOperator(scope: CoroutineScope) {
        scope.apply {
            val newsItemScrap : NewsItemScrap = NewsItemScrap.get()?: NewsItemScrap.getDataFromApi(repository)
            Log.d("testing", "newsOperator: $newsItemScrap")
            val newsItemsList = repository.getNewsItems()
            val isDataEmpty = newsItemsList.isEmpty()
            val newsItem = if (isDataEmpty) NewsItem.fake()
            else repository.getNewsItems().last()
            val newsData = async { webProvider.getNews(newsItem,newsItemScrap) }
            newsData.await().apply {
                if (isDataEmpty) repository.insertAllNewsItems(this)
                else repository.updateNews(this)
                Result.success()
            }
        }
    }
}