package com.ead.project.dreamer.data.utils.media

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.mediarouter.app.MediaRouteButton
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.ui.main.MainActivityViewModel
import com.ead.project.dreamer.ui.player.PlayerViewModel
import com.ead.project.dreamer.ui.profile.AnimeProfileViewModel
import com.google.android.gms.cast.MediaStatus
import com.google.android.gms.cast.framework.*
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.google.gson.Gson
import java.util.*
import kotlin.math.roundToInt


class CastManager (private val initOnCreate : Boolean = false) {

    private var castContext : CastContext = CastContext.getSharedInstance(DreamerApp.INSTANCE)
    private lateinit var castStateListener : CastStateListener
    private var introductoryOverlay : IntroductoryOverlay?= null

    private var sessionManager : SessionManager = castContext.sessionManager
    private var currentSession : CastSession?= null
    private var remoteMediaClient: RemoteMediaClient?= null

    private var chapter: Chapter? = Chapter.getCasting()
    private var viewModel : ViewModel?= null

    private lateinit var mediaRouteButton: MediaRouteButton

    init {
        if (initOnCreate) {
            currentSession = sessionManager.currentCastSession
            remoteMediaClient = currentSession?.remoteMediaClient
        }
    }

    fun getContext () = castContext

    fun setViewModel(viewModel: ViewModel) {
        this.viewModel = viewModel
    }

    private fun setAvailableMode(value : Boolean) = DataStore.writeBoolean(Constants.CASTING_MODE_APP,value)

    fun isAvailable() = DataStore.readBoolean(Constants.CASTING_MODE_APP)

    fun initButtonFactory(activity: Activity,mediaRouteButton: MediaRouteButton) {
        this.mediaRouteButton = mediaRouteButton
        castStateListener = CastStateListener { newState ->
            if (newState != CastState.NO_DEVICES_AVAILABLE && !policies()) {
                mediaRouteButton.visibility = View.VISIBLE
                showIntroductoryOverlay(activity,mediaRouteButton)
                setAvailableMode(true)
            }
            else {
                mediaRouteButton.visibility = View.GONE
                setAvailableMode(false)
            }
        }
        setButtonFactory(mediaRouteButton)
    }

    fun setButtonFactory(mediaRouteButton: MediaRouteButton) {
        CastButtonFactory.setUpMediaRouteButton(DreamerApp.INSTANCE,mediaRouteButton)
    }

    fun castIsConnected() = castContext.castState == CastState.CONNECTED

    private fun policies() = Constants.isGooglePolicyActivate()

    fun onResume() {
        if (!initOnCreate) {
            currentSession = sessionManager.currentCastSession
            remoteMediaClient = currentSession?.remoteMediaClient
        }
        if (isAvailable() && !policies() && mediaRouteButton.visibility == View.GONE)
            mediaRouteButton.visibility = View.VISIBLE

        castContext.addCastStateListener(castStateListener)
        castContext.sessionManager.addSessionManagerListener(sessionManagerListener)
    }

    fun onPause() {
        castContext.removeCastStateListener(castStateListener)
        castContext.sessionManager.removeSessionManagerListener(sessionManagerListener)
    }

    fun onDestroy() {
        unregisterRemote()
        remoteMediaClient = null
        currentSession = null
    }

    private fun showIntroductoryOverlay(activity: Activity,mediaRouteButton: MediaRouteButton) {
        introductoryOverlay?.remove()
        introductoryOverlay = IntroductoryOverlay.Builder(activity,mediaRouteButton)
            .setTitleText("¡Casting Disponible!")
            .setSingleTime()
            .setOverlayColor(R.color.blueLight)
            .setOnOverlayDismissedListener { introductoryOverlay = null }
            .build()

        introductoryOverlay?.show()
    }

    private fun updatedChapter() {
        chapter = Chapter.getCasting()
        Chapter.setStreamDuration(Tools.longToSeconds(remoteMediaClient?.streamDuration!!))
        chapter?.totalToSeen = Tools.longToSeconds(remoteMediaClient?.streamDuration!!)
        chapter?.currentSeen = getStream()
        chapter?.lastSeen = Calendar.getInstance().time

        if (isOnFinalState(chapter)) {
            chapter?.alreadySeen = true
        }
    }

    private fun updatedChapterFinal() {
        chapter = Chapter.getCasting()
        chapter?.totalToSeen = Chapter.getStreamDuration()!!
        chapter?.currentSeen = Chapter.getStreamDuration()!!
        chapter?.lastSeen = Calendar.getInstance().time
        chapter?.alreadySeen = true
    }

