package com.ead.project.dreamer.ui.player

import android.app.PictureInPictureParams
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.WindowManager
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
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.database.model.VideoModel
import com.ead.project.dreamer.data.utils.ui.DreamerLayout
import com.ead.project.dreamer.data.utils.ui.DreamerOnScaleGestureListener
import com.ead.project.dreamer.data.utils.ThreadUtil
import com.ead.project.dreamer.data.utils.media.CastManager
import com.ead.project.dreamer.data.utils.media.PlayerManager
import com.ead.project.dreamer.ui.player.content.PlayerContentFragment
import com.ead.project.dreamer.ui.player.content.chapterselector.ChapterSelectorFragment
import com.ead.project.dreamer.ui.player.content.scalegesture.ScaleGestureFragment
import com.ead.project.dreamer.ui.player.content.trackselector.TrackSelectorFragment
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerActivity : AppCompatActivity() {

    private val playerViewModel : PlayerViewModel by viewModels()
    private lateinit var playerView : StyledPlayerView
    private lateinit var dreamController : AspectRatioFrameLayout
    private lateinit var aspectRatio : AspectRatioFrameLayout
    lateinit var videoList : List<VideoModel>

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

    private lateinit var buttonPlay : ImageButton
    private lateinit var buttonPause : ImageButton

    private lateinit var txvTitle : TextView

    private var scaleGestureDetector: ScaleGestureDetector? = null
    private var dreamerOnScaleGestureListener : DreamerOnScaleGestureListener?=null

    private lateinit var mediaRouteButton: MediaRouteButton
    private var castManager = CastManager()
    private val previousChapter : Chapter?= castManager.getPreviousCast()
    private lateinit var playerManager: PlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
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

    private fun initPlayer() {
        playerManager = PlayerManager(this,playerViewModel, playerView,chapter, videoList,castManager)
        playerManager.mediaRouteButton = mediaRouteButton
        playerManager.initPlayer()
    }

    private fun initVariables() {
        chapter = intent.extras!!.getParcelable(Constants.REQUESTED_CHAPTER)!!
        videoList = intent.extras!!.getParcelableArrayList(Constants.PLAY_VIDEO_LIST)!!
        orientation = resources.configuration.orientation
    }

    private fun initLayouts() {
        playerView = findViewById(R.id.styledPlayerView)
        dreamController = findViewById(R.id.dream_controller)
        mediaRouteButton = findViewById(R.id.mrbInPlayer)
        aspectRatio = findViewById(R.id.aspectRatio)
        frameContent = findViewById(R.id.Frame_Content)
        txvTitle = findViewById(R.id.txvChapterTitle)
        buttonScreen = findViewById(R.id.bt_fullscreen)
        buttonPlayList = findViewById(R.id.bt_play_list)
        buttonClose = findViewById(R.id.bt_close_player)
        buttonPlay = findViewById(R.id.exo_play)
        buttonPause = findViewById(R.id.exo_pause)
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
        scaleGestureDetector = ScaleGestureDetector(this, dreamerOnScaleGestureListener)
    }

    private fun initSettings() {
        supportActionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setOrientationMode(orientation)
    }

    private fun prepareLayout() {
        buttonPlay.visibility = View.GONE
        buttonPause.visibility = View.VISIBLE
        imvChapterCover.load(chapter.chapterCover)
        DreamerLayout.setClickEffect(lnPlaylist,this)
        DreamerLayout.setClickEffect(lnSettings,this)
        DreamerLayout.setClickEffect(buttonScreen,this)
        DreamerLayout.setClickEffect(lnGesture,this)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        scaleGestureDetector?.onTouchEvent(event)
        return true
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
            onBackPressed()
        }
        buttonPlay.setOnClickListener{
            run {
                it.visibility = View.GONE
                buttonPause.visibility = View.VISIBLE
            }
            playerManager.playerView.player?.play()
        }
        buttonPause.setOnClickListener {
            run {
                it.visibility = View.GONE
                buttonPlay.visibility = View.VISIBLE
            }
            playerManager.playerView.player?.pause()
        }
        setOnClickInPlayer()
        resizePlayerLayout()
    }

    private fun setOnClickInPlayer() {
        lnSettings.setOnClickListener {
            if (!playerManager.isPlayerCastMode()) {
                val fragmentManager: FragmentManager = supportFragmentManager
                val trackSelectorFragment = TrackSelectorFragment()
                trackSelectorFragment.player = playerManager.currentPlayer!! as ExoPlayer
                trackSelectorFragment.videoModelList = videoList.asReversed()
                trackSelectorFragment.trackSelector = playerManager.trackSelector
                trackSelectorFragment.playerView = playerManager.playerView
                trackSelectorFragment.show(fragmentManager, null)
            }
            else DreamerApp.showLongToast("En Modo Casting")
        }
        lnGesture.setOnClickListener {
            if (!playerManager.isPlayerCastMode()) {
                val fragmentManager: FragmentManager = supportFragmentManager
                val scaleGestureFragment = ScaleGestureFragment()
                scaleGestureFragment.playerView = playerManager.playerView
                scaleGestureFragment.show(fragmentManager, null)
            }
            else DreamerApp.showLongToast("En Modo Casting")
        }
        lnPlaylist.setOnClickListener {
            val fragmentManager: FragmentManager = supportFragmentManager
            val chapterSelectorFragment = ChapterSelectorFragment()
            chapterSelectorFragment.isHorizontal = orientation == Configuration.ORIENTATION_LANDSCAPE
            chapterSelectorFragment.playerView = playerManager.playerView
            chapterSelectorFragment.show(fragmentManager, null)
        }
    }


    private fun resizePlayerLayout() {
        playerView.addOnLayoutChangeListener { _, left, top, right, bottom,
                                               oldLeft, oldTop, oldRight, oldBottom ->
            if (left != oldLeft || right != oldRight || top != oldTop || bottom != oldBottom) {
                val sourceRectHint = Rect()
                playerView.getGlobalVisibleRect(sourceRectHint)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    setPictureInPictureParams(PictureInPictureParams.Builder()
                        .setSourceRectHint(sourceRectHint)
                        .build()
                    )
            }
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
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            portraitMode()
        } else  {
            horizontalMode()
        }
    }

    private fun portraitMode() {
        buttonScreen.setImageDrawable(AppCompatResources
                .getDrawable(this,R.drawable.ic_fullscreen_24))
        dreamerOnScaleGestureListener?.isHorizontalMode = false
        dreamController.setAspectRatio(16f/9f)
        aspectRatio.setAspectRatio(16f/9f)
        aspectRatio.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
        dreamController.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
    }

    private fun horizontalMode() {
        buttonScreen.setImageDrawable(
            AppCompatResources
                .getDrawable(this,R.drawable.ic_fullscreen_exit_24))
        dreamerOnScaleGestureListener?.isHorizontalMode = true
        aspectRatio.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
        dreamController.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
    }

    //PIP-MODE

    override fun onBackPressed() {
        if(devicesCompatibleWithPipMode() && playerManager.isPIPModeEnabled
            && !playerManager.isCasting()) {
            onUserLeaveHint()
        } else {
            super.onBackPressed()
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        if(newConfig != null){
            playerManager.isInPipMode = !isInPictureInPictureMode
        }
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

    override fun onUserLeaveHint() {
        if (!playerManager.isCasting()) {
            super.onUserLeaveHint()
            enterPIPMode()
        }
        else {
            onBackPressed()
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
            if(!isInPictureInPictureMode){
                onBackPressed()
            }
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

    private fun run(task: () -> Unit) = ThreadUtil.runInMs(task, 200)

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
        Tools.hideSystemUI(this,playerView)
        if (playerManager.castPlayer?.isCastSessionAvailable == false) {
            playerView.player = playerManager.exoPlayer
            relativeCover.visibility = View.GONE
            lnOperator.visibility = View.VISIBLE
            playerView.controllerShowTimeoutMs = 3500
            playerView.controllerHideOnTouch = true
            bottomControls.visibility = View.VISIBLE
            txvTitle.visibility = View.VISIBLE
            playerManager.exoPlayer?.play()
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
        }
    }
}