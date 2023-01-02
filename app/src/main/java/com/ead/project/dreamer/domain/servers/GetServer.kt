package com.ead.project.dreamer.domain.servers
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.models.server.*
import javax.inject.Inject

class GetServer @Inject constructor(
    private val serverIdentifier: ServerIdentifier
) {

    operator fun invoke(url : String) : Server =
        when (serverIdentifier(url)) {
            Constants.TITLE_OKRU -> Okru(url)
            Constants.TITLE_FEMBED -> Fembed(url)
            Constants.TITLE_ONEFICHIER -> Onefichier(url)
            Constants.TITLE_STREAMSB -> StreamSB(url)
            Constants.TITLE_SENDVID -> Senvid(url)
            Constants.TITLE_DOOD_STREAM -> DoodStream(url)
            Constants.TITLE_SOLIDFILES -> SolidFiles(url)
            Constants.TITLE_BAYFILES -> Bayfiles(url)
            Constants.TITLE_VIDEOBIN -> Videobin(url)
            Constants.TITLE_ZIPPYSHARE -> Zippyshare(url)
            Constants.TITLE_MEDIAFIRE -> Mediafire(url)
            Constants.TITLE_STREAMTAPE -> Streamtape(url)
            Constants.TITLE_GOOGLE_DRIVE -> GoogleDrive(url)
            Constants.TITLE_PUJ -> Puj(url)
            Constants.TITLE_VOE -> Voe(url)
            Constants.TITLE_UPTOBOX -> Uptobox(url)
            Constants.TITLE_ANONFILE -> Anonfiles(url)
            Constants.TITLE_MEGA_UP -> MegaUp(url)
            Constants.TITLE_FIRELOAD -> Fireload(url)
            Constants.TITLE_MP4UPLOAD -> Mp4Upload(url)
            Constants.TITLE_UQLOAD -> Uqload(url)
            Constants.TITLE_MEGA -> Mega(url)
            Constants.TITLE_VIDLOX -> Vidlox(url)
            Constants.TITLE_YOUR_UPLOAD -> YourUpload(url)
            else -> NullServer(url)
        }
}