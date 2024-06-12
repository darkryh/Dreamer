package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.app.data.worker.Worker
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.domain.ChapterUseCase
import com.ead.project.dreamer.domain.ObjectUseCase
import com.ead.project.dreamer.domain.ProfileUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

@HiltWorker
class ChaptersCachingWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val profileUseCase: ProfileUseCase,
    private val chapterUseCase: ChapterUseCase,
    private val objectUseCase: ObjectUseCase,
    private val webProvider: WebProvider
) : CoroutineWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val array = inputData
                    .getStringArray(Worker.CHAPTER_PROFILE_KEY)!!
                val id = array[0].toInt()
                val size = array[1].toInt()
                val reference = array[2]
                val chapterId = array[3].toInt()

                val localChapters = chapterUseCase.getChapters(id)
                val title = (profileUseCase.getProfile(id)?:
                return@withContext Result.failure()).title

                val localChaptersToCompare = localChapters.map { it.toComparison() }

                val requestedChaptersToCompare = async {
                    webProvider.getChaptersFromProfile(
                        reference,
                        id,
                        title,
                        context
                    ) }

                requestedChaptersToCompare.await().apply {
                    val remainingChapters = this.filter { chapterToCompare ->
                        chapterToCompare !in localChaptersToCompare
                    }.map { it.toChapter() }

                    objectUseCase.insertObject(remainingChapters)
                    Result.success()
                }

                Result.failure()
            }
            catch (ex : IOException) {
                ex.printStackTrace()
                Result.failure()
            }
        }
    }
}