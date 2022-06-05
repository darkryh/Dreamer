package com.ead.project.dreamer.data.database.model.server

import com.ead.project.dreamer.data.database.model.Player
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.VideoModel

class Mega (var url : String) : Server() {

    init {
        player = Player.Mega
        isDirect = false
        url = url.replace("/#","/embed#")
        videoList.add(VideoModel("Default",url))
    }
}