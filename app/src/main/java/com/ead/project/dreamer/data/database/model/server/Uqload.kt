package com.ead.project.dreamer.data.database.model.server

import com.ead.project.dreamer.data.database.model.Server

class Uqload (var url : String) : Server() {

    init {
        isDirect = false
        /*videoList.add(VideoModel("Default",url))*/
    }
}