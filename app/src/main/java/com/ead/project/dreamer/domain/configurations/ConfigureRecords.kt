package com.ead.project.dreamer.domain.configurations

import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.Chapter
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ConfigureRecords @Inject constructor(
    private val repository: AnimeRepository
) {
    suspend operator fun invoke(chapterList: List<Chapter>) {
        val nexToChapterList: MutableList<Chapter> = ArrayList()
        var isUpgradeable = false
        for (chapter in chapterList) {
            if (chapter.alreadySeen) {
                chapter.alreadySeen = false
                isUpgradeable = true
                val nextChapter = repository
                    .getChapterFromTitleAndNumber(
                        chapter.title,
                        chapter.number + 1
                    )

                if (nextChapter != null) {
                    nextChapter.currentSeen = 1
                    nextChapter.lastSeen = Calendar.getInstance().time
                    nexToChapterList.add(nextChapter)
                }
            }
        }
        if (isUpgradeable) {
            nexToChapterList.addAll(chapterList)
            repository.updateChapterList(nexToChapterList.reversed())
        }
    }

    fun checkIfUpgradeExist(chapterList: List<Chapter>): Boolean {
        for (chapter in chapterList) if (chapter.alreadySeen) return true
        return false
    }
}