package com.ead.project.dreamer.presentation.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ead.project.dreamer.app.model.EadAccount
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.utils.AdOrder
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.ead.project.dreamer.domain.RecordsUseCase
import com.ead.project.dreamer.domain.servers.HandleChapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val recordsUseCase: RecordsUseCase,
    val handleChapter: HandleChapter,
    preferenceUseCase: PreferenceUseCase
): ViewModel() {

    private val userPreferences = preferenceUseCase.userPreferences

    private val adOrder by lazy {
        AdOrder(
            items = mutableListOf(),
            ads = emptyList()
        )
    }

    private val _records : MutableLiveData<List<Any>> = MutableLiveData(emptyList())
    val records : LiveData<List<Any>> = _records

   fun setRecords(list : List<Any>) {
       viewModelScope.launch(Dispatchers.IO) {
           adOrder.setup(list,_records)
       }
   }

    fun getAccount() : Flow<EadAccount?> = userPreferences.user

    fun getLiveDataRecords() : LiveData<List<Chapter>> = recordsUseCase.getRecords.livedata()

    fun configureRecords(chapterList: List<Chapter>) =
        viewModelScope.launch (Dispatchers.IO) { recordsUseCase.configureRecords(chapterList) }

}