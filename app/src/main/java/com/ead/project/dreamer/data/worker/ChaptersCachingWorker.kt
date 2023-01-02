package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.domain.ChapterManager
import com.ead.project.dreamer.domain.ObjectManager
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
    private val chapterManager: ChapterManager,
    private val objectManager: ObjectManager,
    private val webProvider: WebProvider
) : CoroutineWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val array = inputData
                    .getStringArray(Constants.CHAPTER_PROFILE_KEY)!!
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
                    objectManager.insertObject(this)
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
        if (size <= 0) chapterManager.getChapter.fromId(chapterId).apply { if (this != null) return this }
        return Chapter.fake()
    }
}