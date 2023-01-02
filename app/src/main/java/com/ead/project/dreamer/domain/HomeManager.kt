package com.ead.project.dreamer.domain

import com.ead.project.dreamer.domain.apis.app.GetHomeScrap
import com.ead.project.dreamer.domain.databasequeries.GetHomeList
import com.ead.project.dreamer.domain.databasequeries.GetHomeRecommendations
import com.ead.project.dreamer.domain.databasequeries.GetHomeReleaseList
import javax.inject.Inject

class HomeManager @Inject constructor(
    val getHomeList: GetHomeList,
    val getHomeRecommendations: GetHomeRecommendations,
    val getHomeReleaseList: GetHomeReleaseList,
    val getHomeScrap: GetHomeScrap
)