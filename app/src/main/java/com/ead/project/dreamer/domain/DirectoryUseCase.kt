package com.ead.project.dreamer.domain

import com.ead.project.dreamer.domain.apis.app.GetDirectoryScrap
import com.ead.project.dreamer.domain.databasequeries.GetDirectory
import com.ead.project.dreamer.domain.databasequeries.GetDirectoryList
import com.ead.project.dreamer.domain.directory.GetDirectoryState
import com.ead.project.dreamer.domain.directory.SetDirectoryState
import javax.inject.Inject

class DirectoryUseCase @Inject constructor(
    val getDirectoryList: GetDirectoryList,
    val getDirectory: GetDirectory,
    val getDirectoryScrap: GetDirectoryScrap,
    val getDirectoryState: GetDirectoryState,
    val setDirectoryState: SetDirectoryState
)