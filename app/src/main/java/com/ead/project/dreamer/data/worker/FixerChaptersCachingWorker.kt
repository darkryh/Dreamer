package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.app.model.scrapping.ChapterScrap
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.network.WebProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

class FixerChaptersCachingWorker@AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context,workerParameters) {

    lateinit var repository: AnimeRepository
    lateinit var webProvider: WebProvider

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val chapterScrap = repository.getChapterScrap()
                if (ChapterScrap.get() == chapterScrap) return@withContext Result.success()
                else ChapterScrap.set(chapterScrap)
                val fakeChapter = Chapter.fake()

                val chaptersToFix = repository.getChaptersToFix()

                for (chapter in chaptersToFix) {
                    val animeBase = repository.getAnimeBaseById(chapter.idProfile)
                    val requestedProfileChapters = async {
                        webProvider.getChaptersFromProfile(
                            fakeChapter,
                            animeBase.reference,
                            chapter.idProfile,
                            chapterScrap) }

                    val deleteData = async { repository.deleteChaptersById(chapter.idProfile) }
                    deleteData.await().apply {
                        requestedProfileChapters.await().apply { repository.insertChapters(this) }
                    }
                }
                Result.success()
            }
            catch (ex : IOException) {
                ex.printStackTrace()
                Result.failure()
            }
        }
    }

}