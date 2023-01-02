package com.ead.project.dreamer.domain

import com.ead.project.dreamer.domain.apis.app.GetAppStatusVersion
import com.ead.project.dreamer.domain.apis.app.GetApplicationAds
import javax.inject.Inject

class ApplicationManager @Inject constructor(
    val getApplicationAds: GetApplicationAds,
    val getAppStatusVersion: GetAppStatusVersion
)