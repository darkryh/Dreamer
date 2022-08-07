package com.ead.project.dreamer.data.database.model

import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore

open class Server : VideoInterface {

    private lateinit var _domain : String
    private lateinit var _videoId : String
    private var _isDirect : Boolean = true
    private var _videoList : MutableList<VideoModel> = ArrayList()
    private var _player : Player = Player.blank

    var player : Player
        get() = _player
        set(value) { _player = value }

    var domain : String
        get() = _domain
        set(value) { _domain = value }

    var videoId : String
        get() = _videoId
        set(value) { _videoId = value}


    var videoList : MutableList<VideoModel>
        get() = _videoList
        set(value) { _videoList = value }

    var isDirect : Boolean
        get() = _isDirect
        set(value) { _isDirect = value }

    companion object {

        fun identify (url : String) : String {

            if (Constants.SERVER_OKRU in url) return Constants.TITLE_OKRU
            if (Constants.SERVER_VOE in url) return Constants.TITLE_VOE
            if (Constants.SERVER_SOLIDFILES in url) return Constants.TITLE_SOLIDFILES
            if (Constants.SERVER_ONEFICHIER in url) return Constants.TITLE_ONEFICHIER
            if (Constants.SERVER_FEMBED in url) return Constants.TITLE_FEMBED
            if (Constants.SERVER_FIRELOAD in url) return Constants.TITLE_FIRELOAD
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

        fun isOperationBreak() = DataStore.readBoolean(Constants.PREFERENCE_RANK_AUTOMATIC_PLAYER) &&
                DataStore.readBoolean(Constants.BREAK_SERVER_OPERATION)

        fun endOperation() = DataStore.writeBooleanAsync(Constants.BREAK_SERVER_OPERATION,false)

        private val recommendedServers : List<String> =
            listOf(Constants.TITLE_OKRU,Constants.TITLE_ONEFICHIER, Constants.TITLE_SOLIDFILES , Constants.TITLE_VOE)

        private val webServers : List<String> =
            listOf(Constants.TITLE_MP4UPLOAD,Constants.TITLE_MEGA, Constants.TITLE_UQLOAD)

        fun isRecommended(server : String) : Boolean = server in recommendedServers

        fun isWebServer(server : String) : Boolean = server in webServers
    }

    override fun configureHeaders() {}

    override fun patternReference() {}

    override fun linkProcess(){}

    private val validatedServers : List<Player> =
        listOf(Player.Okru,Player.Onefichier,Player.SolidFiles,Player.Mp4Upload,Player.Uqload)

    fun isValidated () : Boolean = this.player in validatedServers

    fun isConnectionValidated() : Boolean = VideoChecker.getConnection(this.videoList.last().directLink)

    fun connectionAvailable() : Boolean {
        if (videoList.isNotEmpty()) {
            if (VideoChecker.getConnection(videoList.last().directLink)) {
                return true
            }
        }
        return false
    }

    fun breakOperation() {
        if (DataStore.readBoolean(Constants.PREFERENCE_RANK_AUTOMATIC_PLAYER) && videoList.isNotEmpty())
            DataStore.writeBoolean(Constants.BREAK_SERVER_OPERATION,true)
    }
}

enum class Player {
    Bayfiles,Embed,Fembed,Fireload,Mega,Mp4Upload,
    Okru,Onefichier,Puj,Senvid,SolidFiles,Streamtape,
    Uqload,Videobin,Zippyshare,Voe,blank
}