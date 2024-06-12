package com.ead.project.dreamer.presentation.player

import android.app.PictureInPictureParams
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.util.Rational
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.mediarouter.app.MediaRouteButton
import coil.load
import coil.transform.CircleCropTransformation
import com.ead.commons.lib.lifecycle.activity.onBack
import com.ead.commons.lib.lifecycle.activity.onBackHandle
import com.ead.commons.lib.lifecycle.parcelable
import com.ead.commons.lib.lifecycle.parcelableArrayList
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.player.PlayerManager
import com.ead.project.dreamer.app.data.util.system.hideSystemUI
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.data.system.extensions.toast
import com.ead.project.dreamer.data.utils.DimensionUtil
import com.ead.project.dreamer.data.utils.Thread
import com.ead.project.dreamer.data.utils.ui.PlayerOnScaleGestureListener
import com.ead.project.dreamer.presentation.player.content.PlayerContentFragment
import com.ead.project.dreamer.presentation.player.content.chapterselector.ChapterSelectorFragment
import com.ead.project.dreamer.presentation.player.content.scalegesture.ScaleGestureFragment
import com.ead.project.dreamer.presentation.player.content.trackselector.TrackSelectorFragment
import com.ead.project.dreamer.presentation.player.model.NextChapterLayout
import dagger.hilt.android.AndroidEntryPoint

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@AndroidEntryPoint
class PlayerActivity : AppCompatActivity(),View.OnLayoutChangeListener   {

    private val viewModel : PlayerViewModel by viewModels()
    private val playerManager: PlayerManager by lazy {
        PlayerManager(this, viewModel.castContext, viewModel, chapter,
            playList, playerView, viewModel.castManager, false,
            viewModel.preferenceUseCase
        )
    }

    lateinit var chapter: Chapter
    private var nextChapter : Chapter?= null
    private var previousChapter : Chapter?=null
    var playList : List<VideoModel> = emptyList()

    private var orientation : Int = 0
    private val aspectRatio : Float = (16f / 9f)
    private val rationalAspectRatio = Rational(16,9)
    private val pipPermissionTimeout : Long = 30L

    private val fixedWidthMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
    private val fillMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
    private val fitMode = AspectRatioFrameLayout.RESIZE_MODE_FIT

    private val nextChapterLayout by lazy {
        NextChapterLayout(
            root = findViewById(R.id.next_chapter_root),
            logo = findViewById(R.id.next_chapter_logo),
            textNumber = findViewById(R.id.text_next_chapter_number)
        )
    }

    private val playerView : PlayerView by lazy { findViewById(R.id.styled_player_view) }
    private val controller : AspectRatioFrameLayout by lazy { findViewById(R.id.aspect_ratio_controller) }
    private val aspectFrameRatio : AspectRatioFrameLayout by lazy { findViewById(R.id.aspect_ratio) }

    private val bottomControlsPlayer : LinearLayout by lazy { findViewById(R.id.bottom_controls) }
    private val playerCover : RelativeLayout by lazy { findViewById(R.id.relative_cover_casting) }
    private val contentPlayer  : FrameLayout by lazy { findViewById(R.id.frame_content) }

    private val buttonPlayPause : ImageButton by lazy { findViewById(R.id.exo_play_pause) }
    private val buttonForward : ImageButton by lazy { findViewById(R.id.exo_ffwd) }
    private val buttonRewind : ImageButton by lazy { findViewById(R.id.exo_rew) }

    private val buttonChangeOrientationScreen : LinearLayout by lazy { findViewById(R.id.linear_fullscreen) }
    private val buttonClose : ImageView by lazy { findViewById(R.id.button_close_player) }
    private val buttonSettings : LinearLayout by lazy { findViewById(R.id.linear_settings) }
    private val buttonNextChapter : LinearLayout by lazy { findViewById(R.id.linear_next_chapter) }
    private val buttonPlaylist : LinearLayout by lazy { findViewById(R.id.linear_playlist) }
    private val buttonGesture : LinearLayout by lazy { findViewById(R.id.linear_gesture) }

    private val imagePlayerOrientationScreen : ImageView by lazy { findViewById(R.id.button_fullscreen) }
    private val imageChapterCoverInCasting : ImageView by lazy { findViewById(R.id.image_cover_casting) }
    private val imageChapterCoverInPlayer : ImageView by lazy { findViewById(R.id.image_logo_current_chapter) }

