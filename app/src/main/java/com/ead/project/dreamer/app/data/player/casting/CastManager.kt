package com.ead.project.dreamer.app.data.player.casting

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.mediarouter.app.MediaRouteButton
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.app.AppInfo
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
    private var isCastingDeviceAvailable = false
    private val isGoogleVersion = AppInfo.isGoogleAppVersion

    val isConnectedToChromeCast get() = castMediaSession.isConnected

    private var chapter: Chapter? = getChapter()
    private var mediaRouteButton: MediaRouteButton?=null

    fun initFactory(activity: Activity, mediaRouteButton: MediaRouteButton) {
        this.activity = activity
        this.mediaRouteButton = mediaRouteButton
        mediaRouteButton.setVisibility(isCastingDeviceAvailable)
        CastButtonFactory.setUpMediaRouteButton(context,mediaRouteButton)
    }

    fun showCastingTextView(text: TextView) { castBinding.showCastingTextView(text) }

    fun hideCastingTextView(text: TextView) { castBinding.hideCastingTextView(text) }

    fun onResume() {
        castMediaSession.addCastStateListener(castStateListener)
        castMediaSession.addSessionManagerListener(sessionManagerListener)
        onMediaStatus()
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

    fun getUpdatedChapter() : Chapter? {
        val chapter = getChapter() ?: return null

        val totalProgress = castMediaSession.streamDurationProgress
        val currentProgress = castMediaSession.stream

        return chapter.copy(
            currentProgress = currentProgress,
            totalProgress = totalProgress,
            isContentConsumed = chapter.isMediaWatched
        )
    }

    fun onMediaStatus() {
        castMediaSession.onPerformAction {

            val isConsumed = (castMediaSession.getRemoteClient()?.mediaStatus?: return@onPerformAction).playerState == MediaStatus.IDLE_REASON_FINISHED
            chapter = playerPreferences.getCastingChapter()

            val totalProgress : Int
            val currentProgress : Int

            if (isConsumed) {

                val totalDuration = castMediaSession.streamDurationProgress

                totalProgress = totalDuration
                currentProgress = totalDuration

            }
            else {

                totalProgress = castMediaSession.streamDurationProgress
                currentProgress = castMediaSession.stream
            }

            chapter = chapter?.copy(
                totalProgress = totalProgress,
                currentProgress = currentProgress
            )

            chapter = chapter?.copy(
                isContentConsumed = chapter?.isMediaWatched?:false,
                lastDateSeen = TimeUtil.getNow()
            )?.apply {

                playerPreferences.setCastingChapter(this)
                updateObject(this)

            }
        }
    }

    private val castStateListener = CastStateListener { newState ->

        isCastingDeviceAvailable = newState != CastState.NO_DEVICES_AVAILABLE && !isGoogleVersion
        mediaRouteButton?.setVisibility(isCastingDeviceAvailable)

        if (isCastingDeviceAvailable) {

            castBinding.showIntroductoryOverlay(activity?:return@CastStateListener,mediaRouteButton?:return@CastStateListener)

        }
    }

    private val sessionManagerListener : SessionManagerListener<Session> = object : SessionManagerListener<Session>{

        private var isMediaConsumed = false

        override fun onSessionStarted(session: Session, p1: String) {

            Log.d(TAG, "onSessionStarted: ")
            isMediaConsumed = false
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
            onMediaStatus()

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
            onMediaStatus()

        }

        override fun onSessionSuspended(session: Session, p1: Int) {

            Log.d(TAG, "onSessionSuspended: ")
            onMediaStatus()
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

            when(castMediaSession.getRemoteClient()?.idleReason) {
                MediaStatus.IDLE_REASON_FINISHED -> {
                    onMediaStatus()
                }
            }
        }

    }
}