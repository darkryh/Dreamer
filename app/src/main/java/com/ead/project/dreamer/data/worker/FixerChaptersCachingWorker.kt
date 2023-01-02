package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.app.model.scrapping.ChapterScrap
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.domain.ChapterManager
import com.ead.project.dreamer.domain.DirectoryManager
import com.ead.project.dreamer.domain.ObjectManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

@HiltWorker
class FixerChaptersCachingWorker@AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val chapterManager: ChapterManager,
    private val directoryManager: DirectoryManager,
    private val objectManager: ObjectManager,
    private val webProvider: WebProvider
) : CoroutineWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val chapterScrap = chapterManager.getChapterScrap.fromApi()
                if (ChapterScrap.get() == chapterScrap) return@withContext Result.success()

                ChapterScrap.set(chapterScrap)
                val fakeChapter = Chapter.fake()
                val chaptersToFix = chapterManager.getChaptersToFix()

                for (chapter in chaptersToFix) {
                    val animeBase = directoryManager.getDirectory.byId(chapter.idProfile)
                    val requestedProfileChapters = async {
                        webProvider.getChaptersFromProfile(
                            fakeChapter,
                            animeBase.reference,
                            chapter.idProfile) }

                    val deleteData = async { objectManager.deleteObject(chapter) }
                    deleteData.await().apply {
                        requestedProfileChapters.await().apply { objectManager.insertObject(this) }
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