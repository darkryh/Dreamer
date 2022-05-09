package com.ead.project.dreamer.ui.menuplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MenuPlayerViewModelFactory (private var embedList: MutableList<String>) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = MenuPlayerViewModel(embedList) as T
}