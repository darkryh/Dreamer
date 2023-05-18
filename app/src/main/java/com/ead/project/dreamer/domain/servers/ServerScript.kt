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
    operator fun invoke() : String {
        val code = get()
        if (code != "null") {
            return code
        }
        return fromApi().also { set(it) }
    }

    fun fromApi() : String = repository.getServerScript()

    private fun get() : String =
        runBlocking { preferences.getString(Server.PREFERENCE_SERVER_SCRIPT) }

    private fun set(code : String) = runBlocking {
        preferences.set(Server.PREFERENCE_SERVER_SCRIPT,code)
    }
}