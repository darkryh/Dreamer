package com.ead.project.dreamer.data.commons

import com.ead.project.dreamer.data.utils.DataStore

class Constants {

    companion object {

        const val BASE_URL = "https://monoschinos2.com/"

        const val LIST = "animes/"

        const val PAGE = "animes?p="

        const val API_APP = "https://my-json-server.typicode.com/Darkryh/Api_Dreamer/"

        const val BLOG_APP = "https://eadreference.blogspot.com/2022/03/dreamer.html"

        const val PLAY_STORE_APP = "https://play.google.com/store/apps/details?id=com.ead.project.dreamer"

        const val DREAMER_TOPIC = "Dreamer_Topic"

        //CONSTANTS

        const val BLANK_BROWSER = "about:blank"

        const val MENU_PLAYER_FRAGMENT = "MENU_PLAYER_FRAGMENT"

        const val CHAPTER_CHECKER_FRAGMENT = "CHAPTER_CHECKER_FRAGMENT"

        const val PLAY_VIDEO_LIST = "PLAY_VIDEO_LIST"

        const val QUOTATION = '"'

        const val CONTACT_EMAIL = "darkryhsthreatment@gmail.com"

        const val SERVER_VIDEOS = "CONSTANT_SERVER_VIDEOS"

        const val REQUESTED_CHAPTER = "REQUESTED_CHAPTER"

        const val IS_FROM_CONTENT_PLAYER = "IS_FROM_CONTENT_PLAYER"

        const val SYNC_DIRECTORY = "CONSTANT_SYNC_DIRECTORY"

        const val PREFERENCE_DIRECTORY_PROFILE = "SYNC_DIRECTORY_PROFILE"

        const val SYNC_HOME = "CONSTANT_SYNC_HOME"

        const val SYNC_NEW_CONTENT = "SYNC_NEW_CONTENT"

        const val SYNC_RELEASES = "SYNC_RELEASES"

        const val SYNC_SERIES_NOTIFICATIONS = "SYNC_SERIES_NOTIFICATIONS"

        const val DIRECTORY_KEY = "DIRECTORY_KEY"

        const val ANIME_PROFILE_KEY = "ANIME_PROFILE_KEY"

        const val CHAPTER_PROFILE_KEY = "CHAPTER_PROFILE_KEY"

        const val SYNC_PROFILE_CHECKER = "SYNC_PROFILE_CHECKER"

        const val SYNC_CHAPTER_SIZE = "SYNC_CHAPTER_SIZE"

        const val SYNC_CHAPTER_SIZE_CHECKER = "SYNC_CHAPTER_SIZE_CHECKER"

        const val PROFILE_FINAL_STATE = "Finalizado"

        const val PROFILE_RELEASE_STATE = "Estreno"

        const val TYPE_UNCENSORED = "Sin Censura"

        const val TYPE_ECCHI = "Ecchi"

        const val TYPE_BOYS_LOVE = "Yaoi"

        const val TYPE_LATIN = "Latino"

        const val TYPE_SHONEN = "Shonen"

        const val TYPE_ROMANCE = "Romance"

        const val TYPE_COMEDY = "Comedia"

        const val TYPE_DRAMA = "Drama"

        const val TYPE_MYSTERY = "Misterio"

        const val CURRENT_NOTICED_CHAPTERS_HOME = "CURRENT_NOTICED_CHAPTERS_HOME"

        const val PROFILE_REPOSITORY = "PROFILE_REPOSITORY"

        const val CURRENT_EXECUTED_CHAPTER = "CURRENT_EXECUTED_CHAPTER"

        const val CURRENT_EXECUTED_PLAYLIST = "CURRENT_EXECUTED_PLAYLIST"

        const val PROFILE_SENDER_VIDEO_PLAYER = "PROFILE_SENDER_PIP"

        const val VALUE_VIDEO_PLAYER_LINK = "VALUE_PIP_MODE_LINK"

        const val VALUE_VIDEO_PLAYER_ID_PROFILE = "VALUE_VIDEO_PLAYER_ID_PROFILE"

        const val AUTHORIZATION_VALUE = "Authorization="

        const val MS_CLICK_EFFECT_MEDIUM = 175L

        const val FINAL_DIRECTORY = "FINAL_DIRECTORY"

        const val USED_ACCESS_TOKEN = "USED_ACCESS_TOKEN"

        const val WEB_ACTION = "WEB_ACTION"

        const val WEB_ACTION_URL = "WEB_ACTION_URL"

        const val CURRENT_CASTING_CHAPTER = "CURRENT_CASTING_CHAPTER"

        const val CURRENT_PREVIOUS_CASTING_CHAPTER = "CURRENT_PREVIOUS_CASTING_CHAPTER"

        const val CASTING_MODE_APP = "CASTING_MODE_APP"

        //PREFERENCES

        const val PREFERENCE_DIRECTORY_CLICKED = "PREFERENCE_DIRECTORY_CLICKED"

        const val PREFERENCE_SETTINGS_CLICKED = "PREFERENCE_SETTINGS_CLICKED"

        const val PREFERENCE_NOTIFICATIONS = "PREFERENCE_NOTIFICATIONS"

        const val PREFERENCE_LINK = "PREFERENCE_LINK"

        const val PREFERENCE_ID_BASE = "ID_BASE"

        const val PREFERENCE_THEME_MODE = "PREFERENCE_THEME_MODE"

        const val PREFERENCE_RANK_AUTOMATIC_PLAYER = "PREFERENCE_RANK_AUTOMATIC_PLAYER"

        const val PREFERENCE_EXTERNAL_PLAYER = "PREFERENCE_EXTERNAL_PLAYER"

        const val PREFERENCE_ADVISER = "PREFERENCE_ADVISER"

        const val PREFERENCE_SESSION = "PREFERENCE_SESSION"

        const val PREFERENCE_CUSTOMIZE_COMMUNICATORS = "PREFERENCE_CUSTOMIZE_COMMUNICATORS"

        const val PREFERENCE_CUSTOMIZED_IMV_PROFILE = "PREFERENCE_CUSTOMIZED_IMV_PROFILE"

        const val PREFERENCE_PIP_MODE_PLAYER = "PREFERENCE_PIP_MODE_PLAYER"

        const val PREFERENCE_CURRENT_WATCHED_VIDEOS = "PREFERENCE_CURRENT_WATCHED_VIDEOS"

        const val PREFERENCE_TERMS_AND_CONDITIONS = "PREFERENCE_TERMS_AND_CONDITIONS"

        const val PREFERENCE_RESIZING_MODE = "PREFERENCE_RESIZING_MODE"

        const val PREFERENCE_GOOGLE_POLICY = "PREFERENCE_GOOGLE_POLICY"

        const val PREFERENCE_SKIP_LOGIN = "PREFERENCE_SKIP_LOGIN"

        const val PREFERENCE_APP_VERSION = "PREFERENCE_APP_VERSION"

        const val MINIMUM_VERSION_REQUIRED = "MINIMUM_VERSION_REQUIRED"

        const val VERSION_DEPRECATED = "VERSION_DEPRECATED"

        const val IS_THE_APP_FROM_GOOGLE = "IS_THE_APP_FROM_GOOGLE"

        private const val QUANTITY_ADS_PLAYER = "QUANTITY_ADS_PLAYER"

        private const val PREFERENCE_QUANTITY_VIDEO_LIMIT = "PREFERENCE_QUANTITY_VIDEO_LIMIT"

        private const val DEFAULT_VIDEO_LIMIT = 4

        //WORK PREFERENCES

        const val WORK_PREFERENCE_CLICKED_CHAPTER = "WORK_PREFERENCE_CLICKED_CHAPTER"

        const val WORK_PREFERENCE_CLICKED_PROFILE = "WORK_PREFERENCE_CLICKED_PROFILE"

        const val WORK_PREFERENCE_CLICKED_PROFILE_SUGGESTION = "WORK_PREFERENCE_CLICKED_PROFILE_SUGGESTION"

        //PLAYER

        const val TITLE_FEMBED = "Fembed"

        const val TITLE_PUJ = "Puj"

        const val TITLE_VIDEOBIN = "Videobin"

        const val TITLE_MP4UPLOAD = "Mp4upload"

        const val TITLE_OKRU = "Ok.ru"

        const val TITLE_EMBED = "Embed"

        const val TITLE_UQLOAD = "Uqload"

        const val TITLE_STREAMTAPE = "Streamtape"

        const val TITLE_SOLIDFILES = "Solidfiles"

        const val TITLE_SENDVID = "Senvid"

        const val TITLE_BAYFILES = "Bayfiles"

        const val TITLE_ZIPPYSHARE = "Zippyshare"

        const val TITLE_MEGA = "Mega"

        const val TITLE_ONEFICHIER = "1Fichier"


        const val SERVER_FEMBED = "fembed.com"

        const val SERVER_PUJ= "repro.monoschinos2.com/aqua"

        const val SERVER_VIDEOBIN = "videobin.co"

        const val SERVER_MP4UPLOAD = "mp4upload.com"

        const val SERVER_OKRU = "ok.ru"

        const val SERVER_EMBED = "embedsb.com"

        const val SERVER_UQLOAD = "uqload.com"

        const val SERVER_STREAMTAPE = "streamtape.com"

        const val SERVER_SOLIDFILES = "solidfiles.com"

        const val SERVER_SENDVID = "sendvid.com"

        const val SERVER_BAYFILES = "bayfiles.com"

        const val SERVER_ZIPPYSHARE = "zippyshare.com"

        const val SERVER_MEGA = "mega.nz"

        const val SERVER_ONEFICHIER = "1fichier.com"

        fun isInQuantityAdLimit() = DataStore
            .readInt(PREFERENCE_CURRENT_WATCHED_VIDEOS,1) >= DataStore
            .readInt(PREFERENCE_QUANTITY_VIDEO_LIMIT, DEFAULT_VIDEO_LIMIT)

        fun quantityAdPlus() {
            var currentSeriesSeen = DataStore
                .readInt(PREFERENCE_CURRENT_WATCHED_VIDEOS,1)
            DataStore.writeIntAsync(PREFERENCE_CURRENT_WATCHED_VIDEOS,++currentSeriesSeen)
        }

        fun isExternalPlayerMode() = DataStore.readBoolean(PREFERENCE_EXTERNAL_PLAYER)

        fun isGooglePolicyActivate() = DataStore.readBoolean(PREFERENCE_GOOGLE_POLICY)

        fun setQuantityAdsPlayer(value : Int) = DataStore.writeIntAsync(QUANTITY_ADS_PLAYER,value)

        fun isCustomizedCommunicator() = DataStore
            .readBoolean(PREFERENCE_CUSTOMIZE_COMMUNICATORS,true)

        fun isDirectorySynchronized() = DataStore
            .readBoolean(PREFERENCE_DIRECTORY_PROFILE)
    }
}