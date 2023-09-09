package com.ead.project.dreamer.domain.configurations

import com.ead.project.dreamer.app.data.util.TimeUtil
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.Chapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class ConfigureRecords @Inject constructor(
    private val repository: AnimeRepository
) {

    private val scope : CoroutineScope = CoroutineScope(Dispatchers.IO)

    private val chaptersToUpdate: MutableList<Chapter> = ArrayList()
    private val chaptersConsumed: MutableList<Chapter> = ArrayList()
    private var isUpgradeable = false

    suspend operator fun invoke(chapterList: List<Chapter>) {
        if (!checkIfUpgradeExist(chapterList)) return

        chaptersToUpdate.clear()
        chaptersConsumed.clear()
        isUpgradeable = false

        for (chapter in chapterList) {
            if (chapter.isContentConsumed) {

                isUpgradeable = true
                val nextChapter = repository
                    .getChapterFromTitleAndNumber(
                        chapter.title,
                        chapter.number + 1
                    )

                chaptersConsumed.add(
                    chapter.copy(
                        isContentConsumed = false
                    )
                )

                if (nextChapter != null) {

                    chaptersToUpdate.add(
                        nextChapter.copy(
                            currentProgress = 1,
                            lastDateSeen = TimeUtil.getNow()
                        )
                    )
                }
            }
        }

        if (isUpgradeable) {
            scope.launch {
                val consumedChapters =  async { repository.updateChapterList(chaptersConsumed.reversed()) }
                consumedChapters.await().apply {
                    repository.updateChapterList(chaptersToUpdate.reversed())
                }
            }
        }
    }

    private fun checkIfUpgradeExist(chapterList: List<Chapter>): Boolean {
        return chapterList.any { it.isContentConsumed }
    }
}