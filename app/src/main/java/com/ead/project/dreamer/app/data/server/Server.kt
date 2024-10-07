package com.ead.project.dreamer.app.data.server

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.ead.lib.moongetter.models.ServerIntegration
import com.ead.project.dreamer.app.App
import com.ead.project.dreamer.app.model.AutomaticServerPreference
import com.ead.project.dreamer.app.model.ServerPreference
import com.ead.project.dreamer.data.models.server.DoodStream
import com.ead.project.dreamer.data.models.server.FileMoon
import com.ead.project.dreamer.data.models.server.GoodStream
import com.ead.project.dreamer.data.models.server.Mega
import com.ead.project.dreamer.data.models.server.MegaUp
import com.ead.project.dreamer.data.models.server.MixDrop
import com.ead.project.dreamer.data.models.server.Mp4Upload
import com.ead.project.dreamer.data.models.server.Puj
import com.ead.project.dreamer.data.models.server.Uptobox
import com.ead.project.dreamer.data.models.server.Uqload
import com.ead.project.dreamer.data.models.server.VidGuard
import com.ead.project.dreamer.data.models.server.Voe
import com.ead.project.dreamer.data.models.server.YourUpload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object Server {

    private val context : Context by lazy { App.Instance }
    private val scope : CoroutineScope = CoroutineScope(Dispatchers.IO +  SupervisorJob())

    private const val SERVER_PREFERENCES = "SERVER_PREFERENCES"
    private const val AUTOMATIC_SERVER_PREFERENCES = "AUTOMATIC_SERVER_PREFERENCES"

    private val store : DataStore<ServerPreference> = DataStoreFactory.create(
        serializer = ServerSerializer,
        produceFile = { context.dataStoreFile(SERVER_PREFERENCES) },
        corruptionHandler = null,
    )

    private val automaticStore : DataStore<AutomaticServerPreference> = DataStoreFactory.create(
        serializer = AutomaticServerSerializer,
        produceFile = { context.dataStoreFile(AUTOMATIC_SERVER_PREFERENCES) },
        corruptionHandler = null
    )

    val serverPreferenceFlow get() = store.data

    fun isAutomaticResolverActivated() : Boolean = runBlocking { store.data.first().isAutomatic }

    fun isProcessed() : Boolean = runBlocking { store.data.first().isProcessed }

    fun isDownloading() : Boolean = runBlocking { store.data.first().isDownloading }

    fun updateAutomaticResolver() {
        scope.launch {
            store.updateData { serverPreference: ServerPreference ->
                serverPreference.copy(
                    isAutomatic = !serverPreference.isAutomatic
                )
            }
        }
    }

    fun setProcessed(value : Boolean) {
        runBlocking {
            store.updateData { serverPreference: ServerPreference ->
                serverPreference.copy(
                    isProcessed = value
                )
            }
        }
    }

    fun setDownloading(value: Boolean) {
        scope.launch {
            store.updateData { serverPreference: ServerPreference ->
                serverPreference.copy(
                    isDownloading = value
                )
            }
        }
    }

    fun getSortedInternalServersLiveData() : LiveData<List<String>> =
        automaticStore.data.asLiveData().map { it.internalServerList }

    fun getSortedExternalServersLiveData() : LiveData<List<String>> =
        automaticStore.data.asLiveData().map { it.externalServerList }

    fun getSortedDownloadServersLiveData() : LiveData<List<String>> =
        automaticStore.data.asLiveData().map { it.downloadServerList }

    fun getSortedInternalServers() : List<String> = runBlocking {
        automaticStore.data.first().internalServerList
    }

    fun getSortedExternalServers() : List<String> = runBlocking {
        automaticStore.data.first().externalServerList
    }

    fun getSortedDownloadServers() : List<String> = runBlocking {
        automaticStore.data.first().downloadServerList
    }

    fun setSortedInternalServers(servers : List<String>) {
        scope.launch {
            automaticStore.updateData { automaticServerPreference: AutomaticServerPreference ->
                automaticServerPreference.copy(
                    internalServerList = servers
                )
            }
        }
    }

    fun setSortedExternalServers(servers : List<String>) {
        scope.launch {
            automaticStore.updateData { automaticServerPreference: AutomaticServerPreference ->
                automaticServerPreference.copy(
                    externalServerList = servers
                )
            }
        }
    }

    fun setSortedDownloadServers(servers : List<String>) {
        scope.launch {
            automaticStore.updateData { automaticServerPreference: AutomaticServerPreference ->
                automaticServerPreference.copy(
                    downloadServerList = servers
                )
            }
        }
    }

    // SERVERS DATA

    const val PREFERENCE_SERVER_SCRIPT = "PREFERENCE_SERVER_SCRIPT"

    const val FEMBED = "Fembed"
    const val DOOD_STREAM = "DoodStream"
    const val PUJ = "Puj"
    const val VIDEOBIN = "Videobin"
    const val MP4UPLOAD = "Mp4upload"
    const val OKRU = "Ok.ru"
    const val STREAMSB = "StreamSB"
    const val UQLOAD = "Uqload"
    const val STREAMTAPE = "Streamtape"
    const val SOLIDFILES = "Solidfiles"
    const val SENDVID = "Senvid"
    const val BAYFILES = "Bayfiles"
    const val ZIPPYSHARE = "Zippyshare"
    const val MEGA = "Mega"
    const val ONEFICHIER = "1Fichier"
    const val GOODSTREAM = "GoodStream"
    const val FIRELOAD = "Fireload"
    const val VOE = "Voe"
    const val UPTOBOX = "Uptobox"
    const val ANONFILE = "Anonfile"
    const val YOUR_UPLOAD = "YourUpload"
    const val MEGA_UP = "MegaUp"
    const val GOOGLE_DRIVE = "Google Drive"
    const val MEDIAFIRE = "Mediafire"
    const val VIDLOX = "Vidlox"
    const val STREAMWISH = "StreamWish"
    const val FILEMOON = "FileMoon"
    const val MIXDROP = "MixDrop"
    const val PIXELDRAIN = "PixelDrain"
    const val FILELIONS = "Filelions"
    const val VIDGUARD = "VidGuard"


    const val URL_FEMBED = "fembed.com"
    const val URL_DOOD_STREAM = "doodstream.com"
    const val URL_PUJ= "repro.monoschinos2.com/aqua"
    const val URL_VIDEOBIN = "videobin.co"
    const val URL_MP4UPLOAD = "mp4upload.com"
    const val URL_OKRU = "ok.ru"
    const val URL_STREAMTAPE = "streamtape.com"
    const val URL_SOLIDFILES = "solidfiles.com"
    const val URL_SENDVID = "sendvid.com"
    const val URL_BAYFILES = "bayfiles.com"
    const val URL_ZIPPYSHARE = "zippyshare.com"
    const val URL_MEGA = "mega.nz"
    const val URL_ONEFICHIER = "1fichier.com"
    const val URL_GOODSTREAM = "goodstream.uno"
    const val URL_FIRELOAD = "fireload.com"
    const val URL_VOE = "voe.sx"
    const val URL_UPTOBOX = "uptobox.com"
    const val URL_ANONFILE = "anonfile.com"
    const val URL_YOUR_UPLOAD = "yourupload.com"
    const val URL_MEGA_UP = "megaup.net"
    const val URL_GOOGLE_DRIVE = "drive.google.com"
    const val URL_MEDIAFIRE = "mediafire.com"
    const val URL_VIDLOX = "vidlox.me"
    const val URL_FILEMOON = "filemoon.sx"
    const val URL_MIXDROP = "mixdrop.co"
    const val URL_PIXELDRAIN = "pixeldrain.com"

    val URL_STREAMSB_DOMAINS = listOf(
        "sblanh.com", "lvturbo.com" ,"sbface.com","sbbrisk.com",
        "sbchill.com","sblongvu.com", "sbanh.com", "playersb.com",
        "embedsb.com","sbspeed.com","tubesb.com","sbrity.com"
    )

    val URL_STREAMWISH_DOMAINS = listOf(
        "streamwish.to", "embedwish.com","sfastwish.com","wishfast.top","swhoi.com"
    )

    val URL_FILELIONS_DOMAINS = listOf(
        "filelions.com","fviplions.com" , "filelions.online"
    )

    val URL_VIDGUARD_DOMAINS = listOf(
        "vgfplay.com", "vidguard.to", "vid-guard.com",
        "vembed.net", "vgembed.com", "v6embed.xyz",
        "moflix-stream.day"
    )

    val URL_UQLOAD_DOMAINS = listOf(
        "uqload"
    )

    val serverIntegrationList : List<ServerIntegration> = listOf(
        ServerIntegration(
          serverClass = GoodStream::class.java,
            pattern = "https://goodstream\\.uno/video/embed/([a-zA-Z0-9]+)"
        ),
        ServerIntegration(
          serverClass = Voe::class.java,
            pattern = "https?://(?:voe|markstyleall|shannonpersonalcost|cindyeyefinal)\\.(?:com|sx|net|to|io|co|xyz)/(?:e|d)/\\w+"
        ),
        ServerIntegration(
            serverClass = DoodStream::class.java,
            pattern = "https://doodstream\\.com/e/([a-zA-Z0-9]+)"
        ),
        ServerIntegration(
            serverClass = FileMoon::class.java,
            pattern = "https://filemoon\\.sx/e/([a-zA-Z0-9]+)"
        ),
        ServerIntegration(
            serverClass = Mega::class.java,
            pattern = "https://mega\\.nz/([a-zA-Z0-9/_-]+)"
        ),
        ServerIntegration(
            serverClass = MegaUp::class.java,
            pattern = "https://megaup\\.net/([a-zA-Z0-9]+)"
        ),
        ServerIntegration(
            serverClass = MixDrop::class.java,
            pattern = "https://mixdrop\\.(co|to)/f/([a-zA-Z0-9]+)"
        ),
        ServerIntegration(
            serverClass = Mp4Upload::class.java,
            pattern = "https://www\\.mp4upload\\.com/embed-[a-zA-Z0-9]+\\.html"
        ),
        ServerIntegration(
            serverClass = Puj::class.java,
            pattern = "https://player\\.odycdn\\.com/api/v4/streams/free/[a-zA-Z0-9%/-]+"
        ),
        ServerIntegration(
            serverClass = Uptobox::class.java,
            pattern = "https://uptobox\\.com/[a-zA-Z0-9]+"
        ),
        ServerIntegration(
            serverClass = Uqload::class.java,
            pattern = "https://uqload\\.[a-z]{2,3}/embed-([a-zA-Z0-9]+)\\.html"
        ),
        ServerIntegration(
            serverClass = VidGuard::class.java,
            pattern = "https://(?:vidguard\\.(?:net|io)|vgapi\\.xyz|listeamed\\.net)/[a-zA-Z0-9_-]+"
        ),
        ServerIntegration(
            serverClass = YourUpload::class.java,
            pattern = "https://www\\.yourupload\\.com/embed/[a-zA-Z0-9]+"
        )
    )
}