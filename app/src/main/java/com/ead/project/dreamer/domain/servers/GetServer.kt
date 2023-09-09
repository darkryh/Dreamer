package com.ead.project.dreamer.domain.servers

import com.ead.project.dreamer.app.data.server.Server
import com.ead.project.dreamer.data.models.server.*
import javax.inject.Inject

class GetServer @Inject constructor(
    private val serverIdentifier: ServerIdentifier
) {

    operator fun invoke(url : String) : com.ead.project.dreamer.data.models.Server =
        when (serverIdentifier(url)) {
            Server.OKRU -> Okru(url)
            Server.FEMBED -> Fembed(url)
            Server.ONEFICHIER -> Onefichier(url)
            Server.STREAMSB -> StreamSB(url)
            Server.SENDVID -> Senvid(url)
            Server.DOOD_STREAM -> DoodStream(url)
            Server.SOLIDFILES -> SolidFiles(url)
            Server.BAYFILES -> Bayfiles(url)
            Server.VIDEOBIN -> Videobin(url)
            Server.ZIPPYSHARE -> Zippyshare(url)
            Server.MEDIAFIRE -> Mediafire(url)
            Server.PIXELDRAIN -> PixelDrain(url)
            Server.STREAMTAPE -> Streamtape(url)
            Server.GOOGLE_DRIVE -> GoogleDrive(url)
            Server.PUJ -> Puj(url)
            Server.VOE -> Voe(url)
            Server.UPTOBOX -> Uptobox(url)
            Server.ANONFILE -> Anonfiles(url)
            Server.MEGA_UP -> MegaUp(url)
            Server.FIRELOAD -> Fireload(url)
            Server.MP4UPLOAD -> Mp4Upload(url)
            Server.FILEMOON -> FileMoon(url)
            Server.STREAMWISH -> StreamWish(url)
            Server.MIXDROP -> MixDrop(url)
            Server.UQLOAD -> Uqload(url)
            Server.MEGA -> Mega(url)
            Server.VIDLOX -> Vidlox(url)
            Server.YOUR_UPLOAD -> YourUpload(url)
            else -> NullServer(url)
        }
}