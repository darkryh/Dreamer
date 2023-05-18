package com.ead.project.dreamer.app.repository

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseClient @Inject constructor() {

    val inAppMessage : FirebaseMessaging get() =  Firebase.messaging

}