    fun setPreviousCast(string: String?=null)  {
        if (remoteMediaClient != null) updatedChapter()
        if (string != null) {
            DataStore.writeStringAsync(Constants.CURRENT_PREVIOUS_CASTING_CHAPTER,
                Gson().toJson(string))
        }
        else
        DataStore.writeStringAsync(Constants.CURRENT_PREVIOUS_CASTING_CHAPTER,
            Gson().toJson(chapter))
    }

    fun getPreviousCast() : Chapter? = try {
        Gson().fromJson(DataStore.readString(Constants.CURRENT_PREVIOUS_CASTING_CHAPTER),Chapter::class.java)
    } catch (e : Exception) { null }


    fun updateChapterMetaData() {
        if (remoteMediaClient != null) {
            updatedChapter()
            applyUpdates(chapter)
        }
    }

    fun updateChapterFinalMetaData() {
        if (remoteMediaClient != null) {
            updatedChapterFinal()
            applyUpdates(chapter)
        }
    }

    private fun isOnFinalState(chapter: Chapter?) : Boolean = try {
        chapter!!.currentSeen >= (chapter.totalToSeen * 0.92).roundToInt()
                && chapter.totalToSeen > 0
    } catch (e : Exception) { false }

    /*fun isPlaying () = remoteMediaClient?.isPlaying

    fun pause() = remoteMediaClient?.pause()

    fun play() = remoteMediaClient?.play()*/

    private fun getStream() : Int {
        return when (currentStream()) {
            0 -> totalStream()
            else -> currentStream()
        }
    }

    private fun registerRemote()= castContext.sessionManager
        .currentCastSession?.remoteMediaClient?.registerCallback(callback)

    private fun unregisterRemote() = castContext.sessionManager
        .currentCastSession?.remoteMediaClient?.unregisterCallback(callback)

    private fun currentStream() = Tools
        .longToSeconds(remoteMediaClient?.streamDuration!! - currentSession?.sessionRemainingTimeMs!!)

    private fun totalStream() = Tools.longToSeconds(remoteMediaClient?.streamDuration!!)

    private val sessionManagerListener : SessionManagerListener<Session> = object : SessionManagerListener<Session>{

        override fun onSessionStarted(session: Session, p1: String) {
            Log.d(TAG, "onSessionStarted: ")
            registerRemote()
        }

        override fun onSessionStarting(session: Session) {
            Log.d(TAG, "onSessionStarting: ")
        }

        override fun onSessionStartFailed(session: Session, p1: Int) {
            Log.d(TAG, "onSessionStartFailed: ")
        }

        override fun onSessionResumed(session: Session, p1: Boolean) {
            Log.d(TAG, "onSessionResumed: ")
            registerRemote()
        }

        override fun onSessionResuming(session: Session, p1: String) {
            Log.d(TAG, "onSessionResuming: ")
        }

        override fun onSessionResumeFailed(session: Session, p1: Int) {
            Log.d(TAG, "onSessionResumeFailed: ")
        }

        override fun onSessionEnded(session: Session, p1: Int) {
            Log.d(TAG, "onSessionEnded: ")
            unregisterRemote()
        }

        override fun onSessionEnding(session: Session) {
            updateChapterMetaData()
            Log.d(TAG, "onSessionEnding: ")
        }

        override fun onSessionSuspended(session: Session, p1: Int) {
            Log.d(TAG, "onSessionSuspended: ")
            updateChapterMetaData()
            unregisterRemote()
        }

    }

    private fun applyUpdates(chapter: Chapter?) {
        if (chapter != null && viewModel != null) {
            when(viewModel) {
                is MainActivityViewModel -> (viewModel as MainActivityViewModel).updateChapter(chapter)
                is AnimeProfileViewModel -> (viewModel as AnimeProfileViewModel).updateChapter(chapter)
                is PlayerViewModel -> (viewModel as PlayerViewModel).updateChapter(chapter)
            }
        }
        else
            DreamerApp.showLongToast("null")
    }

    private val callback : RemoteMediaClient.Callback = object : RemoteMediaClient.Callback() {

        override fun onStatusUpdated() {
            super.onStatusUpdated()
            when(remoteMediaClient?.mediaStatus?.playerState) {
                MediaStatus.IDLE_REASON_FINISHED -> {
                    Log.d(TAG, "onStatusUpdated: IDLE_REASON_FINISHED")
                    updateChapterFinalMetaData()
                }
            }
        }
    }
}