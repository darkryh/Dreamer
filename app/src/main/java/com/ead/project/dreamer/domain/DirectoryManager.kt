package com.ead.project.dreamer.domain

import com.ead.project.dreamer.domain.apis.app.GetDirectoryScrap
import com.ead.project.dreamer.domain.databasequeries.GetDirectory
import com.ead.project.dreamer.domain.databasequeries.GetDirectoryList
import javax.inject.Inject

class DirectoryManager @Inject constructor(
    val getDirectoryList: GetDirectoryList,
    val getDirectory: GetDirectory,
    val getDirectoryScrap: GetDirectoryScrap
)