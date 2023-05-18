package com.ead.project.dreamer.domain.configurations

import com.ead.project.dreamer.app.data.util.TimeUtil
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class ConfigureRecords @Inject constructor(
    private val repository: AnimeRepository
) {

    private val chaptersToUpdate: MutableList<Chapter> = ArrayList()

    suspend operator fun invoke(chapterList: List<Chapter>) {
        chaptersToUpdate.clear()
        var isUpgradeable = false
        for (chapter in chapterList) {
            if (chapter.isContentConsumed) {

                isUpgradeable = true
                val nextChapter = repository
                    .getChapterFromTitleAndNumber(
                        chapter.title,
                        chapter.number + 1
                    )

                if (nextChapter != null) {

                    chaptersToUpdate.add(
                        chapter.copy(
                            isContentConsumed = false
                        )
                    )

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
            chaptersToUpdate.addAll(chapterList)
            repository.updateChapterList(chaptersToUpdate.reversed())
        }
    }

    fun checkIfUpgradeExist(chapterList: List<Chapter>): Boolean {
        for (chapter in chapterList) if (chapter.isContentConsumed) return true
        return false
    }
}