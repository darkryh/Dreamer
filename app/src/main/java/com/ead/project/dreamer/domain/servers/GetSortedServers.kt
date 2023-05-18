package com.ead.project.dreamer.domain.servers

import com.ead.project.dreamer.app.data.server.Server
import com.ead.project.dreamer.domain.PreferenceUseCase
import javax.inject.Inject

class GetSortedServers @Inject constructor(
    private val serverIdentifier: ServerIdentifier,
    preferenceUseCase: PreferenceUseCase
) {

    private val playerPreferences = preferenceUseCase.playerPreferences

    operator fun invoke(embedList : List<String>, isDownload : Boolean) : MutableList<String> {
        val sortedList : MutableList<Pair<Int,String>> = ArrayList()

        if (!isDownload) {
            if (!playerPreferences.isInExternalMode()) {

                for (embedUrl in embedList) {
                    sortedList.add(Pair(getPositions(embedUrl),embedUrl))
                }
            }
            else {
                for (embedUrl in embedList) {
                    sortedList.add(Pair(getPositionsExternal(embedUrl),embedUrl))
                }
            }
        }
        else {

            for (embedUrl in embedList) {
                sortedList.add(Pair(getDownloadsPosition(embedUrl),embedUrl))
            }

        }

        return sortedList
            .apply { sortBy { it.first } }
            .map { it.second }
            .toMutableList()
    }

    private fun getPositions(data : String) : Int {
        return when (serverIdentifier(data)) {
            Server.MEDIAFIRE -> 0
            Server.OKRU -> 1
            Server.ONEFICHIER -> 2
            Server.STREAMSB -> 3
            Server.SENDVID -> 4
            Server.VIDEOBIN -> 5
            Server.FEMBED -> 6
            Server.VOE -> 7
            Server.BAYFILES -> 8
            Server.ZIPPYSHARE -> 9
            Server.STREAMTAPE -> 10
            Server.SOLIDFILES -> 11
            Server.PUJ -> 12
            Server.GOOGLE_DRIVE -> 13
            Server.ANONFILE -> 14
            Server.FIRELOAD -> 15
            Server.MP4UPLOAD -> 16
            Server.UQLOAD -> 17
            Server.MEGA -> 18
            else -> 10000
        }
    }

    private fun getPositionsExternal(data : String) : Int {
        return when (serverIdentifier(data)) {
            Server.MEDIAFIRE -> 0
            Server.ONEFICHIER -> 1
            Server.FEMBED -> 2
            Server.STREAMSB -> 3
            Server.SENDVID -> 4
            Server.VIDEOBIN -> 5
            Server.VOE -> 6
            Server.OKRU -> 7
            Server.ZIPPYSHARE -> 8
            Server.BAYFILES -> 9
            Server.STREAMTAPE -> 10
            Server.SOLIDFILES -> 11
            Server.PUJ -> 12
            Server.GOOGLE_DRIVE -> 13
            Server.ANONFILE -> 14
            Server.FIRELOAD -> 15
            Server.MP4UPLOAD -> 16
            Server.UQLOAD -> 17
            Server.MEGA -> 18
            else -> 10000
        }
    }

    private fun getDownloadsPosition(data: String) : Int {
        return when (serverIdentifier(data)) {
            Server.ONEFICHIER -> 0
            Server.MEDIAFIRE -> 1
            Server.OKRU -> 2
            Server.FEMBED -> 3
            Server.SENDVID -> 4
            Server.VOE -> 5
            Server.ZIPPYSHARE -> 6
            Server.BAYFILES -> 7
            Server.STREAMTAPE -> 8
            Server.SOLIDFILES -> 9
            Server.PUJ -> 10
            Server.VIDEOBIN -> 11
            Server.STREAMSB -> 12
            Server.GOOGLE_DRIVE -> 13
            Server.ANONFILE -> 14
            Server.FIRELOAD -> 15
            Server.MP4UPLOAD -> 16
            Server.UQLOAD -> 17
            Server.MEGA -> 18
            else -> 10000
        }
    }
}