package com.ead.project.dreamer.data.utils

import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools.Companion.contains
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.server.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ServerManager @Inject constructor () {

    companion object {

        suspend fun getData(list: List<String>) : List<Server> {
            return withContext(Dispatchers.IO) {
                getServersList(list.toMutableList())
            }
        }

        fun getServersList(embeddedServerList : MutableList<String>) : List<Server> {
            val serverList : MutableList<Server> = mutableListOf()
            for (serverUrl in embeddedServerList) {
                serverList.add(getServer(serverUrl))
                if (Server.isOperationBreak()) {
                    Server.endOperation()
                    break
                }
            }
            return serverList
        }

        fun getServer(serverUrl : String) : Server {
            return when (identify(serverUrl)) {
                Constants.TITLE_OKRU -> Okru(serverUrl)
                Constants.TITLE_FEMBED -> Fembed(serverUrl)
                Constants.TITLE_ONEFICHIER -> Onefichier(serverUrl)
                Constants.TITLE_STREAMSB -> StreamSB(serverUrl)
                Constants.TITLE_SENDVID -> Senvid(serverUrl)
                Constants.TITLE_SOLIDFILES -> SolidFiles(serverUrl)
                Constants.TITLE_BAYFILES -> Bayfiles(serverUrl)
                Constants.TITLE_VIDEOBIN -> Videobin(serverUrl)
                Constants.TITLE_ZIPPYSHARE -> Zippyshare(serverUrl)
                Constants.TITLE_MEDIAFIRE -> Mediafire(serverUrl)
                Constants.TITLE_STREAMTAPE -> Streamtape(serverUrl)
                Constants.TITLE_GOOGLE_DRIVE -> GoogleDrive(serverUrl)
                Constants.TITLE_PUJ -> Puj(serverUrl)
                Constants.TITLE_VOE -> Voe(serverUrl)
                Constants.TITLE_UPTOBOX -> Uptobox(serverUrl)
                Constants.TITLE_ANONFILE -> Anonfiles(serverUrl)
                Constants.TITLE_MEGA_UP -> MegaUp(serverUrl)
                Constants.TITLE_FIRELOAD -> Fireload(serverUrl)
                Constants.TITLE_MP4UPLOAD -> Mp4Upload(serverUrl)
                Constants.TITLE_UQLOAD -> Uqload(serverUrl)
                Constants.TITLE_MEGA -> Mega(serverUrl)
                else -> NullServer(serverUrl)
            }
        }

        fun identify(url: String): String {
            if (url.contains(Constants.SERVER_OKRU)) return Constants.TITLE_OKRU
            if (url.contains(Constants.SERVER_SOLIDFILES)) return Constants.TITLE_SOLIDFILES
            if (url.contains(Constants.SERVER_ONEFICHIER)) return Constants.TITLE_ONEFICHIER
            if (url.contains(Constants.SERVER_GOOGLE_DRIVE)) return Constants.TITLE_GOOGLE_DRIVE
            if (url.contains(Constants.SERVER_FEMBED)) return Constants.TITLE_FEMBED
            if (url.contains(Constants.SERVER_FIRELOAD)) return Constants.TITLE_FIRELOAD
            if (url.contains(Constants.SERVER_SENDVID)) return Constants.TITLE_SENDVID
            if (url.contains(Constants.SERVER_BAYFILES)) return Constants.TITLE_BAYFILES
            if (url.contains(Constants.SERVER_ZIPPYSHARE)) return Constants.TITLE_ZIPPYSHARE
            if (url.contains(Constants.SERVER_MEDIAFIRE)) return Constants.TITLE_MEDIAFIRE
            if (url.contains(Constants.SERVER_STREAMTAPE)) return Constants.TITLE_STREAMTAPE
            if (url.contains(Constants.SERVER_VOE)) return Constants.TITLE_VOE
            if (url.contains(Constants.SERVER_PUJ)) return Constants.TITLE_PUJ
            if (url.contains(Constants.SERVER_VIDEOBIN)) return Constants.TITLE_VIDEOBIN
            if (url.contains(Constants.SERVER_MP4UPLOAD)) return Constants.TITLE_MP4UPLOAD
            if (url.contains(Constants.SERVER_UQLOAD)) return Constants.TITLE_UQLOAD
            if (url.contains(Constants.SERVER_MEGA)) return Constants.TITLE_MEGA
            if (url.contains(Constants.SERVER_UPTOBOX)) return Constants.TITLE_UPTOBOX
            if (url.contains(Constants.SERVER_ANONFILE)) return  Constants.TITLE_ANONFILE
            if (url.contains(Constants.SERVER_YOUR_UPLOAD)) return Constants.TITLE_YOUR_UPLOAD
            if (url.contains(Constants.SERVER_MEGA_UP)) return Constants.TITLE_MEGA_UP
            if (url.contains(Constants.SERVER_STREAMSB_DOMAINS)) return Constants.TITLE_STREAMSB
            return "null"
        }

        private fun recommendedServers(): List<String> = listOf(
            Constants.TITLE_OKRU, Constants.TITLE_ONEFICHIER,
            Constants.TITLE_VIDEOBIN, Constants.TITLE_VOE,
            Constants.TITLE_MEDIAFIRE, Constants.TITLE_STREAMSB,
            Constants.TITLE_SENDVID, Constants.TITLE_ZIPPYSHARE,
            Constants.TITLE_BAYFILES
        )


        private fun webServers(): List<String> =listOf(
            Constants.TITLE_MP4UPLOAD,
            Constants.TITLE_MEGA,
            Constants.TITLE_UQLOAD
        )

        fun isRecommended(server: String?): Boolean = recommendedServers().contains(server)

        fun isWebServer(server: String?): Boolean = webServers().contains(server)
    }
}