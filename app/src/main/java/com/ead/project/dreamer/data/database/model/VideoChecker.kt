package com.ead.project.dreamer.data.database.model

import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import org.jsoup.Connection
import org.jsoup.Jsoup
import javax.inject.Inject

class VideoChecker @Inject constructor() {

    companion object {

        fun getSorterServerList(list: List<String>) : MutableList<String> {
            val pairList : MutableList<Pair<Int,String>> = ArrayList()
            if (!DataStore.readBoolean(Constants.PREFERENCE_EXTERNAL_PLAYER))
                for (data in list) {
                    pairList.add(Pair(getPositions(data),data))
                }
            else
                for (data in list) {
                    pairList.add(Pair(getPositionsExternal(data),data))
                }

            pairList.sortBy { it.first }
            return pairList.map {
                it.second
            }.toMutableList()
        }

        fun getConnection(url : String) : Boolean {
            return try {
                when (Jsoup.connect(url)
                    .ignoreContentType(true)
                    .method(Connection.Method.GET)
                    .execute()
                    .statusCode()) {
                    200 -> true
                    else -> false
                }
            } catch (e : Exception) {
                e.printStackTrace()
                false
            }
        }

        private fun getPositions(data : String) : Int {
            return when (Server.identify(data)) {
                Constants.TITLE_OKRU -> 0
                Constants.TITLE_ONEFICHIER -> 1
                Constants.TITLE_VOE -> 2
                Constants.TITLE_FEMBED -> 3
                Constants.TITLE_SOLIDFILES -> 4
                Constants.TITLE_BAYFILES -> 5
                Constants.TITLE_SENDVID -> 6
                Constants.TITLE_STREAMTAPE -> 7
                Constants.TITLE_ZIPPYSHARE -> 8
                Constants.TITLE_PUJ -> 9
                Constants.TITLE_EMBED -> 10
                Constants.TITLE_VIDEOBIN -> 11
                Constants.TITLE_MP4UPLOAD -> 12
                Constants.TITLE_UQLOAD -> 13
                Constants.TITLE_MEGA -> 14
                else -> 10000
            }
        }

        private fun getPositionsExternal(data : String) : Int {
            return when (Server.identify(data)) {
                Constants.TITLE_FEMBED -> 0
                Constants.TITLE_ONEFICHIER -> 1
                Constants.TITLE_VOE -> 2
                Constants.TITLE_OKRU -> 3
                Constants.TITLE_SOLIDFILES -> 4
                Constants.TITLE_BAYFILES -> 5
                Constants.TITLE_SENDVID -> 6
                Constants.TITLE_STREAMTAPE -> 7
                Constants.TITLE_ZIPPYSHARE -> 8
                Constants.TITLE_PUJ -> 9
                Constants.TITLE_EMBED -> 10
                Constants.TITLE_VIDEOBIN -> 11
                Constants.TITLE_MP4UPLOAD -> 12
                Constants.TITLE_UQLOAD -> 13
                Constants.TITLE_MEGA -> 14
                else -> 10000
            }
        }
    }
}