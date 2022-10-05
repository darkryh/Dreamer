package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.AnimeBase
import com.ead.project.dreamer.data.database.model.ChapterHome
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class NewContentWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context,workerParameters) {

    lateinit var repository: AnimeRepository

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val chapterHomeList = repository.getChapterHomeReleaseList()
                operatingData(chapterHomeList)
                Result.success()
            }
            catch (ex : IOException) {
                ex.printStackTrace()
                Result.failure()
            }
        }
    }

    private suspend fun operatingData(chapterHomeList : List<ChapterHome>) {
        val seriesList : MutableList<AnimeBase> = ArrayList()
        for (chapter in chapterHomeList) {
            if (!repository.checkIfAnimeBaseExist(chapter.title)) {
                seriesList.add(AnimeBase(
                    0,
                    chapter.title,
                    chapter.chapterCover,
                    fixLinker(chapter.reference),
                    chapter.type,
                    Calendar.getInstance().get(Calendar.YEAR))
                )
            }
        }
        if (seriesList.isNotEmpty()) {
            repository.insertAllAnimeBase(seriesList)
        }
    }

    private fun fixLinker(link : String) = link.substringBefore("-episodio-")
        .replace("/ver/","/anime/")
        .plus("-sub-espanol")
}