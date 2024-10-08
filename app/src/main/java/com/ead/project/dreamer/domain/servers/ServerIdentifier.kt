package com.ead.project.dreamer.domain.servers

import com.ead.project.dreamer.app.data.server.Server
import com.ead.project.dreamer.app.data.util.system.contains

import javax.inject.Inject

class ServerIdentifier @Inject constructor() {

    operator fun invoke(url:String) : String {
        if (url.contains(Server.URL_OKRU)) return Server.OKRU
        if (url.contains(Server.URL_SOLIDFILES)) return Server.SOLIDFILES
        if (url.contains(Server.URL_ONEFICHIER)) return Server.ONEFICHIER
        if (url.contains(Server.URL_GOODSTREAM)) return Server.GOODSTREAM
        if (url.contains(Server.URL_GOOGLE_DRIVE)) return Server.GOOGLE_DRIVE
        if (url.contains(Server.URL_FEMBED)) return Server.FEMBED
        if (url.contains(Server.URL_SENDVID)) return Server.SENDVID
        if (url.contains(Server.URL_DOOD_STREAM)) return Server.DOOD_STREAM
        if (url.contains(Server.URL_BAYFILES)) return Server.BAYFILES
        if (url.contains(Server.URL_ZIPPYSHARE)) return Server.ZIPPYSHARE
        if (url.contains(Server.URL_MEDIAFIRE)) return Server.MEDIAFIRE
        if (url.contains(Server.URL_PIXELDRAIN)) return Server.PIXELDRAIN
        if (url.contains(Server.URL_FIRELOAD)) return Server.FIRELOAD
        if (url.contains(Server.URL_STREAMTAPE)) return Server.STREAMTAPE
        if (url.contains(Server.URL_VOE)) return Server.VOE
        if (url.contains(Server.URL_PUJ)) return Server.PUJ
        if (url.contains(Server.URL_VIDEOBIN)) return Server.VIDEOBIN
        if (url.contains(Server.URL_MP4UPLOAD)) return Server.MP4UPLOAD
        if (url.contains(Server.URL_FILEMOON)) return Server.FILEMOON
        if (url.contains(Server.URL_FILELIONS_DOMAINS)) return Server.FILELIONS
        if (url.contains(Server.URL_STREAMWISH_DOMAINS)) return Server.STREAMWISH
        if (url.contains(Server.URL_VIDGUARD_DOMAINS)) return Server.VIDGUARD
        if (url.contains(Server.URL_MIXDROP)) return Server.MIXDROP
        if (url.contains(Server.URL_UQLOAD_DOMAINS)) return Server.UQLOAD
        if (url.contains(Server.URL_MEGA)) return Server.MEGA
        if (url.contains(Server.URL_UPTOBOX)) return Server.UPTOBOX
        if (url.contains(Server.URL_ANONFILE)) return  Server.ANONFILE
        if (url.contains(Server.URL_YOUR_UPLOAD)) return Server.YOUR_UPLOAD
        if (url.contains(Server.URL_MEGA_UP)) return Server.MEGA_UP
        if (url.contains(Server.URL_STREAMSB_DOMAINS)) return Server.STREAMSB
        if (url.contains(Server.URL_VIDLOX)) return Server.VIDLOX
        return "null"
    }
}