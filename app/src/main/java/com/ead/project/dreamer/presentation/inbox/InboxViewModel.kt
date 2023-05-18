package com.ead.project.dreamer.presentation.inbox

import androidx.lifecycle.ViewModel
import com.ead.project.dreamer.data.utils.AdManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    val adManager: AdManager
) : ViewModel()