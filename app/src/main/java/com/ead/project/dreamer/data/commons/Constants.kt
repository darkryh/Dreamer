package com.ead.project.dreamer.data.commons

import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.data.models.discord.User
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.NotificationManager

class Constants {

    companion object {

        const val PROVIDER_URL = "https://monoschinos2.com/"

        const val PROVIDER_NEWS_URL = "https://somoskudasai.com/"

        const val LIST = "animes/"

        const val PAGE = "animes?p="

        const val API_APP = "https://darkryh.github.io/Api_Dreamer/"

        const val WEB_APP = "https://dreamer-ead.net/"

        const val PLAY_STORE_APP = "https://play.google.com/store/apps/details?id=com.ead.project.dreamer"

        const val DREAMER_TOPIC = "Dreamer_Topic"

        const val LOGO_URL = "https://i.ibb.co/6nfLSKL/logo-app.png"

        //CONSTANTS

        const val BLANK_BROWSER = "about:blank"

        const val MENU_PLAYER_FRAGMENT = "MENU_PLAYER_FRAGMENT"

        const val MENU_CHAPTER_SETTINGS = "MENU_CHAPTER_SETTINGS"

        const val CHAPTER_CHECKER_FRAGMENT = "CHAPTER_CHECKER_FRAGMENT"

        const val CAP_BLANK_MC2 = "https://monoschinos2.com/public/img/capblank.png"

        const val PLAY_VIDEO_LIST = "PLAY_VIDEO_LIST"

        const val QUOTATION = '"'

        const val CONTACT_EMAIL = "darkryhsthreatment@gmail.com"

        const val SERVER_VIDEOS = "CONSTANT_SERVER_VIDEOS"

        const val REQUESTED_CHAPTER = "REQUESTED_CHAPTER"

        const val REQUESTED_IS_DIRECT = "REQUESTED_IS_DIRECT"

        const val REQUESTED_NEWS = "REQUESTED_NEWS"

        private const val VERSION_UPDATE = "VERSION_UPDATE"

        const val IS_FROM_CONTENT_PLAYER = "IS_FROM_CONTENT_PLAYER"

        const val IS_DATA_FOR_DOWNLOADING_MODE = "IS_DATA_FOR_DOWNLOADING_MODE"

        const val IS_CORRECT_DATA_FROM_CHAPTER_SETTINGS = "IS_CORRECT_DATA_FROM_CHAPTER_SETTINGS"

        const val IS_CORRECT_DATA_FROM_RECORDS_SETTINGS = "IS_CORRECT_DATA_FROM_RECORDS_SETTINGS"

        const val SYNC_DIRECTORY = "CONSTANT_SYNC_DIRECTORY"

        const val PREFERENCE_DIRECTORY_PROFILE = "SYNC_DIRECTORY_PROFILE"

        const val SYNC_HOME = "CONSTANT_SYNC_HOME"

        const val SYNC_NEWS = "SYNC_NEWS"

        const val SYNC_NEW_CONTENT = "SYNC_NEW_CONTENT"

        const val SYNC_RELEASES = "SYNC_RELEASES"

        private const val SYNC_NOTIFICATIONS_FIRST_TIME = "SYNC_NOTIFICATIONS_FIRST_TIME"

        const val SYNC_SCRAPPER = "SYNC_SCRAPPER"

        const val DIRECTORY_KEY = "DIRECTORY_KEY"

        const val ANIME_PROFILE_KEY = "ANIME_PROFILE_KEY"

        const val CHAPTER_PROFILE_KEY = "CHAPTER_PROFILE_KEY"

        private const val ANIME_PROFILE_FIXER_KEY = "ANIME_PROFILE_FIXER_KEY"

        const val SYNC_PROFILE_CHECKER = "SYNC_PROFILE_CHECKER"

        const val SYNC_CHAPTER_SIZE = "SYNC_CHAPTER_SIZE"

        const val SYNC_PROFILE_FIXER_CHECKER = "SYNC_PROFILE_FIXER_CHECKER"

        const val SYNC_CHAPTER_FIXER_SIZE = "SYNC_CHAPTER_FIXER_SIZE"

        const val PROFILE_FINAL_STATE = "Finalizado"

        const val PROFILE_RELEASE_STATE = "Estreno"

        const val TYPE_UNCENSORED = "Sin Censura"

        const val TYPE_ECCHI = "Ecchi"

        const val TYPE_BOYS_LOVE = "Yaoi"

        const val CURRENT_NOTICED_CHAPTERS_HOME = "CURRENT_NOTICED_CHAPTERS_HOME"

        const val DOWNLOADED_CHAPTERS = "DOWNLOADED_CHAPTERS"

        const val PROFILE_REPOSITORY = "PROFILE_REPOSITORY"

        const val CURRENT_EXECUTED_CHAPTER = "CURRENT_EXECUTED_CHAPTER"

        const val PROFILE_SENDER_VIDEO_PLAYER = "PROFILE_SENDER_PIP"

        const val VALUE_VIDEO_PLAYER_LINK = "VALUE_PIP_MODE_LINK"

        const val VALUE_VIDEO_PLAYER_ID_PROFILE = "VALUE_VIDEO_PLAYER_ID_PROFILE"

        const val MS_CLICK_EFFECT_MEDIUM = 175L

        const val FINAL_DIRECTORY = "FINAL_DIRECTORY"

        const val USED_ACCESS_TOKEN = "USED_ACCESS_TOKEN"

        const val WEB_ACTION = "WEB_ACTION"

        const val WEB_ACTION_URL = "WEB_ACTION_URL"

        const val CURRENT_CASTING_CHAPTER = "CURRENT_CASTING_CHAPTER"

        const val CURRENT_PREVIOUS_CASTING_CHAPTER = "CURRENT_PREVIOUS_CASTING_CHAPTER"

        const val CASTING_MODE_APP = "CASTING_MODE_APP"

        const val BREAK_SERVER_OPERATION = "BREAK_SERVER_OPERATION"

        const val ANIME_BASE_SCRAP = "ANIME_BASE_SCRAP"

        const val ANIME_PROFILE_SCRAP = "ANIME_PROFILE_SCRAP"

        const val CHAPTER_HOME_SCRAP = "CHAPTER_HOME_SCRAP"

        const val CHAPTER_SCRAP = "CHAPTER_SCRAP"

        const val NEWS_ITEM_SCRAP = "NEWS_ITEM_SCRAP"

        const val NEWS_ITEM_WEB_SCRAP = "NEWS_ITEM_WEB_SCRAP"

        //PREFERENCES

        private const val PREFERENCE_DOWNLOAD_MODE = "PREFERENCE_DOWNLOAD_MODE"

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

        const val PREFERENCE_OFFICIAL_ADVERTISER = "PREFERENCE_OFFICIAL_ADVERTISER"

        const val PREFERENCE_CUSTOMIZED_IMV_PROFILE = "PREFERENCE_CUSTOMIZED_IMV_PROFILE"

        const val PREFERENCE_CUSTOMIZED_DIAGNOSTIC_VIEW = "PREFERENCE_CUSTOMIZED_DIAGNOSTIC_VIEW"

        const val PREFERENCE_PIP_MODE_PLAYER = "PREFERENCE_PIP_MODE_PLAYER"

        private const val PREFERENCE_CURRENT_WATCHED_VIDEOS = "PREFERENCE_CURRENT_WATCHED_VIDEOS"

        const val PREFERENCE_TERMS_AND_CONDITIONS = "PREFERENCE_TERMS_AND_CONDITIONS"

        const val PREFERENCE_RESIZING_MODE = "PREFERENCE_RESIZING_MODE"

        const val PREFERENCE_GOOGLE_POLICY = "PREFERENCE_GOOGLE_POLICY"

        const val PREFERENCE_SKIP_LOGIN = "PREFERENCE_SKIP_LOGIN"

        const val PREFERENCE_APP_VERSION = "PREFERENCE_APP_VERSION"

        const val PREFERENCE_CLICK_FIXER = "PREFERENCE_CLICK_FIXER"

        const val PREFERENCE_SERVER_SCRIPT = "PREFERENCE_SERVER_SCRIPT"

        private const val IS_FIRST_DOWNLOAD_CHECK = "IS_FIRST_DOWNLOAD_CHECK"

        private const val IS_FIRST_DIRECTORY_INSTALL = "IS_FIRST_DIRECTORY_INSTALL"

        const val MINIMUM_VERSION_REQUIRED = "MINIMUM_VERSION_REQUIRED"

        const val VERSION_DEPRECATED = "VERSION_DEPRECATED"

        const val IS_THE_APP_FROM_GOOGLE = "IS_THE_APP_FROM_GOOGLE"

        private const val QUANTITY_ADS_PLAYER = "QUANTITY_ADS_PLAYER"

        private const val PREFERENCE_QUANTITY_VIDEO_LIMIT = "PREFERENCE_QUANTITY_VIDEO_LIMIT"

        private const val DEFAULT_AD_LIMIT = 4

        private const val DEFAULT_VIDEO_LIMIT = 3

        const val CAST_STREAM_DURATION = "CAST_STREAM_DURATION"

        private const val NOTIFICATIONS_INDEX = "NOTIFICATIONS_INDEX"

        const val HOME_ITEMS_LIMIT = 25



        //WORK PREFERENCES

        const val WORK_PREFERENCE_CLICKED_CHAPTER = "WORK_PREFERENCE_CLICKED_CHAPTER"

        const val WORK_PREFERENCE_CLICKED_PROFILE = "WORK_PREFERENCE_CLICKED_PROFILE"

        const val WORK_PREFERENCE_CLICKED_PROFILE_SUGGESTION = "WORK_PREFERENCE_CLICKED_PROFILE_SUGGESTION"

        //PLAYER

        const val TITLE_FEMBED = "Fembed"

        const val TITLE_DOOD_STREAM = "DoodStream"

        const val TITLE_PUJ = "Puj"

        const val TITLE_VIDEOBIN = "Videobin"

        const val TITLE_MP4UPLOAD = "Mp4upload"

        const val TITLE_OKRU = "Ok.ru"

        const val TITLE_STREAMSB = "StreamSB"

        const val TITLE_UQLOAD = "Uqload"

        const val TITLE_STREAMTAPE = "Streamtape"

        const val TITLE_SOLIDFILES = "Solidfiles"

        const val TITLE_SENDVID = "Senvid"

        const val TITLE_BAYFILES = "Bayfiles"

        const val TITLE_ZIPPYSHARE = "Zippyshare"

        const val TITLE_MEGA = "Mega"

        const val TITLE_ONEFICHIER = "1Fichier"

        const val TITLE_FIRELOAD = "Fireload"

        const val TITLE_VOE = "Voe"

        const val TITLE_UPTOBOX = "Uptobox"

        const val TITLE_ANONFILE = "Anonfile"

        const val TITLE_YOUR_UPLOAD = "YourUpload"

        const val TITLE_MEGA_UP = "MegaUp"

        const val TITLE_GOOGLE_DRIVE = "Google Drive"

        const val TITLE_MEDIAFIRE = "Mediafire"

        const val TITLE_VIDLOX = "Vidlox"


        const val SERVER_FEMBED = "fembed.com"

        const val SERVER_DOOD_STREAM = "doodstream.com"

        const val SERVER_PUJ= "repro.monoschinos2.com/aqua"

        const val SERVER_VIDEOBIN = "videobin.co"

        const val SERVER_MP4UPLOAD = "mp4upload.com"

        const val SERVER_OKRU = "ok.ru"

        val SERVER_STREAMSB_DOMAINS = listOf("sblanh.com","sbchill.com","sblongvu.com", "sbanh.com", "playersb.com","embedsb.com","sbspeed.com","tubesb.com")

        const val SERVER_UQLOAD = "uqload.com"

        const val SERVER_STREAMTAPE = "streamtape.com"

        const val SERVER_SOLIDFILES = "solidfiles.com"

        const val SERVER_SENDVID = "sendvid.com"

        const val SERVER_BAYFILES = "bayfiles.com"

        const val SERVER_ZIPPYSHARE = "zippyshare.com"

        const val SERVER_MEGA = "mega.nz"

        const val SERVER_ONEFICHIER = "1fichier.com"

        const val SERVER_FIRELOAD = "fireload.com"

        const val SERVER_VOE = "voe.sx"

        const val SERVER_UPTOBOX = "uptobox.com"

        const val SERVER_ANONFILE = "anonfile.com"

        const val SERVER_YOUR_UPLOAD = "yourupload.com"

        const val SERVER_MEGA_UP = "megaup.net"

        const val SERVER_GOOGLE_DRIVE = "drive.google.com"

        const val SERVER_MEDIAFIRE = "mediafire.com"

        const val SERVER_VIDLOX = "vidlox.me"

        // SYSTEM

        const val INSTALL_MIME_TYPE = "application/vnd.android.package-archive"
        const val FILES_PROVIDER_PATH = ".provider"

        private fun isInQuantityAdLimit() = DataStore
            .readInt(PREFERENCE_CURRENT_WATCHED_VIDEOS,1) >= DataStore
            .readInt(PREFERENCE_QUANTITY_VIDEO_LIMIT, DEFAULT_AD_LIMIT)

        private fun isInQuantityAdLimitVideo() = DataStore
            .readInt(PREFERENCE_CURRENT_WATCHED_VIDEOS,1) >= DataStore
            .readInt(PREFERENCE_QUANTITY_VIDEO_LIMIT, DEFAULT_VIDEO_LIMIT)

        fun quantityAdPlus() {
            var currentSeriesSeen = DataStore
                .readInt(PREFERENCE_CURRENT_WATCHED_VIDEOS,1)
            DataStore.writeIntAsync(PREFERENCE_CURRENT_WATCHED_VIDEOS,++currentSeriesSeen)
        }

        fun getMinimumVersion() = DataStore.readDouble(MINIMUM_VERSION_REQUIRED)

        private fun isVersionDeprecated() = DataStore.readBoolean(VERSION_DEPRECATED)

        fun isVersionNotDeprecated() = !isVersionDeprecated()

        fun isTermsAndConditionsNotNeeded() = DataStore.readBoolean(PREFERENCE_TERMS_AND_CONDITIONS) || !isAppFromGoogle()

        fun isDirectoryActivityClicked() = DataStore.readBoolean(PREFERENCE_DIRECTORY_CLICKED,true)

        fun setDirectoryActivityClicked(value: Boolean) =DataStore.writeBooleanAsync(
            PREFERENCE_DIRECTORY_CLICKED,value)

        fun isConfigurationActivityClicked() = DataStore.readBoolean(PREFERENCE_SETTINGS_CLICKED,true)

        fun setConfigurationActivityClicked(value: Boolean) = DataStore.writeBoolean(
            PREFERENCE_SETTINGS_CLICKED,value)

        fun getNotificationMode() = DataStore.readInt(PREFERENCE_NOTIFICATIONS, NotificationManager.ALL)

        fun isExternalPlayerMode() = DataStore.readBoolean(PREFERENCE_EXTERNAL_PLAYER)

        fun isAutomaticPlayerMode() = DataStore.readBoolean(PREFERENCE_RANK_AUTOMATIC_PLAYER)

        fun setAutomaticPlayerMode(value: Boolean) = DataStore.writeBoolean(PREFERENCE_RANK_AUTOMATIC_PLAYER,value)

        fun setAppFromGoogle(value : Boolean) = DataStore.writeBooleanAsync(IS_THE_APP_FROM_GOOGLE,value)

        fun isAppFromGoogle() = DataStore.readBoolean(IS_THE_APP_FROM_GOOGLE)

        fun setGooglePolicyTo(value: Boolean) = DataStore.writeBooleanAsync(
            PREFERENCE_GOOGLE_POLICY, value)

        fun isGooglePolicyActivate() = DataStore.readBoolean(PREFERENCE_GOOGLE_POLICY)

        fun isGooglePolicyNotActivate() = !isGooglePolicyActivate()

        fun isAdInterstitialTime(isDirect : Boolean) = isInQuantityAdLimit() && User.isNotVip() && (isExternalPlayerMode() || !isDirect)

        fun isAdTime() = isInQuantityAdLimitVideo() && User.isNotVip()

        fun setQuantityAdsPlayer(value : Int) = DataStore.writeInt(QUANTITY_ADS_PLAYER,value)

        fun resetCountedAds() = DataStore.writeIntAsync(PREFERENCE_CURRENT_WATCHED_VIDEOS, 0)

        fun isCustomizedCommunicator() = DataStore
            .readBoolean(PREFERENCE_OFFICIAL_ADVERTISER,true)

        fun isDirectorySynchronized() = DataStore
            .readBoolean(PREFERENCE_DIRECTORY_PROFILE)

        fun isProfileFixerLaunched() = DataStore.flowBoolean(ANIME_PROFILE_FIXER_KEY).asLiveData()

        fun setProfileFixer(value: Boolean) = DataStore.writeBoolean(ANIME_PROFILE_FIXER_KEY,value)

        fun getNotificationIndex() = DataStore.readInt(NOTIFICATIONS_INDEX,0)

        fun setNotificationIndex(value : Int) = DataStore.writeIntAsync(NOTIFICATIONS_INDEX,value)

        fun isDarkThemeMode() = DataStore.readBoolean(PREFERENCE_THEME_MODE)

        fun getDownloadMode() = DataStore.readBoolean(PREFERENCE_DOWNLOAD_MODE)

        fun setDownloadMode(value: Boolean) = DataStore.writeBooleanAsync(PREFERENCE_DOWNLOAD_MODE,value)

        fun isActiveFirebaseNotifications() = DataStore.readBoolean(DREAMER_TOPIC,true)

        fun isFirstDirectoryInstall() = DataStore.readBoolean(IS_FIRST_DIRECTORY_INSTALL,true)

        fun disableDirectoryInstall() = DataStore.writeBooleanAsync(IS_FIRST_DIRECTORY_INSTALL,false)

        fun isDownloadFirstCheck() = DataStore.readBoolean(IS_FIRST_DOWNLOAD_CHECK,true)

        fun disableDownloadCheck() = DataStore.writeBooleanAsync(IS_FIRST_DOWNLOAD_CHECK,false)

        fun getPlayerPipMode() = DataStore.
        readBoolean(PREFERENCE_PIP_MODE_PLAYER,true)

        fun isFirstTimeShowingNotification() =
            DataStore.readBoolean(SYNC_NOTIFICATIONS_FIRST_TIME,true)

        fun disableFirstTimeShowingNotifications() =
            DataStore.writeBoolean(SYNC_NOTIFICATIONS_FIRST_TIME,false)

        fun setVersionUpdateRoute(code: String) = DataStore.writeString(VERSION_UPDATE,code)

        fun getVersionUpdateRoute() : String = DataStore.readString(VERSION_UPDATE)


    }
}