    private val containerContentPlayer : LinearLayout by lazy { findViewById(R.id.content_reference) }
    private val containerPlayerOperations : LinearLayout by lazy { findViewById(R.id.linear_operator) }

    private val textTitle : TextView by lazy { findViewById(R.id.text_chapter_title) }
    private val textChapterNumber : TextView by lazy { findViewById(R.id.text_chapter_number) }
    private val textCasting : TextView by lazy { findViewById(R.id.text_casting) }

    private var scaleGestureDetector: ScaleGestureDetector? = null
    private var playerOnScaleGestureListener : PlayerOnScaleGestureListener?=null

    private val mediaRouteButton: MediaRouteButton by lazy { findViewById(R.id.media_route_button_in_player) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        onBackHandle { onBackPressedMode()  }
        init(false)
        initSettings()
        initPlayer()
        initCastSettings()
    }

    override fun onNewIntent(intent: Intent) {
        playerManager.updateMedia()
        super.onNewIntent(intent)
        setIntent(intent)
        init(true)
        playerManager.onNewIntent()
        onStart()
    }

    private fun init(isNewIntent : Boolean){
        initVariables()
        if (!isNewIntent) {
            initPlayerGesture()
        }
        initLayouts()
        initSuggestions()
        observeNextChapter()
    }

    private fun initSettings() {
        supportActionBar?.hide()
    }

    private fun initPlayer() {
        playerManager.setMediaRouteButton(mediaRouteButton)
        playerManager.initPlayer()
        setOrientationMode()
        viewModel.updateChapter(previousChapter?:return)
    }

    private fun initVariables() {
        intent.extras?.let {
            chapter = it.parcelable(Chapter.REQUESTED)?:return@let
            playList = it.parcelableArrayList(Chapter.PLAY_VIDEO_LIST)?:return@let
            previousChapter = it.parcelable(Chapter.PREVIOUS_CASTING_MEDIA)
        }

        orientation = resources.configuration.orientation
    }

    private fun initPlayerGesture() {
        playerOnScaleGestureListener = PlayerOnScaleGestureListener(playerView,viewModel.preferenceUseCase)
        scaleGestureDetector = ScaleGestureDetector(this, playerOnScaleGestureListener?:return)
    }

    private fun initLayouts() {
        if (chapter.cover.isNotBlank()) {
            imageChapterCoverInCasting.load(chapter.cover)
            imageChapterCoverInPlayer.load(chapter.cover) {
                transformations(
                    CircleCropTransformation()
                )
            }
        }
        else {
            observeProfileFromChapter()
        }


        nextChapterLayout.root.addSelectableItemEffect()
        buttonPlaylist.addSelectableItemEffect()
        buttonSettings.addSelectableItemEffect()
        buttonNextChapter.addSelectableItemEffect()
        buttonGesture.addSelectableItemEffect()
        imagePlayerOrientationScreen.addSelectableItemEffect()
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        event?.let { scaleGestureDetector?.onTouchEvent(it) }
        return super.dispatchTouchEvent(event)
    }

