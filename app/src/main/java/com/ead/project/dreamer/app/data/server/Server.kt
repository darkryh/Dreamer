package com.ead.project.dreamer.app.data.server

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.ead.project.dreamer.app.App
import com.ead.project.dreamer.app.model.AutomaticServerPreference
import com.ead.project.dreamer.app.model.ServerPreference
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
        "streamwish.to", "embedwish.com","sfastwish.com","wishfast.top"
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
        "uqload.com","uqload.io"
    )
}