package com.ead.project.dreamer.app.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GoogleBuild(
    val isTermsAndConditionsAccepted : Boolean = false
) : Parcelable
