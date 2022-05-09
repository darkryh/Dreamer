package com.ead.project.dreamer.ui.web

import androidx.lifecycle.ViewModel
import com.ead.project.dreamer.data.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebViewModel @Inject constructor(
    val repository: AnimeRepository
) : ViewModel() {

    fun getToken() = repository.getAccessToken()!!
}