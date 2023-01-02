package com.ead.project.dreamer.domain.servers

import com.ead.project.dreamer.data.commons.Constants
import javax.inject.Inject

class GetSortedServers @Inject constructor(
    private val serverIdentifier: ServerIdentifier
) {

    operator fun invoke(embedList : List<String>, isDownload : Boolean) : MutableList<String> {
        val sortedList : MutableList<Pair<Int,String>> = ArrayList()
        if (!isDownload) {
            if (!Constants.isExternalPlayerMode())
                for (embedUrl in embedList) sortedList.add(Pair(getPositions(embedUrl),embedUrl))
            else
                for (embedUrl in embedList) sortedList.add(Pair(getPositionsExternal(embedUrl),embedUrl))
        }
        else for (embedUrl in embedList) sortedList.add(Pair(getDownloadsPosition(embedUrl),embedUrl))

        return sortedList
            .apply { sortBy { it.first } }
            .map { it.second }
            .toMutableList()
    }

    private fun getPositions(data : String) : Int {
        return when (serverIdentifier(data)) {
            Constants.TITLE_MEDIAFIRE -> 0
            Constants.TITLE_OKRU -> 1
            Constants.TITLE_ONEFICHIER -> 2
            Constants.TITLE_STREAMSB -> 3
            Constants.TITLE_SENDVID -> 4
            Constants.TITLE_VIDEOBIN -> 5
            Constants.TITLE_FEMBED -> 6
            Constants.TITLE_VOE -> 7
            Constants.TITLE_BAYFILES -> 8
            Constants.TITLE_ZIPPYSHARE -> 9
            Constants.TITLE_STREAMTAPE -> 10
            Constants.TITLE_SOLIDFILES -> 11
            Constants.TITLE_PUJ -> 12
            Constants.TITLE_GOOGLE_DRIVE -> 13
            Constants.TITLE_ANONFILE -> 14
            Constants.TITLE_FIRELOAD -> 15
            Constants.TITLE_MP4UPLOAD -> 16
            Constants.TITLE_UQLOAD -> 17
            Constants.TITLE_MEGA -> 18
            else -> 10000
        }
    }

    private fun getPositionsExternal(data : String) : Int {
        return when (serverIdentifier(data)) {
            Constants.TITLE_MEDIAFIRE -> 0
            Constants.TITLE_ONEFICHIER -> 1
            Constants.TITLE_FEMBED -> 2
            Constants.TITLE_STREAMSB -> 3
            Constants.TITLE_SENDVID -> 4
            Constants.TITLE_VIDEOBIN -> 5
            Constants.TITLE_VOE -> 6
            Constants.TITLE_OKRU -> 7
            Constants.TITLE_ZIPPYSHARE -> 8
            Constants.TITLE_BAYFILES -> 9
            Constants.TITLE_STREAMTAPE -> 10
            Constants.TITLE_SOLIDFILES -> 11
            Constants.TITLE_PUJ -> 12
            Constants.TITLE_GOOGLE_DRIVE -> 13
            Constants.TITLE_ANONFILE -> 14
            Constants.TITLE_FIRELOAD -> 15
            Constants.TITLE_MP4UPLOAD -> 16
            Constants.TITLE_UQLOAD -> 17
            Constants.TITLE_MEGA -> 18
            else -> 10000
        }
    }

    private fun getDownloadsPosition(data: String) : Int {
        return when (serverIdentifier(data)) {
            Constants.TITLE_ONEFICHIER -> 0
            Constants.TITLE_MEDIAFIRE -> 1
            Constants.TITLE_OKRU -> 2
            Constants.TITLE_FEMBED -> 3
            Constants.TITLE_SENDVID -> 4
            Constants.TITLE_VOE -> 5
            Constants.TITLE_ZIPPYSHARE -> 6
            Constants.TITLE_BAYFILES -> 7
            Constants.TITLE_STREAMTAPE -> 8
            Constants.TITLE_SOLIDFILES -> 9
            Constants.TITLE_PUJ -> 10
            Constants.TITLE_VIDEOBIN -> 11
            Constants.TITLE_STREAMSB -> 12
            Constants.TITLE_GOOGLE_DRIVE -> 13
            Constants.TITLE_ANONFILE -> 14
            Constants.TITLE_FIRELOAD -> 15
            Constants.TITLE_MP4UPLOAD -> 16
            Constants.TITLE_UQLOAD -> 17
            Constants.TITLE_MEGA -> 18
            else -> 10000
        }
    }
}