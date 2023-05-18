package com.ead.project.dreamer.app.data.player.casting

import com.ead.project.dreamer.app.data.util.system.longToSeconds
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastState
import com.google.android.gms.cast.framework.CastStateListener
import com.google.android.gms.cast.framework.Session
import com.google.android.gms.cast.framework.SessionManagerListener
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import javax.inject.Inject

class CastMediaSession @Inject constructor (
    private val castContext: CastContext
) {

    val session = castContext.sessionManager

    private val currentSession get() = session.currentCastSession
    private val remoteMediaClient get() = currentSession?.remoteMediaClient

    private val streamDurationLong : Long get() = remoteMediaClient?.streamDuration?:1L
    private val remainingTimeLong : Long get() = currentSession?.sessionRemainingTimeMs?:-1L
    private val currentStreamProgress : Int get() = (streamDurationLong - remainingTimeLong).longToSeconds()

    val stream : Int get() {
        return when (currentStreamProgress) {
            0 -> streamDurationProgress
            else -> currentStreamProgress
        }
    }

    val streamDurationProgress : Int get() = streamDurationLong.longToSeconds()

    val isConnected get() = castContext.castState == CastState.CONNECTED

    fun addCastStateListener(castStateListener: CastStateListener) {
        castContext.addCastStateListener(castStateListener)
    }

    fun addSessionManagerListener(sessionManagerListener: SessionManagerListener<Session>) {
        session.addSessionManagerListener(sessionManagerListener)
    }

    fun registerRemoteClient(callBack : RemoteMediaClient.Callback) {
        remoteMediaClient?.registerCallback(callBack)
    }

    fun removeCastStateListener(castStateListener: CastStateListener) {
        castContext.removeCastStateListener(castStateListener)
    }

    fun removeSessionManagerListener(sessionManagerListener: SessionManagerListener<Session>) {
        session.removeSessionManagerListener(sessionManagerListener)
    }

    fun removeRemoteClient(callBack : RemoteMediaClient.Callback) {
        remoteMediaClient?.unregisterCallback(callBack)
    }

    fun getRemoteClient() : RemoteMediaClient? = remoteMediaClient

    fun stop() {
        session.endCurrentSession(true)
    }

    fun onPerformAction(task : () -> Unit) {
        if (remoteMediaClient != null) {
            task()
        }
    }
}