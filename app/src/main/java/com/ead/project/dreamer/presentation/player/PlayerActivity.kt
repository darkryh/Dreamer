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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.mediarouter.app.MediaRouteButton
import coil.load
import com.ead.commons.lib.lifecycle.activity.onBack
import com.ead.commons.lib.lifecycle.activity.onBackHandle
import com.ead.commons.lib.lifecycle.parcelable
import com.ead.commons.lib.lifecycle.parcelableArrayList
import com.ead.commons.lib.views.addSelectableItemEffect
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.player.PlayerManager
import com.ead.project.dreamer.app.data.util.system.hideSystemUI
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.data.system.extensions.toast
import com.ead.project.dreamer.data.utils.Thread
import com.ead.project.dreamer.data.utils.ui.PlayerOnScaleGestureListener
import com.ead.project.dreamer.presentation.player.content.PlayerContentFragment
import com.ead.project.dreamer.presentation.player.content.scalegesture.ScaleGestureFragment
import com.ead.project.dreamer.presentation.player.content.trackselector.TrackSelectorFragment
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
    private var previousChapter : Chapter?=null
    var playList : List<VideoModel> = emptyList()

    private var orientation : Int = 0
    private val aspectRatio : Float = (16f / 9f)
    private val rationalAspectRatio = Rational(16,9)
    private val pipPermissionTimeout : Long = 30L

    private val fixedWidthMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
    private val fillMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
    private val fitMode = AspectRatioFrameLayout.RESIZE_MODE_FIT

    private val playerView : PlayerView by lazy { findViewById(R.id.styled_player_view) }
    private val controller : AspectRatioFrameLayout by lazy { findViewById(R.id.dreamer_controller) }
    private val aspectFrameRatio : AspectRatioFrameLayout by lazy { findViewById(R.id.aspect_ratio) }

    private val bottomControlsPlayer : ConstraintLayout by lazy { findViewById(R.id.bottom_controls) }
    private val playerCover : RelativeLayout by lazy { findViewById(R.id.relative_cover_dreamer) }
    private val contentPlayer  : FrameLayout by lazy { findViewById(R.id.frame_content) }

    private val buttonChangeOrientationScreen : LinearLayout by lazy { findViewById(R.id.linear_fullscreen) }
    private val buttonClose : ImageView by lazy { findViewById(R.id.button_close_player) }
    private val buttonSettings : LinearLayout by lazy { findViewById(R.id.linear_settings) }
    //private val buttonPlaylist : LinearLayout by lazy { findViewById(R.id.ln_play_list) }
    private val buttonGesture : LinearLayout by lazy { findViewById(R.id.linear_gesture) }

    private val imagePlayerOrientationScreen : ImageView by lazy { findViewById(R.id.button_fullscreen) }
    private val imageChapterCover : ImageView by lazy { findViewById(R.id.image_cover_dreamer) }

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
        launchSuggestions()
    }

    private fun initSettings() {
        supportActionBar?.hide()
    }

    private fun initPlayer() {
        playerManager.setMediaRouteButton(mediaRouteButton)
        playerManager.initPlayer()
        setOrientationMode(orientation)
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
        imageChapterCover.load(chapter.cover)
        //buttonPlaylist.addSelectableItemEffect()
        buttonSettings.addSelectableItemEffect()
        imagePlayerOrientationScreen.addSelectableItemEffect()
        buttonGesture.addSelectableItemEffect()
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
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
                else {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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
                val fragmentManager: FragmentManager = supportFragmentManager
                val trackSelectorFragment = TrackSelectorFragment()
                trackSelectorFragment.player = playerManager.currentPlayer as ExoPlayer
                trackSelectorFragment.playlist = playList.asReversed()
                trackSelectorFragment.trackSelector = playerManager.trackSelector
                trackSelectorFragment.playerView = playerManager.playerView
                trackSelectorFragment.show(fragmentManager, null)
            }
            else {
                toast(getString(R.string.casting_mode))
            }

        }

        buttonGesture.setOnClickListener {

            if (playerManager.isNotCasting()) {
                val fragmentManager: FragmentManager = supportFragmentManager
                val scaleGestureFragment = ScaleGestureFragment()
                scaleGestureFragment.playerView = playerManager.playerView
                scaleGestureFragment.show(fragmentManager, null)
            }
            else {
                toast(getString(R.string.casting_mode))
            }

        }

        /*buttonPlaylist.setOnClickListener {

            val fragmentManager: FragmentManager = supportFragmentManager
            val chapterSelectorFragment = ChapterSelectorFragment()
            chapterSelectorFragment.isHorizontal = orientation == Configuration.ORIENTATION_LANDSCAPE
            chapterSelectorFragment.playerView = playerManager.playerView
            chapterSelectorFragment.chapter = chapter
            chapterSelectorFragment.show(fragmentManager, null)

        }*/
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
        setOrientationMode(orientation)
    }

    private fun setOrientationMode(orientation : Int) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            portraitMode()
        }
         else {
            horizontalMode()
        }
    }

    private fun portraitMode() {
        imagePlayerOrientationScreen.setImageDrawable(AppCompatResources
            .getDrawable(this,R.drawable.ic_fullscreen_24))
        playerOnScaleGestureListener?.isHorizontalMode = false
        controller.setAspectRatio(aspectRatio)
        aspectFrameRatio.setAspectRatio(aspectRatio)
        textTitle.visibility = View.GONE
        aspectFrameRatio.resizeMode = fixedWidthMode
        playerView.resizeMode = fixedWidthMode
        controller.resizeMode = fixedWidthMode
    }

    private fun horizontalMode() {
        imagePlayerOrientationScreen.setImageDrawable(
            AppCompatResources
                .getDrawable(this,R.drawable.ic_fullscreen_exit_24))
        playerOnScaleGestureListener?.isHorizontalMode = true
        if (playerManager.isNotCasting()) {
            textTitle.visibility = View.VISIBLE
        }
        aspectFrameRatio.resizeMode = fillMode
        controller.resizeMode = fillMode
        playerView.resizeMode = fitMode
    }

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

            Thread.runInMs({checkPIPPermission()}, pipPermissionTimeout)

        }
    }

    private fun checkPIPPermission() {
        playerManager.isPIPModeEnabled = isInPictureInPictureMode
        if(!isInPictureInPictureMode) { onBack() }
    }

    private fun launchSuggestions() {
        contentPlayer.removeAllViews()
        val fragment = PlayerContentFragment()
        val data = Bundle()
        val transaction = supportFragmentManager
            .beginTransaction()
        data.putParcelable(Chapter.REQUESTED,chapter)
        fragment.arguments = data
        transaction.replace(R.id.frame_content,fragment).commit()
    }

    //Casting

    private fun initCastSettings() {
        playerManager.castManager.initFactory(this,mediaRouteButton)
        playerManager.castManager.onMediaStatus()
    }

    fun setMetaData() {
        textTitle.text = chapter.title
        textChapterNumber.text = getString(R.string.chapter_number_short,chapter.number.toString())
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