package com.ead.project.dreamer.data.utils.media

import android.app.Activity

import android.content.Context
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import android.view.View
import androidx.mediarouter.app.MediaRouteButton
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.database.model.VideoModel
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.ui.player.PlayerActivity
import com.ead.project.dreamer.ui.player.PlayerViewModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.cast.CastPlayer
import com.google.android.exoplayer2.ext.cast.SessionAvailabilityListener
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.StyledPlayerView
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

    var isInPipMode : Boolean = false
    var isPIPModeEnabled : Boolean = DataStore.
    readBoolean(Constants.PREFERENCE_PIP_MODE_PLAYER)

    private var mediaSession : MediaSessionCompat? = null

    private val timeOut = 10000
    private var playWhenReady = true
    private var currentWindow = 0
    private var isVideoEnded = false
    var playbackPosition = chapter.currentSeenToLong()

    private val playerActivity = activity as PlayerActivity

    lateinit var mediaRouteButton: MediaRouteButton

    fun initPlayer() {
        Chapter.setCasting(chapter)
        if (castManager.isAvailable())
            mediaRouteButton.visibility = View.VISIBLE

        exoPlayer = ExoPlayer.Builder(context).build()
        if (castPlayer == null || isNewIntent) {
            castPlayer = CastPlayer(castManager.getContext())
            castPlayer?.setSessionAvailabilityListener(this)
        }
        playOnPlayer(if (castPlayer?.isCastSessionAvailable == true) castPlayer else exoPlayer)
    }

    private fun initMediaSettings() = try {
        chapter.totalToSeen = Tools.longToSeconds(currentPlayer!!.contentDuration) }
    catch (e : Exception) { e.printStackTrace() }

    private fun playOnPlayer(player: Player?) {
        if (currentPlayer == player) return
        currentPlayer?.let {
            if (it.playbackState != Player.STATE_ENDED) {
                it.rememberState()
            }
            it.stop()
        }
        playerActivity.setMetaData()
        playerActivity.preparingLayoutByMode()
        currentPlayer = player

        if (currentPlayer == exoPlayer) {
            trackSelector = DefaultTrackSelector(context).apply {
                setParameters(buildUponParameters().setMaxVideoSizeSd())
            }
            val dataSourceFactory = DefaultHttpDataSource.Factory()
                .setConnectTimeoutMs(timeOut)
                .setAllowCrossProtocolRedirects(true)
                .setKeepPostFor302Redirects(true)
                .setUserAgent(Util.getUserAgent(context, "Dreamer"))

            val mediaSource  = when (Util.inferContentType(videoList.last().directLink)) {
                C.TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory)
                C.TYPE_SS -> SsMediaSource.Factory(dataSourceFactory)
                C.TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory)
                C.TYPE_OTHER -> ProgressiveMediaSource.Factory(dataSourceFactory)
                else -> throw Exception("error")
            }

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(C.CONTENT_TYPE_MOVIE).build()

            currentPlayer = ExoPlayer.Builder(context)
                .setTrackSelector(trackSelector)
                .setMediaSourceFactory(mediaSource)
                .setSeekForwardIncrementMs(30000)
                .setSeekBackIncrementMs(10000)
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

            mediaSession = MediaSessionCompat(context, activity.packageName)
            val mediaSessionConnector = MediaSessionConnector(mediaSession!!)
            mediaSessionConnector.setPlayer(currentPlayer)
            mediaSession?.isActive = true
        }

        if (currentPlayer == castPlayer) {
            val metadata = MediaMetadata.Builder()
                .setTitle(chapter.title)
                .setSubtitle("Capítulo ${chapter.chapterNumber}")
                .setArtworkUri(Uri.parse(chapter.chapterCover))
                .setWriter("Dreamer")
                .build()

            val mediaItem = MediaItem.Builder()
                .setUri(videoList.last().directLink)
                .setMimeType(MimeTypes.VIDEO_UNKNOWN)
                .setMediaMetadata(metadata).build()

            currentPlayer?.addListener(this)
            currentPlayer?.setMediaItem(mediaItem, playbackPosition)
        }
    }

    private fun mediaItems() : List<MediaItem> =
        videoList.map { MediaItem.fromUri(it.directLink) }

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
        Tools.hideSystemUI(activity,playerView)
        if ((Util.SDK_INT < 24 || exoPlayer == null && !isInPipMode && videoList.isNotEmpty())) {
            initPlayer()
        }
        playerView.useController = true
    }

    fun onPause() {
        if (currentPlayer!= null) {
            playbackPosition = currentPlayer!!.currentPosition
        }
    }

    fun onStop() {
        updateMedia()
        release()
    }

    fun release() {
        if (!isPlayerCastMode()) {
            currentPlayer?.run {
                this@PlayerManager.playWhenReady = playWhenReady
                this@PlayerManager.playbackPosition = currentPosition
                this@PlayerManager.currentWindow = currentMediaItemIndex
                release()
            }
            currentPlayer = null
        }
        mediaSession?.isActive = false
        mediaSession?.release()
        exoPlayer?.release()
        exoPlayer = null
        playerView.player = null
        castPlayer?.setSessionAvailabilityListener(null)
        castPlayer = null
    }

    fun isPlayerCastMode() = currentPlayer is CastPlayer

    fun isCasting() = isPlayerCastMode()
            && (currentPlayer?.isPlaying == true || currentPlayer?.isLoading == true)

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> {
                initMediaSettings()
            }
            Player.STATE_IDLE -> {

            }
            Player.STATE_BUFFERING -> {

            }
            Player.STATE_ENDED -> {

            }
            else -> {
                DreamerApp.showLongToast("error")
            }
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