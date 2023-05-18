package com.ead.project.dreamer.app.model

data class Requester(
    val isRequesting : Boolean,
    val profileId : Int,
    val profileReference : String
) {

    companion object {
        val Deactivate = Requester(isRequesting = false, profileId = -1, profileReference = "null" )
    }
}
