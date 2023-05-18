package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.domain.ChapterUseCase
import com.ead.project.dreamer.domain.DirectoryUseCase
import com.ead.project.dreamer.domain.ObjectUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

@HiltWorker
class FixerChaptersCachingWorker@AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val chapterUseCase: ChapterUseCase,
    private val directoryUseCase: DirectoryUseCase,
    private val objectUseCase: ObjectUseCase,
    private val webProvider: WebProvider
) : CoroutineWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                /*val chapterScrap = chapterUseCase.getChapterScrap.fromApi()
                if (ChapterScrap.get() == chapterScrap) return@withContext Result.success()

                ChapterScrap.set(chapterScrap)
                val fakeChapter = Chapter.fake()
                val chaptersToFix = chapterUseCase.getChaptersToFix()

                for (chapter in chaptersToFix) {
                    val animeBase = directoryUseCase.getDirectory.byId(chapter.idProfile)
                    val requestedProfileChapters = async {
                        webProvider.getChaptersFromProfile(
                            fakeChapter,
                            animeBase.reference,
                            chapter.idProfile) }

                    val deleteData = async { objectUseCase.deleteObject(chapter) }
                    deleteData.await().apply {
                        requestedProfileChapters.await().apply { objectUseCase.insertObject(this) }
                    }
                }*/
                Result.success()
            }
            catch (ex : IOException) {
                ex.printStackTrace()
                Result.failure()
            }
        }
    }

}