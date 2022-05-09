package com.ead.project.dreamer.data.database.model

import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import org.jsoup.Connection
import org.jsoup.Jsoup
import javax.inject.Inject

class VideoChecker @Inject constructor(private val dataList: MutableList<Pair<String, List<VideoModel>>>) {

    private val listIndex : MutableList<Int> = ArrayList()

    init {
        initializationList()
    }

    private fun initializationList() {
        if (!DataStore.readBoolean(Constants.PREFERENCE_EXTERNAL_PLAYER))
            for (data in dataList) {
                listIndex.add(getPositions(data.first))
            }
        else
            for (data in dataList) {
                listIndex.add(getPositionsExternal(data.first))
            }
    }

    fun updatedTripleList() : List<Triple<Int,String,List<VideoModel>>> {
        val tripleList : MutableList<Triple<Int,String,List<VideoModel>>> = ArrayList()
        for (pos  in listIndex.indices) {
            tripleList.add(Triple(listIndex[pos],dataList[pos].first,dataList[pos].second))
        }
        tripleList.sortBy {
            it.first
        }

        return tripleList
    }

    private fun getPositions(data : String) : Int {
        return when (Server.identify(data)) {
            Constants.TITLE_OKRU -> 0
            Constants.TITLE_ONEFICHIER -> 1
            Constants.TITLE_FEMBED -> 2
            Constants.TITLE_SOLIDFILES -> 3
            Constants.TITLE_BAYFILES -> 4
            Constants.TITLE_SENDVID -> 5
            Constants.TITLE_STREAMTAPE -> 6
            Constants.TITLE_ZIPPYSHARE -> 7
            Constants.TITLE_PUJ -> 8
            Constants.TITLE_EMBED -> 9
            Constants.TITLE_MEGA -> 10
            Constants.TITLE_VIDEOBIN -> 11
            Constants.TITLE_MP4UPLOAD -> 12
            Constants.TITLE_UQLOAD -> 13
            else -> 10000
        }
    }

    private fun getPositionsExternal(data : String) : Int {
        return when (Server.identify(data)) {
            Constants.TITLE_FEMBED -> 0
            Constants.TITLE_ONEFICHIER -> 1
            Constants.TITLE_OKRU -> 2
            Constants.TITLE_SOLIDFILES -> 3
            Constants.TITLE_BAYFILES -> 4
            Constants.TITLE_SENDVID -> 5
            Constants.TITLE_STREAMTAPE -> 6
            Constants.TITLE_ZIPPYSHARE -> 7
            Constants.TITLE_PUJ -> 8
            Constants.TITLE_EMBED -> 9
            Constants.TITLE_MEGA -> 10
            Constants.TITLE_VIDEOBIN -> 11
            Constants.TITLE_MP4UPLOAD -> 12
            Constants.TITLE_UQLOAD -> 13
            else -> 10000
        }
    }

    companion object {

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
    }
}