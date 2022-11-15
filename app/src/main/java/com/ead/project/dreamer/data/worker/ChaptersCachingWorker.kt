package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.app.model.scrapping.ChapterScrap
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.network.WebProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

class ChaptersCachingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context,workerParameters) {

    lateinit var repository: AnimeRepository
    lateinit var webProvider: WebProvider

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val chapterScrap = ChapterScrap.get()?:ChapterScrap.getDataFromApi(repository)
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
                        id,
                        chapterScrap) }

                requestedProfileChapters.await().apply {
                    repository.insertChapters(this)
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
        if (size <= 0) repository.getChapterFromId(chapterId).apply { if (this != null) return this }
        return Chapter.fake()
    }
}