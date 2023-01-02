package com.ead.project.dreamer.domain

import com.ead.project.dreamer.domain.configurations.ConfigureRecords
import com.ead.project.dreamer.domain.databasequeries.GetRecords
import javax.inject.Inject

class RecordsManager @Inject constructor(
    val getRecords: GetRecords,
    val configureRecords: ConfigureRecords,
)