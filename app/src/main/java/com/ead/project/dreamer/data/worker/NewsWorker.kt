package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.data.database.model.NewsItem
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.domain.NewsUseCase
import com.ead.project.dreamer.domain.ObjectUseCase
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
    @Assisted workerParameters: WorkerParameters,
    private val newsUseCase: NewsUseCase,
    private val objectUseCase: ObjectUseCase,
    private val webProvider: WebProvider
) : CoroutineWorker(context,workerParameters) {

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
            val newsItemsList = newsUseCase.getNews()

            val isDataEmpty = newsItemsList.isEmpty()
            val newsItem = if (isDataEmpty) NewsItem.fake()
            else newsItemsList.last()

            val newsData = async { webProvider.getNews(newsItem) }
            newsData.await().apply {
                if (isDataEmpty) objectUseCase.insertObject (this)
                else objectUseCase.updateObject(this)
                Result.success()
            }
        }
    }
}