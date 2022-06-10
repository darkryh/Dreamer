package com.ead.project.dreamer.data.database.model

import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.server.*
import javax.inject.Inject

class ServerFactory @Inject constructor (
    private val embeddedServerList: MutableList<String>,
) {

    fun getServers() : MutableList<Server> {
        val serverList : MutableList<Server> = mutableListOf()
        for (server in embeddedServerList) {
            when (Server.identify(server)) {
                Constants.TITLE_OKRU -> serverList.add(Okru(server))
                Constants.TITLE_SOLIDFILES -> serverList.add(SolidFiles(server))
                Constants.TITLE_FEMBED -> serverList.add(Fembed(server))
                Constants.TITLE_ONEFICHIER -> serverList.add(Onefichier(server))
                //Constants.TITLE_FIRELOAD -> serverList.add(Fireload(server))
                Constants.TITLE_SENDVID -> serverList.add(Senvid(server))
                Constants.TITLE_BAYFILES -> serverList.add(Bayfiles(server))
                Constants.TITLE_ZIPPYSHARE -> serverList.add(Zippyshare(server))
                Constants.TITLE_STREAMTAPE -> serverList.add(Streamtape(server))
                Constants.TITLE_PUJ -> serverList.add(Puj(server))
                Constants.TITLE_VIDEOBIN -> serverList.add(Videobin(server))
                Constants.TITLE_EMBED -> serverList.add(Embed(server))
                Constants.TITLE_MP4UPLOAD -> serverList.add(Mp4Upload(server))
                Constants.TITLE_UQLOAD -> serverList.add(Uqload(server))
                Constants.TITLE_MEGA -> serverList.add(Mega(server))
                else -> serverList.add(NullServer(server))
            }
            if (Server.isOperationBreak()) {
                Server.endOperation()
                break
            }
        }
        return serverList
    }
}