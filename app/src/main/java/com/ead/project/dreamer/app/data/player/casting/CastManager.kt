package com.ead.project.dreamer.app.data.player.casting

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.mediarouter.app.MediaRouteButton
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.app.data.util.TimeUtil
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.domain.ObjectUseCase
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.google.android.gms.cast.MediaStatus
import com.google.android.gms.cast.framework.*
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CastManager @Inject constructor(
    private var activity: Activity?=null,
    private val context: Context,
    private val objectUseCase: ObjectUseCase,
    castContext: CastContext,
    preferenceUseCase: PreferenceUseCase
) {

    private val scope : CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val castMediaSession : CastMediaSession = CastMediaSession(castContext)
    private val castBinding : CastBinding = CastBinding(context, castMediaSession)

    private val playerPreferences = preferenceUseCase.playerPreferences
    private val appBuildPreferences = preferenceUseCase.appBuildPreferences

    var isAvailable : Boolean = false
    val isConnectedToChromeCast get() = castMediaSession.isConnected

    private var chapter: Chapter? = getChapter()
    private var mediaRouteButton: MediaRouteButton?=null

    fun initFactory(activity: Activity, mediaRouteButton: MediaRouteButton) {
        this.activity = activity
        this.mediaRouteButton = mediaRouteButton
        CastButtonFactory.setUpMediaRouteButton(context,mediaRouteButton)
    }

    fun showCastingTextView(text: TextView) { castBinding.showCastingTextView(text) }

    fun hideCastingTextView(text: TextView) { castBinding.hideCastingTextView(text) }

    fun onResume() {
        castMediaSession.addCastStateListener(castStateListener)
        castMediaSession.addSessionManagerListener(sessionManagerListener)
        updatedChapter()
    }


    fun stopSession() = castMediaSession.stop()

    fun onPause() {
        castMediaSession.removeCastStateListener(castStateListener)
        castMediaSession.removeSessionManagerListener(sessionManagerListener)
    }

    fun onDestroy() {
        castMediaSession.removeRemoteClient(remoteCallBack)
    }

    fun getChapter() = playerPreferences.getCastingChapter()

    fun updatedChapter(isConsumed : Boolean = false) {
        castMediaSession.onPerformAction {

            chapter = playerPreferences.getCastingChapter()

            val totalProgress : Int
            val currentProgress : Int
            var isContentConsumed = false

            if (isConsumed) {

                val totalDuration = castMediaSession.streamDurationProgress

                totalProgress = totalDuration
                currentProgress = totalDuration

            }
            else {

                totalProgress = castMediaSession.streamDurationProgress
                currentProgress = castMediaSession.stream

                isContentConsumed = chapter?.isMediaWatched?:false
            }

            chapter = chapter?.copy(
                totalProgress = totalProgress,
                currentProgress = currentProgress,
                isContentConsumed = isContentConsumed,
                lastDateSeen = TimeUtil.getNow()
            )?.apply {

                playerPreferences.setCastingChapter(this)
                updateObject(this)

            }
        }
    }

    private val castStateListener = CastStateListener { newState ->

        when (newState) {
            CastState.NO_DEVICES_AVAILABLE -> {

            }
            CastState.NOT_CONNECTED -> {

            }
            CastState.CONNECTING -> {

            }
            CastState.CONNECTED -> {

            }
        }

        if (newState != CastState.NO_DEVICES_AVAILABLE) {

            isAvailable = appBuildPreferences.isUnlockedVersion()
            mediaRouteButton?.setVisibility(isAvailable)

            castBinding.showIntroductoryOverlay(activity?:return@CastStateListener,mediaRouteButton?:return@CastStateListener)

        }
        else {

            isAvailable = false
            mediaRouteButton?.setVisibility(false)

        }

    }

    private val sessionManagerListener : SessionManagerListener<Session> = object : SessionManagerListener<Session>{

        override fun onSessionStarted(session: Session, p1: String) {

            Log.d(TAG, "onSessionStarted: ")
            castMediaSession.registerRemoteClient(remoteCallBack)

        }

        override fun onSessionStarting(session: Session) {

            Log.d(TAG, "onSessionStarting: ")

        }

        override fun onSessionStartFailed(session: Session, p1: Int) {

            Log.d(TAG, "onSessionStartFailed: ")

        }

        override fun onSessionResumed(session: Session, p1: Boolean) {

            Log.d(TAG, "onSessionResumed: ")
            castMediaSession.registerRemoteClient(remoteCallBack)

        }

        override fun onSessionResuming(session: Session, p1: String) {

            Log.d(TAG, "onSessionResuming: ")

        }

        override fun onSessionResumeFailed(session: Session, p1: Int) {

            Log.d(TAG, "onSessionResumeFailed: ")

        }

        override fun onSessionEnded(session: Session, p1: Int) {

            Log.d(TAG, "onSessionEnded: ")
            castMediaSession.removeRemoteClient(remoteCallBack)

        }

        override fun onSessionEnding(session: Session) {

            Log.d(TAG, "onSessionEnding: ")
            updatedChapter()

        }

        override fun onSessionSuspended(session: Session, p1: Int) {

            Log.d(TAG, "onSessionSuspended: ")
            updatedChapter()
            castMediaSession.removeRemoteClient(remoteCallBack)

        }
    }

    private fun updateObject(chapter: Chapter) {

        if (chapter.isMediaInitialized) {
            scope.launch {
                objectUseCase.updateObject(chapter)
            }
        }

    }

    private val remoteCallBack : RemoteMediaClient.Callback = object : RemoteMediaClient.Callback() {

        @SuppressLint("VisibleForTests")
        override fun onStatusUpdated() {
            super.onStatusUpdated()

            when(castMediaSession.getRemoteClient()?.mediaStatus?.playerState) {
                MediaStatus.IDLE_REASON_FINISHED -> {
                    updatedChapter(true)
                }
            }

        }

    }
}