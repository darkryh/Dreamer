package com.ead.project.dreamer.data.utils

import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.database.model.Chapter
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

class WebServer {

    companion object {
        const val PORT = 5001
        private const val localAddress = "http://localhost:$PORT"

        private val server by lazy {
            embeddedServer(Netty, PORT, watchPaths = emptyList()) {
                install(WebSockets)
                install(CallLogging)
                install(PartialContent)
                routing {
                    get("/") {
                        call.respondText(
                            text = "Dreamer Server Ok!!",
                            contentType = ContentType.Text.Plain
                        )
                    }
                }
            }
        }

        fun start() = server.start(false)

        fun add (chapter: Chapter) =  server.application.routing {
            get("/${chapter.routeName()}") {
                val file = File(chapter.getDownloadedRouteReference())
                call.respondFile(file)
            }
        }

        fun isStarted() : Boolean = runBlocking {
            withContext(Dispatchers.IO) {
                Tools.isConnectionAvailable(localAddress)
            }
        }
    }
}