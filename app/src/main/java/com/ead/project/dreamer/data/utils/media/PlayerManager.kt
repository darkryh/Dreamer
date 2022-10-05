package com.ead.project.dreamer.data.utils.media


import android.app.Activity
import android.content.Context
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.mediarouter.app.MediaRouteButton
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.commons.Tools.Companion.hideSystemUI
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.database.model.VideoModel
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.ui.player.PlayerActivity
import com.ead.project.dreamer.ui.player.PlayerViewModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.cast.CastPlayer
import com.google.android.exoplayer2.ext.cast.SessionAvailabilityListener
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.ads.AdsLoader
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import java.util.*
import kotlin.math.roundToInt


class PlayerManager(
    val activity : Activity,
    val viewModel: PlayerViewModel,
    var playerView: StyledPlayerView,
    var chapter: Chapter,
    var videoList : List<VideoModel>,
    val castManager : CastManager,
    var isNewIntent : Boolean = false) : Player.Listener, SessionAvailabilityListener {

    lateinit var trackSelector: DefaultTrackSelector
    private val context : Context = activity

    var currentPlayer : Player?= null
    var exoPlayer : ExoPlayer?= null
    var castPlayer : CastPlayer?= null
    private var adsLoader: ImaAdsLoader? = null

    var isInPipMode : Boolean = false
    var isPIPModeEnabled : Boolean = DataStore.
    readBoolean(Constants.PREFERENCE_PIP_MODE_PLAYER)

    private var mediaSession : MediaSessionCompat? = null

    private var playWhenReady = true
    private var currentWindow = 0
    private var isVideoEnded = false
    private var timeout = 10000
    var playbackPosition = chapter.currentSeenToLong()

    private val playerActivity = activity as PlayerActivity

    lateinit var mediaRouteButton: MediaRouteButton

    fun initPlayer() {
        Chapter.setCasting(chapter)
        if (castManager.isAvailable())
            mediaRouteButton.visibility = View.VISIBLE

        adsLoader = ImaAdsLoader.Builder(context).build()
        exoPlayer = ExoPlayer.Builder(context).build()
        if (castPlayer == null || isNewIntent) {
            castPlayer = CastPlayer(castManager.getContext())
            castPlayer?.setSessionAvailabilityListener(this)
        }
        playOnPlayer(if (castPlayer?.isCastSessionAvailable == true) castPlayer else exoPlayer)
    }

    private fun initMediaSettings() =  currentPlayer?.let {
        chapter.totalToSeen = Tools.longToSeconds(it.contentDuration)
    }


    private fun playOnPlayer(player: Player?) {
        if (currentPlayer == player || videoList.isEmpty()) return
        currentPlayer?.let {
            if (it.playbackState != Player.STATE_ENDED) {
                it.rememberState()
            }
            it.stop()
        }

        playerActivity.setMetaData()
        playerActivity.preparingLayoutByMode()
        currentPlayer = player
        val adsProvider = AdsLoader.Provider { adsLoader }

        if (currentPlayer == exoPlayer) {
            setCurrentExoplayerFlags()

            trackSelector = DefaultTrackSelector(context).apply {
                setParameters(buildUponParameters().setMaxVideoSizeSd())
            }

            val mediaSourceFactory: MediaSource.Factory =
                if (Constants.isAdTime()) {
                    val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(context)
                    DefaultMediaSourceFactory(dataSourceFactory).setLocalAdInsertionComponents(adsProvider,playerView)
                }
                else  {
                    val dataSourceFactory = DefaultHttpDataSource.Factory()
                        .setConnectTimeoutMs(timeout)
                        .setAllowCrossProtocolRedirects(true)
                        .setKeepPostFor302Redirects(true)
                        .setUserAgent(Util.getUserAgent(context, "Dreamer"))

                    val mediaSource  = when (Util.inferContentType(Uri.parse(videoList.last().directLink))) {
                        C.CONTENT_TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory)
                        C.CONTENT_TYPE_SS -> SsMediaSource.Factory(dataSourceFactory)
                        C.CONTENT_TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory)
                        C.CONTENT_TYPE_OTHER -> ProgressiveMediaSource.Factory(dataSourceFactory)
                        else -> throw Exception("error")
                    }
                    mediaSource
                }

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE).build()

            currentPlayer = ExoPlayer.Builder(context)
                .setTrackSelector(trackSelector)
                .setMediaSourceFactory(mediaSourceFactory)
                .setSeekForwardIncrementMs(30000)
                .setSeekBackIncrementMs(30000)
                .setAudioAttributes(audioAttributes,true)
                .build()
                .also {
                    playerView.player = it
                    it.setMediaItems(mediaItems().asReversed())
                    it.playWhenReady = playWhenReady
                    it.seekTo(currentWindow, playbackPosition)
                    it.addListener(this)
                    it.prepare()
                }
            adsLoader?.setPlayer(currentPlayer)

            mediaSession = MediaSessionCompat(context, activity.packageName)
            val mediaSessionConnector = MediaSessionConnector(mediaSession!!)
            mediaSessionConnector.setPlayer(currentPlayer)
            mediaSession?.isActive = true
        }

        if (currentPlayer == castPlayer) {
            setCurrentCastingPlayerFlags()
            val metadata = MediaMetadata.Builder()
                .setTitle(chapter.title)
                .setSubtitle("Capítulo ${chapter.chapterNumber}")
                .setArtworkUri(Uri.parse(chapter.chapterCover))
                .setWriter("Dreamer")
                .build()

            val mediaItem = MediaItem.Builder()
                .setAdsConfiguration(MediaItem.AdsConfiguration.Builder(adTagUri).build())
                .setUri(videoList.last().directLink)
                .setMimeType(MimeTypes.VIDEO_UNKNOWN)
                .setMediaMetadata(metadata).build()

            currentPlayer?.addListener(this)
            currentPlayer?.setMediaItem(mediaItem, playbackPosition)
        }
    }

    private fun setCurrentExoplayerFlags() {
        activity.window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun setCurrentCastingPlayerFlags() {
        activity.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        }
    }

    private val adTagUri = Uri.parse(context.getString(R.string.ad_tag_url))
    private fun mediaItems() : List<MediaItem> =
        videoList.map {
            MediaItem.Builder()
                .setUri(it.directLink)
                .setAdsConfiguration(MediaItem.AdsConfiguration.Builder(adTagUri).build())
                .build()
        }

    private fun Player.rememberState() {
        this@PlayerManager.playWhenReady = playWhenReady
        this@PlayerManager.currentWindow = currentMediaItemIndex
        if (!isNewIntent)  this@PlayerManager.playbackPosition = currentPosition
        else this@PlayerManager.playbackPosition = chapter.currentSeenToLong()
        isNewIntent = false
    }

    fun preNewIntent() {
        updateMedia()
    }

    fun onNewIntent() {
        isNewIntent = true
        chapter = playerActivity.chapter
        videoList = playerActivity.playList
        playbackPosition = Tools.secondsToLong(chapter.currentSeen)
    }

    fun onStart() {
        if (Util.SDK_INT >= 24 && videoList.isNotEmpty()) {
            initPlayer()
        }
    }

    fun onResume() {
        activity.hideSystemUI()
        if ((Util.SDK_INT < 24 || exoPlayer == null && !isInPipMode && videoList.isNotEmpty())) {
            initPlayer()
        }
        playerView.useController = true
    }

    fun onPause() {
        currentPlayer?.let {
            playbackPosition = it.currentPosition
        }
        if (Constants.isAdTime()) Constants.resetCountedAds()
    }

    fun onStop() {
        updateMedia()
        release()
    }

    fun release() {
        adsLoader?.release()
        if (!isPlayerCastMode()) {
            currentPlayer?.run {
                this@PlayerManager.playWhenReady = playWhenReady
                this@PlayerManager.playbackPosition = currentPosition
                this@PlayerManager.currentWindow = currentMediaItemIndex
                release()
            }
            currentPlayer = null
        }
        mediaSession?.apply {
            isActive = false
            release()
        }
        exoPlayer?.release()
        exoPlayer = null
        playerView.player = null
        castPlayer?.setSessionAvailabilityListener(null)
        castPlayer = null
    }

    fun isPlayerCastMode() = currentPlayer is CastPlayer

    fun isCasting() = isPlayerCastMode()

    fun isNotCasting() = !isCasting()

    fun showCastingMessage(textView: TextView) = castManager.showMessageInTextView(textView)

    fun hideCastingMessage(textView: TextView) = castManager.hideMessageInTextView(textView)

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> { initMediaSettings() }
            Player.STATE_IDLE -> {}
            Player.STATE_BUFFERING -> {}
            Player.STATE_ENDED -> {}
            else -> { DreamerApp.showLongToast("error") }
        }
    }

    private fun updateMedia() {
        chapter.currentSeen = Tools.longToSeconds(playbackPosition)
        chapter.lastSeen = Calendar.getInstance().time
        if (mediaIsOnFinalState()) {
            chapter.alreadySeen = true
            Constants.quantityAdPlus()
            isVideoEnded = false
        }
        else if (mediaIsEnded()) Constants.quantityAdPlus()

        viewModel.updateChapter(chapter)
    }

    private fun mediaIsOnFinalState() : Boolean =
        chapter.currentSeen >= (chapter.totalToSeen * 0.92).roundToInt()
                && chapter.totalToSeen > 0

    private fun mediaIsEnded() : Boolean = isVideoEnded || castPlayer?.isCastSessionAvailable == true

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        isVideoEnded = true
    }

    override fun onCastSessionAvailable() {
        playOnPlayer(castPlayer)
    }

    override fun onCastSessionUnavailable() {
        playOnPlayer(exoPlayer)
    }
}