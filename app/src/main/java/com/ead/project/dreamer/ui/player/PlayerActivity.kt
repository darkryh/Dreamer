package com.ead.project.dreamer.ui.player

import android.app.PictureInPictureParams
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
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
import androidx.mediarouter.app.MediaRouteButton
import coil.load
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools.Companion.hideSystemUI
import com.ead.project.dreamer.data.commons.Tools.Companion.onBackHandle
import com.ead.project.dreamer.data.commons.Tools.Companion.onBackHandlePressed
import com.ead.project.dreamer.data.commons.Tools.Companion.parcelable
import com.ead.project.dreamer.data.commons.Tools.Companion.parcelableArrayList
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.data.utils.ThreadUtil
import com.ead.project.dreamer.data.utils.media.CastManager
import com.ead.project.dreamer.data.utils.media.PlayerManager
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.data.utils.ui.DreamerOnScaleGestureListener
import com.ead.project.dreamer.ui.player.content.PlayerContentFragment
import com.ead.project.dreamer.ui.player.content.chapterselector.ChapterSelectorFragment
import com.ead.project.dreamer.ui.player.content.scalegesture.ScaleGestureFragment
import com.ead.project.dreamer.ui.player.content.trackselector.TrackSelectorFragment
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerActivity : AppCompatActivity(),View.OnLayoutChangeListener   {

    private val playerViewModel : PlayerViewModel by viewModels()
    private lateinit var playerView : StyledPlayerView
    private lateinit var dreamController : AspectRatioFrameLayout
    private lateinit var aspectRatio : AspectRatioFrameLayout
    lateinit var playList : List<VideoModel>

    lateinit var chapter: Chapter
    private var orientation : Int = 0

    private lateinit var relativeCover : RelativeLayout
    private lateinit var bottomControls : ConstraintLayout
    private lateinit var frameContent  : FrameLayout

    private lateinit var buttonScreen : ImageView
    private lateinit var buttonClose : ImageView
    private lateinit var buttonSettings : ImageView
    private lateinit var buttonPlayList : ImageView
    private lateinit var imvChapterCover : ImageView

    private lateinit var lnContentReference : LinearLayout
    private lateinit var lnOperator : LinearLayout
    private lateinit var lnScreen : LinearLayout
    private lateinit var lnSettings : LinearLayout
    private lateinit var lnPlaylist : LinearLayout
    private lateinit var lnGesture : LinearLayout

    private lateinit var txvTitle : TextView
    private lateinit var txvCasting : TextView

    private var scaleGestureDetector: ScaleGestureDetector? = null
    private var dreamerOnScaleGestureListener : DreamerOnScaleGestureListener?=null

    private lateinit var mediaRouteButton: MediaRouteButton
    private var castManager = CastManager()
    private val previousChapter : Chapter?= castManager.getPreviousCast()
    private lateinit var playerManager: PlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        onBackHandle { onBackPressedMode()  }
        init(false)
        initSettings()
        initPlayer()
        castSettings()
    }

    override fun onNewIntent(intent: Intent) {
        playerManager.preNewIntent()
        super.onNewIntent(intent)
        setIntent(intent)
        init(true)
        playerManager.onNewIntent()
        onStart()
    }

    private fun init(isNewIntent : Boolean){
        initVariables()
        if (!isNewIntent) initLayouts()
        prepareLayout()
        launchSuggestions()
    }

    private fun initSettings() {
        supportActionBar?.hide()
    }

    private fun initPlayer() {
        playerManager = PlayerManager(this,playerViewModel, playerView,chapter, playList,castManager)
        playerManager.mediaRouteButton = mediaRouteButton
        playerManager.initPlayer()
        setOrientationMode(orientation)
    }

    private fun initVariables() {
        chapter = intent.extras!!.parcelable(Constants.REQUESTED_CHAPTER)!!
        playList = intent.extras!!.parcelableArrayList(Constants.PLAY_VIDEO_LIST)!!
        orientation = resources.configuration.orientation
        Chapter.set(chapter)
    }

    private fun initLayouts() {
        playerView = findViewById(R.id.styledPlayerView)
        dreamController = findViewById(R.id.dream_controller)
        mediaRouteButton = findViewById(R.id.mrbInPlayer)
        aspectRatio = findViewById(R.id.aspectRatio)
        frameContent = findViewById(R.id.Frame_Content)
        txvTitle = findViewById(R.id.txvChapterTitle)
        txvCasting = findViewById(R.id.txvCasting)
        buttonScreen = findViewById(R.id.bt_fullscreen)
        buttonPlayList = findViewById(R.id.bt_play_list)
        buttonClose = findViewById(R.id.bt_close_player)
        buttonSettings = findViewById(R.id.bt_settings)
        relativeCover = findViewById(R.id.relativeCoverDreamer)
        bottomControls = findViewById(R.id.bottom_controls)
        imvChapterCover = findViewById(R.id.imvCoverDreamer)
        lnSettings = findViewById(R.id.lnSettings)
        lnGesture = findViewById(R.id.lnGesture)
        lnOperator = findViewById(R.id.lnOperator)
        lnScreen = findViewById(R.id.ln_fullscreen)
        lnPlaylist = findViewById(R.id.ln_play_list)
        lnContentReference = findViewById(R.id.content_reference)
        dreamerOnScaleGestureListener = DreamerOnScaleGestureListener(playerView)
        dreamerOnScaleGestureListener?.let { scaleGestureDetector = ScaleGestureDetector(this, it) }
    }

    private fun prepareLayout() {
        imvChapterCover.load(chapter.chapterCover)
        DreamerLayout.setClickEffect(lnPlaylist,this)
        DreamerLayout.setClickEffect(lnSettings,this)
        DreamerLayout.setClickEffect(buttonScreen,this)
        DreamerLayout.setClickEffect(lnGesture,this)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        event?.let { scaleGestureDetector?.onTouchEvent(it) }
        return super.dispatchTouchEvent(event)
    }

    private fun devicesCompatibleWithPipMode() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)

    private fun settingControllerViews() {
        lnScreen.setOnClickListener {
            requestedOrientation =
                if (orientation == Configuration.ORIENTATION_PORTRAIT) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        buttonClose.setOnClickListener {
            playerManager.playerView.hideController()
            onBackHandlePressed()
        }
        setOnClickInPlayer()
    }

    private fun setOnClickInPlayer() {
        lnSettings.setOnClickListener {
            if (!playerManager.isPlayerCastMode()) {
                val fragmentManager: FragmentManager = supportFragmentManager
                val trackSelectorFragment = TrackSelectorFragment()
                trackSelectorFragment.player = playerManager.currentPlayer!! as ExoPlayer
                trackSelectorFragment.videoModelList = playList.asReversed()
                trackSelectorFragment.trackSelector = playerManager.trackSelector
                trackSelectorFragment.playerView = playerManager.playerView
                trackSelectorFragment.show(fragmentManager, null)
            }
            else DreamerApp.showLongToast(getString(R.string.casting_mode))
        }
        lnGesture.setOnClickListener {
            if (!playerManager.isPlayerCastMode()) {
                val fragmentManager: FragmentManager = supportFragmentManager
                val scaleGestureFragment = ScaleGestureFragment()
                scaleGestureFragment.playerView = playerManager.playerView
                scaleGestureFragment.show(fragmentManager, null)
            }
            else DreamerApp.showLongToast(getString(R.string.casting_mode))
        }
        lnPlaylist.setOnClickListener {
            val fragmentManager: FragmentManager = supportFragmentManager
            val chapterSelectorFragment = ChapterSelectorFragment()
            chapterSelectorFragment.isHorizontal = orientation == Configuration.ORIENTATION_LANDSCAPE
            chapterSelectorFragment.playerView = playerManager.playerView
            chapterSelectorFragment.show(fragmentManager, null)
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
    }

    public override fun onStop() {
        super.onStop()
        playerManager.onStop()
        if (devicesCompatibleWithPipMode()) {
            finishAndRemoveTask()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        orientation = resources.configuration.orientation
        setOrientationMode(orientation)
    }

    private fun setOrientationMode(orientation : Int) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) portraitMode()
         else horizontalMode()
    }

    private fun portraitMode() {
        buttonScreen.setImageDrawable(AppCompatResources
                .getDrawable(this,R.drawable.ic_fullscreen_24))
        dreamerOnScaleGestureListener?.isHorizontalMode = false
        dreamController.setAspectRatio(16f/9f)
        aspectRatio.setAspectRatio(16f/9f)
        txvTitle.visibility = View.GONE
        aspectRatio.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
        dreamController.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
    }

    private fun horizontalMode() {
        buttonScreen.setImageDrawable(
            AppCompatResources
                .getDrawable(this,R.drawable.ic_fullscreen_exit_24))
        dreamerOnScaleGestureListener?.isHorizontalMode = true
        if (playerManager.isNotCasting()) txvTitle.visibility = View.VISIBLE
        aspectRatio.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
        dreamController.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
    }

    //PIP-MODE

    private fun managePipMode() {
        if(shouldGoPipMode()) {
            enterPIPMode()
        } else { finish() }
    }

    private fun onBackPressedMode() { managePipMode() }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        managePipMode()
    }

    private fun shouldGoPipMode() = devicesCompatibleWithPipMode() && playerManager.isPIPModeEnabled
            && playerManager.isNotCasting()

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            handlePipModeViews(isInPictureInPictureMode)
            super.onPictureInPictureModeChanged(isInPictureInPictureMode)
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            handlePipModeViews(isInPictureInPictureMode)
            super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        }
    }

    private fun handlePipModeViews(isInPictureInPictureMode : Boolean) {
        playerManager.isInPipMode = !isInPictureInPictureMode
        if (isInPictureInPictureMode) {
            lnContentReference.visibility = View.GONE
            // Hide the full-screen UI (controls, etc.) while in picture-in-picture mode.
        } else {
            lnContentReference.visibility = View.VISIBLE
            // Restore the full-screen UI.
        }
    }

    @Suppress("DEPRECATION")
    private fun enterPIPMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
            && playerManager.isPIPModeEnabled) {
            playerManager.playbackPosition = playerManager.exoPlayer!!.currentPosition
            playerView.useController = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val params = PictureInPictureParams.Builder()
                    .setSourceRectHint(Rect())
                    .setAspectRatio(Rational(16, 9))
                this.enterPictureInPictureMode(params.build())
            } else {
                this.enterPictureInPictureMode()
            }
            ThreadUtil.runInMs({checkPIPPermission()}, 30)
        }
    }

    private fun checkPIPPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            playerManager.isPIPModeEnabled = isInPictureInPictureMode
            if(!isInPictureInPictureMode) { onBackHandlePressed() }
        }
    }

    private fun launchSuggestions() {
        frameContent.removeAllViews()
        val fragment = PlayerContentFragment()
        val data = Bundle()
        val transaction = supportFragmentManager
            .beginTransaction()
        data.putParcelable(Constants.REQUESTED_CHAPTER,chapter)
        fragment.arguments = data
        transaction.replace(R.id.Frame_Content,fragment).commit()
    }

    //Casting

    private fun castSettings() {
        playerManager.castManager.setButtonFactory(mediaRouteButton)
        if (previousChapter != null) {
            playerManager.castManager.setPreviousCast("null")
            playerViewModel.updateChapter(previousChapter)
        }
    }

    fun setMetaData() {
        txvTitle.text = getString(R.string.title_player,chapter.title,chapter.chapterNumber)
    }

    fun preparingLayoutByMode() {
        hideSystemUI()
        if (playerManager.castPlayer?.isCastSessionAvailable == false) {
            playerView.player = playerManager.exoPlayer
            relativeCover.visibility = View.GONE
            lnOperator.visibility = View.VISIBLE
            playerView.controllerShowTimeoutMs = 3500
            playerView.controllerHideOnTouch = true
            bottomControls.visibility = View.VISIBLE
            playerManager.exoPlayer?.play()
            playerManager.hideCastingMessage(txvCasting)
        }
        else {
            playerView.player = playerManager.castPlayer
            lnOperator.visibility = View.GONE
            relativeCover.visibility = View.VISIBLE
            playerView.showController()
            playerView.controllerShowTimeoutMs = 0
            playerView.controllerHideOnTouch = false
            bottomControls.visibility = View.GONE
            txvTitle.visibility = View.GONE
            playerManager.exoPlayer?.pause()
            playerManager.showCastingMessage(txvCasting)
        }
    }

    override fun onLayoutChange(p0: View?, p1: Int, p2: Int, p3: Int,
                                p4: Int, p5: Int, p6: Int, p7: Int, p8: Int) {
        val sourceRectHint = Rect()
        playerView.getGlobalVisibleRect(sourceRectHint)
    }
}