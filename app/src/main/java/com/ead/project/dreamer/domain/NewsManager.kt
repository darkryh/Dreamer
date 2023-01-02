package com.ead.project.dreamer.domain

import com.ead.project.dreamer.domain.apis.app.GetNewsItemScrap
import com.ead.project.dreamer.domain.apis.app.GetNewsItemWebScrap
import com.ead.project.dreamer.domain.databasequeries.GetNews
import javax.inject.Inject

class NewsManager @Inject constructor(
    val getNews: GetNews,
    val getNewsItemScrap: GetNewsItemScrap,
    val getNewsItemWebScrap: GetNewsItemWebScrap
)