package com.ead.project.dreamer.ui.directory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ead.project.dreamer.data.database.model.AnimeBase
import com.ead.project.dreamer.domain.DirectoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DirectoryViewModel @Inject constructor(
    private val directoryUseCase: DirectoryUseCase
): ViewModel() {

    fun getDirectory(title : String) : LiveData<List<AnimeBase>> =
        directoryUseCase.getDirectoryList.livedata(title,false)

    fun getFullDirectory(title: String): LiveData<List<AnimeBase>> =
        directoryUseCase.getDirectoryList.livedata(title,true)
}