package com.ead.project.dreamer.domain

import com.ead.project.dreamer.domain.apis.app.GetChapterScrap
import com.ead.project.dreamer.domain.databasequeries.GetChapter
import com.ead.project.dreamer.domain.databasequeries.GetChapters
import com.ead.project.dreamer.domain.databasequeries.GetChaptersToDownload
import com.ead.project.dreamer.domain.databasequeries.GetChaptersToFix
import javax.inject.Inject

class ChapterManager @Inject constructor(
    val getChapter: GetChapter,
    val getChapters: GetChapters,
    val getChaptersToDownload: GetChaptersToDownload,
    val getChaptersToFix: GetChaptersToFix,
    val getChapterScrap: GetChapterScrap
)