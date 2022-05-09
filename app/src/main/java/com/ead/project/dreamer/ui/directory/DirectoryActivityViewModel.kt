package com.ead.project.dreamer.ui.directory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.AnimeBase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DirectoryActivityViewModel @Inject constructor(
    private val repository: AnimeRepository
): ViewModel() {

    fun getDirectory(title : String) : LiveData<List<AnimeBase>> =
        repository.getFlowAnimeBaseList(title).asLiveData()
}