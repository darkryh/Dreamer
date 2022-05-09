package com.ead.project.dreamer.data.database.model

import com.ead.project.dreamer.data.commons.Constants

open class Server : VideoInterface {

    private lateinit var _domain : String
    private lateinit var _videoId : String
    private lateinit var _headers : MutableList<Pair<String,String>>
    private var _isDirect : Boolean = true
    private var _videoList : MutableList<VideoModel> = ArrayList()


    var domain : String
        get() = _domain
        set(value) { _domain = value }

    var videoId : String
        get() = _videoId
        set(value) { _videoId = value}

    var headers : MutableList<Pair<String,String>>
        get() = _headers
        set(value) { _headers = value}

    var videoList : MutableList<VideoModel>
        get() = _videoList
        set(value) { _videoList = value }

    var isDirect : Boolean
        get() = _isDirect
        set(value) { _isDirect = value }

    companion object {

        fun identify (url : String) : String {

            if (Constants.SERVER_OKRU in url) return Constants.TITLE_OKRU
            if (Constants.SERVER_SOLIDFILES in url) return Constants.TITLE_SOLIDFILES
            if (Constants.SERVER_ONEFICHIER in url) return Constants.TITLE_ONEFICHIER
            if (Constants.SERVER_FEMBED in url) return Constants.TITLE_FEMBED
            if (Constants.SERVER_SENDVID in url) return Constants.TITLE_SENDVID
            if (Constants.SERVER_BAYFILES in url) return Constants.TITLE_BAYFILES
            if (Constants.SERVER_ZIPPYSHARE in url) return Constants.TITLE_ZIPPYSHARE
            if (Constants.SERVER_STREAMTAPE in url) return Constants.TITLE_STREAMTAPE
            if (Constants.SERVER_PUJ in url) return Constants.TITLE_PUJ
            if (Constants.SERVER_VIDEOBIN in url) return Constants.TITLE_VIDEOBIN
            if (Constants.SERVER_MP4UPLOAD in url) return Constants.TITLE_MP4UPLOAD
            if (Constants.SERVER_EMBED in url) return Constants.TITLE_EMBED
            if (Constants.SERVER_UQLOAD in url) return Constants.TITLE_UQLOAD
            if (Constants.SERVER_MEGA in url) return Constants.TITLE_MEGA

            return "null"
        }
    }

    override fun configureHeaders() {}

    override fun patternReference() {}

    override fun linkProcess(){}

    fun connectionAvailable() : Boolean {
        if (videoList.isNotEmpty()) {
            if (VideoChecker.getConnection(videoList.last().directLink)) {
                return true
            }
        }
        return false
    }
}