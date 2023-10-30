package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server

class NullServer (embeddedUrl:String) : Server(embeddedUrl,Player.Blank)