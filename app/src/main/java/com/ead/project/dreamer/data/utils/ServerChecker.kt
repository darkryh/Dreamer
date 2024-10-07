package com.ead.project.dreamer.data.utils

import com.ead.lib.moongetter.MoonGetter
import com.ead.lib.moongetter.core.Properties
import com.ead.lib.moongetter.server_sites.Mediafire
import com.ead.lib.moongetter.server_sites.Okru
import com.ead.lib.moongetter.server_sites.Onefichier
import com.ead.lib.moongetter.server_sites.PixelDrain
import com.ead.lib.moongetter.server_sites.Senvid
import com.ead.lib.moongetter.server_sites.StreamWish
import com.ead.lib.moongetter.server_sites.Streamtape
import com.ead.lib.moongetter.server_sites.Voe
import com.ead.project.dreamer.app.App
import com.ead.project.dreamer.app.data.server.Server
import com.ead.project.dreamer.app.data.util.system.contains
import com.ead.project.dreamer.data.models.server.GoodStream
import com.ead.project.dreamer.data.models.server.Mp4Upload


class ServerChecker {

    companion object {

        fun identify(url: String): String {
            return MoonGetter
                .initialize(App.Instance)
                .connect(url)
                .setCustomServers(
                    Server.serverIntegrationList
                )
                .identifier()
                ?: if (url.contains(Server.URL_SOLIDFILES)) Server.SOLIDFILES
                else if (url.contains(Server.URL_ZIPPYSHARE)) return Server.ZIPPYSHARE
                else if (url.contains(Server.URL_VIDEOBIN)) return Server.VIDEOBIN
                else if (url.contains(Server.URL_FILELIONS_DOMAINS)) return Server.FILELIONS
                else if (url.contains(Server.URL_MIXDROP)) return Server.MIXDROP
                else if (url.contains(Server.URL_VIDGUARD_DOMAINS)) return Server.VIDGUARD
                else if (url.contains(Server.URL_MP4UPLOAD)) return Server.MP4UPLOAD
                else if (url.contains(Server.URL_UQLOAD_DOMAINS)) return Server.UQLOAD
                else if (url.contains(Server.URL_MEGA)) return Server.MEGA
                else if (url.contains(Server.URL_UPTOBOX)) return Server.UPTOBOX
                else if (url.contains(Server.URL_ANONFILE)) return  Server.ANONFILE
                else if (url.contains(Server.URL_YOUR_UPLOAD)) return Server.YOUR_UPLOAD
                else if (url.contains(Server.URL_MEGA_UP)) return Server.MEGA_UP
                else if (url.contains(Server.URL_STREAMSB_DOMAINS)) return Server.STREAMSB
                else if (url.contains(Server.URL_VIDLOX)) return Server.VIDLOX
                else  "null"
        }

        private fun recommendedServers(): List<String> = listOf(
            GoodStream::class.java.simpleName,
            Okru::class.java.simpleName, Onefichier::class.java.simpleName,
            StreamWish::class.java.simpleName,
            Mediafire::class.java.simpleName, Server.STREAMSB,
            Senvid::class.java.simpleName,
            PixelDrain::class.java.simpleName,Voe::class.java.simpleName,
            Streamtape::class.java.simpleName, Properties.OneFichierIdentifier
        )


        private fun webServers(): List<String> = listOf(
            Mp4Upload::class.java.simpleName,
            Server.MP4UPLOAD, Server.MEGA,
            Server.UQLOAD, Server.FILEMOON,
            Server.MIXDROP, Server.YOUR_UPLOAD,
            Server.FILELIONS, Server.VIDGUARD,
            Server.DOOD_STREAM
        )

        fun isRecommended(server: String): Boolean = recommendedServers().contains(server)

        fun isWebServer(server: String): Boolean = webServers().contains(server)

        fun isOtherServer(server: String) : Boolean =
            !isRecommended(server) && !isWebServer(server) && server != "null"
    }
}