package com.ead.project.dreamer.app.data.player

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.media3.cast.CastPlayer
import androidx.media3.cast.SessionAvailabilityListener
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.ima.ImaAdsLoader
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.source.ads.AdsLoader
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import androidx.media3.ui.PlayerView
import androidx.mediarouter.app.MediaRouteButton
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
import com.google.android.gms.cast.framework.CastContext
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.runBlocking


@UnstableApi class PlayerManager(
    private val context: Context,
    private val castContext: CastContext,
    private val viewModel: PlayerViewModel,
    private var chapter: Chapter,
    private var videoList : List<VideoModel>,
    val playerView: PlayerView,
    val castManager : CastManager,
    private var isNewIntent : Boolean = false,
    preferenceUseCase: PreferenceUseCase
) : Player.Listener, SessionAvailabilityListener , MediaSession.Callback {

    companion object {
        private const val REWIND_SESSION = "REWIND_SESSION"
        private const val FORWARD_SESSION = "FORWARD_SESSION"
    }

    private val playerActivity = context as PlayerActivity

    private val adPreferences: AdPreferences = preferenceUseCase.adPreferences
    private val playerPreferences : PlayerPreferences = preferenceUseCase.playerPreferences
    private val filesPreferences : FilesPreferences = preferenceUseCase.filesPreferences

    var currentPlayer : Player?= null
    var exoPlayer : ExoPlayer?= null
    var castPlayer : CastPlayer?= null

    private var adsLoader: ImaAdsLoader? = null

    var isInPipMode : Boolean = false
    var isPIPModeEnabled : Boolean = playerPreferences.isInPictureInPictureMode()

    private var mediaRouteButton: MediaRouteButton? = null
    private var mediaSession : MediaSession? = null
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
                    DefaultMediaSourceFactory(dataSourceFactory).setLocalAdInsertionComponents(adsProvider,playerView)
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
                    playerView.player = it
                    it.setMediaItems(mediaItems().asReversed())
                    it.playWhenReady = playWhenReady
                    it.seekTo(currentWindow, playbackPosition)
                    it.addListener(this)
                    it.prepare()
                }

            adsLoader?.setPlayer(currentPlayer)

            /*mediaSession = MediaSession.Builder(context,player?:return).let {
                it.setId()
                it.setCallback(this)
                it.build()
            }*/
        }

        if (currentPlayer == castPlayer) {

            playerPreferences.setCastingChapter(chapter)

            setCastingPlayerFlags()

            val reference : String = if (isLocalVideo()) {
                chapter.getLocalReference()
            }
            else {
                videoList.last().directLink
            }

            val metadata = MediaMetadata.Builder().apply {
                setTitle(chapter.title)
                setSubtitle(context.getString(R.string.chapter_number,chapter.number))
                setArtworkUri(Uri.parse(getMediaCover(chapter)))
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

    private fun getMediaCover(chapter: Chapter) : String = runBlocking {
        viewModel.getProfile(chapter.idProfile)?.profilePhoto ?: "null"
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
        playerView.useController = true

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
            release()
        }

        exoPlayer?.release()
        exoPlayer = null

        playerView.player = null

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

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {
        val connectionResult = super.onConnect(session, controller)
        val sessionCommands =
            connectionResult.availableSessionCommands
                .buildUpon()
                // Add custom commands
                .add(SessionCommand(REWIND_SESSION, Bundle()))
                .add(SessionCommand(FORWARD_SESSION, Bundle()))
                .build()
        return MediaSession.ConnectionResult.accept(
            sessionCommands, connectionResult.availablePlayerCommands)
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {

        when(customCommand.customAction) {
            REWIND_SESSION -> {
                session.player.seekBack()
                return Futures.immediateFuture(
                    SessionResult(SessionResult.RESULT_SUCCESS)
                )
            }
            FORWARD_SESSION -> {
                session.player.seekForward()
                return Futures.immediateFuture(
                    SessionResult(SessionResult.RESULT_SUCCESS)
                )
            }
        }
        return super.onCustomCommand(session, controller, customCommand, args)
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

        if (LocalServer.isStarted()) {

            LocalServer.add(chapter)
            playOnPlayer(castPlayer)
        }

        else {

            LocalServer.start()
            LocalServer.add(chapter)
            Thread.onCasting {
                playOnPlayer(castPlayer)
            }

        }

    }
}