package com.ead.project.dreamer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.data.database.model.AnimeBase
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.domain.DirectoryUseCase
import com.ead.project.dreamer.domain.HomeUseCase
import com.ead.project.dreamer.domain.ObjectUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

@HiltWorker
class NewContentWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val directoryUseCase: DirectoryUseCase,
    private val homeUseCase: HomeUseCase,
    private val objectUseCase: ObjectUseCase
) : CoroutineWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val chapterHomeList = homeUseCase.getHomeReleaseList()
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
            if (!directoryUseCase.getDirectory.checkIfTitleExist(chapter.title)) {
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
        if (seriesList.isNotEmpty()) objectUseCase.insertObject(seriesList)
    }

    private fun fixLinker(link : String) = link.substringBefore("-episodio-")
        .replace("/ver/","/anime/")
        .plus("-sub-espanol")
}