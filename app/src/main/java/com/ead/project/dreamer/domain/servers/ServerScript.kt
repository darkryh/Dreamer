package com.ead.project.dreamer.domain.servers

import com.ead.project.dreamer.app.data.server.Server
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.domain.PreferenceUseCase
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ServerScript @Inject constructor(
    private val repository: AnimeRepository,
    preferenceUseCase: PreferenceUseCase
) {

    private val preferences = preferenceUseCase.preferences
    operator fun invoke() : String = runBlocking {
        val code = get()
        if (code != "null") {
            return@runBlocking code
        }
        return@runBlocking fromApi().also { set(it) }
    }

    suspend fun fromApi() : String = repository.getServerScript()

    private suspend fun get() : String = preferences.getString(Server.PREFERENCE_SERVER_SCRIPT)

    private suspend fun set(code : String) = preferences.set(Server.PREFERENCE_SERVER_SCRIPT,code)
}