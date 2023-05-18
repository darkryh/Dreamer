package com.ead.project.dreamer.app.data.player

import android.content.Context
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import android.view.WindowManager
import android.widget.TextView
import androidx.mediarouter.app.MediaRouteButton
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.ads.AdPreferences
import com.ead.project.dreamer.app.data.files.FilesPreferences
import com.ead.project.dreamer.app.data.player.casting.CastManager
import com.ead.project.dreamer.app.data.util.TimeUtil
import com.ead.project.dreamer.app.data.util.system.hideSystemUI
import com.ead.project.dreamer.app.data.util.system.longToSeconds
import com.ead.project.dreamer.app.data.util.system.secondsToLong
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.data.system.extensions.toast
import com.ead.project.dreamer.data.utils.LocalServer
import com.ead.project.dreamer.data.utils.Thread
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.ead.project.dreamer.presentation.player.PlayerActivity
import com.ead.project.dreamer.presentation.player.PlayerViewModel
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
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
import com.google.android.gms.cast.framework.CastContext


class PlayerManager(
    private val context: Context,
    private val castContext: CastContext,
    private val viewModel: PlayerViewModel,
    private var chapter: Chapter,
    private var videoList : List<VideoModel>,
    val styledPlayerView: StyledPlayerView,
    val castManager : CastManager,
    var isNewIntent : Boolean = false,
    preferenceUseCase: PreferenceUseCase
) : Player.Listener, SessionAvailabilityListener {

    private val playerActivity = context as PlayerActivity

    private val adPreferences: AdPreferences = preferenceUseCase.adPreferences
    private val playerPreferences : PlayerPreferences = preferenceUseCase.playerPreferences
    private val filesPreferences : FilesPreferences = preferenceUseCase.filesPreferences

    var currentPlayer : Player?= null
    var exoPlayer : ExoPlayer?= null
    var castPlayer : CastPlayer?= null

    private var adsLoader: ImaAdsLoader? = null
    private val isServerStarted get() = LocalServer.isStarted()

    var isInPipMode : Boolean = false
    var isPIPModeEnabled : Boolean = playerPreferences.isInPictureInPictureMode()

    private var mediaRouteButton: MediaRouteButton? = null
    private var mediaSession : MediaSessionCompat? = null
    private var playWhenReady = true
    private var currentWindow = 0
    var playbackPosition = chapter.currentProgress.secondsToLong()

    private var isVideoEnded = false
    private val incrementMs : Long = 30000L
    private val timeout = 10000

    private val adTagUri = Uri.parse(context.getString(R.string.ad_tag_url))
    val trackSelector: DefaultTrackSelector by lazy {
        DefaultTrackSelector(context).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
    }

    fun initPlayer() {
        //playerPreferences.setCastingChapter(chapter)
        adsLoader = ImaAdsLoader.Builder(context).build()
        exoPlayer = ExoPlayer.Builder(context).build()

        if (castPlayer == null || isNewIntent) {
            castPlayer = CastPlayer(castContext)
            castPlayer?.setSessionAvailabilityListener(this)
        }

        if (isCastingAvailable()) {
            playerOnPlayerInCastMode()
        }
        else {
            playOnPlayer(exoPlayer)
        }
    }

    private fun initializeChapter() = currentPlayer?.let { player ->
        chapter = chapter.copy(totalProgress = player.contentDuration.longToSeconds())
    }

    fun setMediaRouteButton(mediaRouteButton: MediaRouteButton){
        this.mediaRouteButton = mediaRouteButton
        this.mediaRouteButton?.setVisibility(castManager.isAvailable)
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

            setExoplayerFlags()

            val mediaSourceFactory: MediaSource.Factory =
                if (adPreferences.shouldShowAdPlayer()) {
                    val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(context)
                    DefaultMediaSourceFactory(dataSourceFactory).setLocalAdInsertionComponents(adsProvider,styledPlayerView)
                }
                else  {
                    val dataSourceFactory : DataSource.Factory  =
                        if (isStreamingVideo()) {
                            DefaultHttpDataSource.Factory()
                                .setConnectTimeoutMs(timeout)
                                .setAllowCrossProtocolRedirects(true)
                                .setKeepPostFor302Redirects(true)
                                .setUserAgent(Util.getUserAgent(context, context.applicationInfo.name))
                        }
                        else {
                            DefaultDataSource.Factory(context)
                        }

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
                .setSeekForwardIncrementMs(incrementMs)
                .setSeekBackIncrementMs(incrementMs)
                .setAudioAttributes(audioAttributes,true)
                .build()
                .also {
                    styledPlayerView.player = it
                    it.setMediaItems(mediaItems().asReversed())
                    it.playWhenReady = playWhenReady
                    it.seekTo(currentWindow, playbackPosition)
                    it.addListener(this)
                    it.prepare()
                }

            adsLoader?.setPlayer(currentPlayer)

            mediaSession = MediaSessionCompat(context, playerActivity.packageName)
            val mediaSessionConnector = MediaSessionConnector(mediaSession!!)
            mediaSessionConnector.setPlayer(currentPlayer)
            mediaSession?.isActive = true

        }

        if (currentPlayer == castPlayer) {

            setCastingPlayerFlags()

            val reference : String = if (isLocalVideo()) {
                chapter.getLocalReference()
            }
            else {
                videoList.last().directLink
            }

            val metadata = MediaMetadata.Builder().apply {
                setTitle(chapter.title)
                setSubtitle(context.getString(R.string.chapter_number,chapter.number.toString()))
                setArtworkUri(Uri.parse(chapter.cover))
                setWriter(context.applicationInfo.name)
            }.build()

            val mediaItem = MediaItem.Builder().apply {
                setAdsConfiguration(MediaItem.AdsConfiguration.Builder(adTagUri).build())
                setUri(reference)
                setMimeType(MimeTypes.VIDEO_UNKNOWN)
                setMediaMetadata(metadata)
            } .build()

            currentPlayer?.also {
                it.addListener(this)
                it.setMediaItem(mediaItem, playbackPosition)
                it.prepare()
            }
        }
    }

    private fun setExoplayerFlags() {
        playerActivity.window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun setCastingPlayerFlags() {
        playerActivity.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun mediaItems() : List<MediaItem> =
        if (isStreamingVideo()) {

            videoList.map {
                MediaItem.Builder().apply { setUri(it.directLink)
                    setAdsConfiguration(MediaItem.AdsConfiguration.Builder(adTagUri).build()) }.build()
            }

        }
    else {

        listOf(MediaItem.Builder().setUri(Uri.fromFile(filesPreferences.getChapterFile(chapter)))
            .setAdsConfiguration(MediaItem.AdsConfiguration.Builder(adTagUri).build()).build())

    }

    private fun Player.rememberState() {

        this@PlayerManager.playWhenReady = playWhenReady
        this@PlayerManager.currentWindow = currentMediaItemIndex
        if (!isNewIntent)  {
            this@PlayerManager.playbackPosition = currentPosition
        }
        else {
            this@PlayerManager.playbackPosition = chapter.currentProgress.secondsToLong()
        }
        isNewIntent = false

    }

    fun onNewIntent() {

        isNewIntent = true
        chapter = playerActivity.chapter
        videoList = playerActivity.playList
        playbackPosition = chapter.currentProgress.secondsToLong()

    }

    fun onStart() {

        if (Util.SDK_INT >= 24 && videoList.isNotEmpty()) {
            initPlayer()
        }

    }

    fun onResume() {

        playerActivity.hideSystemUI()
        if ((Util.SDK_INT < 24 || exoPlayer == null && !isInPipMode && videoList.isNotEmpty())) {
            initPlayer()
        }
        styledPlayerView.useController = true

    }

    fun onPause() {

        currentPlayer?.let {
            playbackPosition = it.currentPosition
        }

        playerPreferences.getChapter()?.let {
            if (chapter.id == it.id) {
                chapter = chapter.copy(
                    state = it.state
                )
            }
        }

        if (adPreferences.shouldShowAdPlayer()) {
            adPreferences.resetViews()
        }

    }

    fun onStop() {

        updateMedia()
        release()

    }

    private fun release() {
        adsLoader?.release()

        if (isNotCasting()) {

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

        styledPlayerView.player = null

        castPlayer?.setSessionAvailabilityListener(null)
        castPlayer = null
    }

    private fun isLocalVideo() : Boolean = chapter.isDownloaded()
    private fun isStreamingVideo() : Boolean = !chapter.isDownloaded()
    private fun isMediaEnded() : Boolean = isVideoEnded || isCastingAvailable()
    private fun isCastingAvailable() : Boolean = castPlayer?.isCastSessionAvailable?:false
    fun isNotCasting() : Boolean = currentPlayer !is CastPlayer

    fun showCastingMessage(textView: TextView) {
        castManager.showCastingTextView(textView)
    }

    fun hideCastingMessage(textView: TextView) {
        castManager.hideCastingTextView(textView)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> {
                initializeChapter()
            }
            Player.STATE_IDLE -> {

            }
            Player.STATE_BUFFERING -> {

            }
            Player.STATE_ENDED -> {

            }
            else -> {
                context.toast("unknown state")
            }
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        context.toast(error.errorCodeName)
    }
    fun updateMedia() {

        chapter = chapter.copy(
            currentProgress = playbackPosition.longToSeconds(),
            lastDateSeen = TimeUtil.getNow()
        )

        if (chapter.isMediaWatched) {
            chapter = chapter.copy(isContentConsumed = true)
            adPreferences.addViewedTime()
            isVideoEnded = false
        }
        else if (isMediaEnded()) {
            adPreferences.addViewedTime()
        }

        if (chapter.isMediaInitialized) {
            viewModel.updateChapter(chapter)
        }

    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        when(reason) {
            Player.MEDIA_ITEM_TRANSITION_REASON_AUTO -> {
                isVideoEnded = true
            }
            Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED -> {}
            Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT -> {

            }
            Player.MEDIA_ITEM_TRANSITION_REASON_SEEK -> {
                isVideoEnded = true
            }
        }
    }

    override fun onCastSessionAvailable() {
        playerOnPlayerInCastMode()
    }

    override fun onCastSessionUnavailable() {
        playOnPlayer(exoPlayer)
    }

    private fun playerOnPlayerInCastMode() {

        if (isLocalVideo()) {
            startInCastLocalMode()
        }
        else {
            playOnPlayer(castPlayer)
        }

    }

    private fun startInCastLocalMode() {

        if (isServerStarted) {

            LocalServer.add(chapter)
            playOnPlayer(castPlayer)
        }

        else {

            LocalServer.start()
            LocalServer.add(chapter)
            startCasting {
                playOnPlayer(castPlayer)
            }

        }

    }

    private fun startCasting(task: () -> Unit) = Thread.runInMs(task,1000)

}