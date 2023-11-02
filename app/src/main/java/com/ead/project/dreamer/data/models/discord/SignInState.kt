package com.ead.project.dreamer.data.models.discord

data class SignInState(
    val isSignInSuccessful : Boolean = false,
    val signInError : String? = null
)
