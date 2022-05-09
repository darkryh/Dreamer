package com.ead.project.dreamer.ui.login

import androidx.lifecycle.ViewModel
import com.ead.project.dreamer.data.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val repository: AnimeRepository
) : ViewModel() {

    fun getToken() = repository.getAccessToken()!!

    fun getUserData() = repository.getUserData()!!

    fun getRefreshToken() = repository.getRefreshAccessToken()!!

    fun getUserInToGuild(id: String) = repository.getUserInToGuild(id)!!
}