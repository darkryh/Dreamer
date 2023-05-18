package com.ead.project.dreamer.data.utils

import com.ead.project.dreamer.app.data.files.Files
import com.ead.project.dreamer.app.data.network.Network
import com.ead.project.dreamer.app.data.util.HttpUtil
import com.ead.project.dreamer.data.database.model.Chapter
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.PartialContent
import io.ktor.http.ContentType
import io.ktor.response.respondFile
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.WebSockets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

object LocalServer {

    private const val PORT = 5001
    const val address = "http://localhost:$PORT"

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

    fun getAddress() = "http://" + Network.getIpAddress() + ":$PORT"

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