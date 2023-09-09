package com.ead.project.dreamer.data.utils

import com.ead.project.dreamer.app.data.files.Files
import com.ead.project.dreamer.app.data.network.Network
import com.ead.project.dreamer.app.data.util.HttpUtil
import com.ead.project.dreamer.data.database.model.Chapter
import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.partialcontent.PartialContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

object LocalServer {

    private const val PORT = 5001
    val address = "http://${Network.getIpAddress()}:$PORT"

    private val server by lazy {
        embeddedServer(Netty, PORT, watchPaths = emptyList()) {
            install(WebSockets)
            install(CallLogging)
            install(PartialContent)
            routing {
                get("/") {
                    call.respondText(
                        text = "Dreamer Server Ok?: Hello there",
                        contentType = ContentType.Text.Plain
                    )
                }
            }
        }
    }

    fun start() = server.start(false)

    fun add(chapter: Chapter) =  server.application.routing {
        get("/${chapter.routeName()}") {
            val file = File(Files.getChapterRoute(chapter))
            call.respondFile(file)
        }
    }

    fun isStarted() : Boolean = runBlocking {
        withContext(Dispatchers.IO) {
            HttpUtil.connectionAvailable(address)
        }
    }
}