    private fun devicesIsCompatibleWithPipMode() = packageManager
        .hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)

    private fun settingControllerViews() {
        buttonChangeOrientationScreen.setOnClickListener {

            requestedOrientation =
                if (isInPortraitMode()) {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
                else {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }

            Thread.runInAWhile {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }

        }

        buttonClose.setOnClickListener {

            playerManager.playerView.hideController()
            onBack()

        }

        setOnClickInPlayer()
    }

    private fun setOnClickInPlayer() {
        buttonSettings.setOnClickListener {

            if (playerManager.isNotCasting()) {

                TrackSelectorFragment.launch(
                    context = this@PlayerActivity,
                    exoPlayer = playerManager.currentPlayer as ExoPlayer,
                    videoModelList = playList.reversed(),
                    defaultTrackSelector = playerManager.trackSelector,
                    playerView = playerManager.playerView
                )

                return@setOnClickListener
            }

            toast(getString(R.string.casting_mode))
        }

        buttonGesture.setOnClickListener {

            if (playerManager.isNotCasting()) {

                ScaleGestureFragment.launch(
                    context = this@PlayerActivity,
                    playerView = playerManager.playerView
                )

                return@setOnClickListener
            }

            toast(getString(R.string.casting_mode))
        }

        buttonNextChapter.setOnClickListener {

            viewModel.handleChapter(this@PlayerActivity, nextChapter?:return@setOnClickListener)

        }

        buttonPlaylist.setOnClickListener {

            ChapterSelectorFragment.launch(
                context = this@PlayerActivity,
                isHorizontal = !isInPortraitMode(),
                playerView = playerView,
                chapter = chapter
            )

        }
    }

    override fun onStart() {
        super.onStart()
        playerManager.onStart()
        settingControllerViews()
    }

    override fun onResume() {
        super.onResume()
        playerManager.onResume()
    }

    public override fun onPause() {
        super.onPause()
        playerManager.onPause()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    public override fun onStop() {
        super.onStop()
        playerManager.onStop()
        if (devicesIsCompatibleWithPipMode()) {
            finishAndRemoveTask()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        orientation = resources.configuration.orientation
        setOrientationMode()
    }

    private fun setOrientationMode() {
        if (isInPortraitMode()) {
            portraitMode()
        }
        else {
            horizontalMode()
        }
    }

    private fun portraitMode() {

        imagePlayerOrientationScreen.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.ic_fullscreen_24
            )
        )

        playerOnScaleGestureListener?.isHorizontalMode = false

        buttonPlayPause.apply {
            layoutParams.height = DimensionUtil.portraitPlayPause
            layoutParams.width = DimensionUtil.portraitPlayPause
            requestLayout()
        }

        buttonForward.apply {
            layoutParams.height = DimensionUtil.portraitControls
            layoutParams.width = DimensionUtil.portraitControls
            requestLayout()
            setImageResource(R.drawable.ic_forward_30)
        }

        buttonRewind.apply {
            layoutParams.height = DimensionUtil.portraitControls
            layoutParams.width = DimensionUtil.portraitControls
            requestLayout()
            setImageResource(R.drawable.ic_replay_30)
        }

        controller.setAspectRatio(aspectRatio)
        aspectFrameRatio.setAspectRatio(aspectRatio)

        textTitle.visibility = View.GONE

        aspectFrameRatio.resizeMode = fixedWidthMode
        playerView.resizeMode = fixedWidthMode
        controller.resizeMode = fixedWidthMode

        imageChapterCoverInPlayer.setVisibility(false)
        buttonNextChapter.setVisibility(nextChapter != null)
        nextChapterLayout.root.setVisibility(false)
    }

    private fun horizontalMode() {

        imagePlayerOrientationScreen.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.ic_fullscreen_exit_24
            )
        )

        playerOnScaleGestureListener?.isHorizontalMode = true

        if (playerManager.isNotCasting()) {
            textTitle.visibility = View.VISIBLE
        }

        buttonPlayPause.apply {
            layoutParams.height = DimensionUtil.landscapePlayPause
            layoutParams.width = DimensionUtil.landscapePlayPause
            requestLayout()
        }

        buttonForward.apply {
            layoutParams.height = DimensionUtil.landscapeControls
            layoutParams.width = DimensionUtil.landscapeControls
            requestLayout()
            setImageResource(R.drawable.ic_forward_30_64)
        }

        buttonRewind.apply {
            layoutParams.height = DimensionUtil.landscapeControls
            layoutParams.width = DimensionUtil.landscapeControls
            requestLayout()
            setImageResource(R.drawable.ic_replay_30_64)
        }

        aspectFrameRatio.resizeMode = fillMode
        playerView.resizeMode = fitMode
        controller.resizeMode = fillMode

        imageChapterCoverInPlayer.setVisibility(true)
        buttonNextChapter.setVisibility(false)
        nextChapterLayout.root.setVisibility(nextChapter != null)
    }

    private fun observeProfileFromChapter() {
        viewModel.getProfileData(chapter.idProfile).observe(this) {
            if (it != null) {
                imageChapterCoverInCasting.load(it.profilePhoto)
                imageChapterCoverInPlayer.load(it.profilePhoto) {
                    transformations(
                        CircleCropTransformation()
                    )
                }
            }
        }
    }

    private fun observeNextChapter() {
        viewModel.geNextChapterFrom(chapter).observe(this) { nextChapter ->
            this@PlayerActivity.nextChapter = nextChapter

            val nextChapterIsAvailable = nextChapter != null
            val isInPortraitMode = isInPortraitMode()

            nextChapterLayout.root.setVisibility(nextChapterIsAvailable && !isInPortraitMode)
            buttonNextChapter.setVisibility(nextChapterIsAvailable && isInPortraitMode)

            if (nextChapter == null) return@observe

            nextChapterLayout.apply {

                root.setOnClickListener {
                    viewModel.handleChapter(this@PlayerActivity, nextChapter)
                }

                logo.setVisibility(nextChapter.cover.isNotBlank())

                logo.load(nextChapter.cover) {
                    transformations(
                        CircleCropTransformation()
                    )
                }

                textNumber.text = getString(R.string.chapter_number_normal,nextChapter.number)
            }
        }
    }

    private fun isInPortraitMode() : Boolean = orientation == Configuration.ORIENTATION_PORTRAIT

    //PIP-MODE

    private fun managePipMode() {
        if(shouldGoPipMode()) {
            enterPIPMode()
        } else {
            finish()
        }
    }

    private fun onBackPressedMode() { managePipMode() }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        managePipMode()
    }

    private fun shouldGoPipMode() = devicesIsCompatibleWithPipMode() && playerManager.isPIPModeEnabled
            && playerManager.isNotCasting()

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        handlePipModeViews(isInPictureInPictureMode)
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

    private fun handlePipModeViews(isInPictureInPictureMode : Boolean) {
        playerManager.isInPipMode = !isInPictureInPictureMode
        if (isInPictureInPictureMode) {
            containerContentPlayer.visibility = View.GONE
            // Hide the full-screen UI (controls, etc.) while in picture-in-picture mode.
        } else {
            containerContentPlayer.visibility = View.VISIBLE
            // Restore the full-screen UI.
        }
    }

    private fun enterPIPMode() {
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE) &&
            playerManager.isPIPModeEnabled) {

            playerManager.playbackPosition = playerManager.exoPlayer?.currentPosition?:return

            playerView.useController = false

            val params = PictureInPictureParams.Builder()
                .setSourceRectHint(Rect())
                .setAspectRatio(rationalAspectRatio)
            this.enterPictureInPictureMode(params.build())

            Thread.executeIn(pipPermissionTimeout) {
                checkPIPPermission()
            }
        }
    }

    private fun checkPIPPermission() {
        playerManager.isPIPModeEnabled = isInPictureInPictureMode
        if(!isInPictureInPictureMode) { onBack() }
    }

    private fun initSuggestions() {
        PlayerContentFragment.launch(
            context = this@PlayerActivity,
            contentPlayer = contentPlayer,
            chapter = chapter
        )
    }

    //Casting

    private fun initCastSettings() {
        playerManager.castManager.initFactory(this,mediaRouteButton)
        playerManager.castManager.onMediaStatus()
    }

    fun setMetaData() {
        textTitle.text = chapter.title
        textChapterNumber.text = getString(R.string.chapter_number_short,chapter.number)
    }

    fun preparingLayoutByMode() {
        hideSystemUI()

        if (playerManager.castPlayer?.isCastSessionAvailable == false) {

            playerView.player = playerManager.exoPlayer
            playerCover.visibility = View.GONE
            containerPlayerOperations.visibility = View.VISIBLE
            playerView.controllerShowTimeoutMs = 3500
            playerView.controllerHideOnTouch = true
            bottomControlsPlayer.visibility = View.VISIBLE
            playerManager.exoPlayer?.play()
            playerManager.hideCastingMessage(textCasting)

        }
        else {

            playerView.player = playerManager.castPlayer
            containerPlayerOperations.visibility = View.GONE
            playerCover.visibility = View.VISIBLE
            playerView.showController()
            playerView.controllerShowTimeoutMs = 0
            playerView.controllerHideOnTouch = false
            bottomControlsPlayer.visibility = View.GONE
            textTitle.visibility = View.GONE
            playerManager.exoPlayer?.pause()
            playerManager.showCastingMessage(textCasting)

        }
    }

    override fun onLayoutChange(p0: View?, p1: Int, p2: Int, p3: Int,
                                p4: Int, p5: Int, p6: Int, p7: Int, p8: Int) {
        val sourceRectHint = Rect()
        playerView.getGlobalVisibleRect(sourceRectHint)
    }

    companion object {
        const val IS_FROM_CONTENT_PLAYER = "IS_FROM_CONTENT_PLAYER"
    }
}