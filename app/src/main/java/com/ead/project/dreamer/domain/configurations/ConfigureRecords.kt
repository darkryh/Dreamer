package com.ead.project.dreamer.domain.configurations

import com.ead.project.dreamer.app.data.util.TimeUtil
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class ConfigureRecords @Inject constructor(
    private val repository: AnimeRepository
) {

    private val chaptersToUpdate: MutableList<Chapter> = ArrayList()
    private val chaptersConsumed: MutableList<Chapter> = ArrayList()
    private var isUpgradeable = false

    suspend operator fun invoke(chapterList: List<Chapter>) {
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
            repository.updateChapterList(chaptersConsumed.reversed())
            repository.updateChapterList(chaptersToUpdate.reversed())
        }
    }

    fun checkIfUpgradeExist(chapterList: List<Chapter>): Boolean {
        for (chapter in chapterList) if (chapter.isContentConsumed) return true
        return false
    }
}