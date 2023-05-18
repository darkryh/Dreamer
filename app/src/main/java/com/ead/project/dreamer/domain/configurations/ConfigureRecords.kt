package com.ead.project.dreamer.domain.configurations

import com.ead.project.dreamer.app.data.util.TimeUtil
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class ConfigureRecords @Inject constructor(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(chapterList: List<Chapter>) {
        val nexToChapterList: MutableList<Chapter> = ArrayList()
        var isUpgradeable = false
        for (chapter in chapterList) {
            if (chapter.isContentConsumed) {
                //todo chapter.alreadySeen = false
                isUpgradeable = true
                val nextChapter = repository
                    .getChapterFromTitleAndNumber(
                        chapter.title,
                        chapter.number + 1
                    )

                if (nextChapter != null) {
                    nexToChapterList.add(
                        nextChapter.copy(
                            currentProgress = 1,
                            lastDateSeen = TimeUtil.getNow()
                        )
                    )
                }
            }
        }
        if (isUpgradeable) {
            nexToChapterList.addAll(chapterList)
            repository.updateChapterList(nexToChapterList.reversed())
        }
    }

    fun checkIfUpgradeExist(chapterList: List<Chapter>): Boolean {
        for (chapter in chapterList) if (chapter.isContentConsumed) return true
        return false
    }
}