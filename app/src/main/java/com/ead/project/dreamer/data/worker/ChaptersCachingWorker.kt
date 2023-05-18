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
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

@HiltWorker
class ChaptersCachingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
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

                val chapter = getChapterIfChapterExist(size, chapterId)

                val requestedProfileChapters = async {
                    webProvider.getChaptersFromProfile(
                        chapter,
                        reference,
                        id) }

                requestedProfileChapters.await().apply {
                    objectUseCase.insertObject(this)
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

    private suspend fun getChapterIfChapterExist(size : Int,chapterId : Int) : Chapter {
        if (size <= 0) chapterUseCase.getChapter.fromId(chapterId).apply { if (this != null) return this }
        return fakeChapter
    }

    private val fakeChapter : Chapter = Chapter(
        id = 0,
        idProfile = 0,
        title = "null",
        cover = "null",
        number = -1,
        reference = "null"
    )
}