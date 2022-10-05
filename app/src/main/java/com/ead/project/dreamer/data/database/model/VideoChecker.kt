package com.ead.project.dreamer.data.database.model

import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.ServerManager
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class VideoChecker @Inject constructor() {

    companion object {

        fun getSorterServerList(list: List<String>,isDownload : Boolean = false) : MutableList<String> {
            val pairList : MutableList<Pair<Int,String>> = ArrayList()
            if (!isDownload) {
                if (!DataStore.readBoolean(Constants.PREFERENCE_EXTERNAL_PLAYER))
                    for (data in list) pairList.add(Pair(getPositions(data),data))
                else
                    for (data in list) pairList.add(Pair(getPositionsExternal(data),data))
            }
            else for (data in list) pairList.add(Pair(getDownloadsPosition(data),data))


            pairList.sortBy { it.first }
            return pairList.map {
                it.second
            }.toMutableList()
        }

        fun getConnection(url : String) : Boolean {
            return try {
                val urlObject = URL(url)
                val connection: HttpURLConnection = urlObject.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()
                val code = connection.responseCode
                connection.disconnect()
                when (code) {
                    200 -> true
                    else -> false
                }
            } catch (e : Exception) { false }
        }

        private fun getDownloadsPosition(data: String) : Int {
            return when (ServerManager.identify(data)) {
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

        private fun getPositions(data : String) : Int {
            return when (ServerManager.identify(data)) {
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
            return when (ServerManager.identify(data)) {
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
    }
}