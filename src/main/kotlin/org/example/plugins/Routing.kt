package org.example.plugins

import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import org.example.merger.AudioManager

fun Application.configureRouting() {

    // Starting point for a Ktor app:
    routing {
        get("/tts/{name}") {
            call.parameters["name"]?.let {
                AudioManager.getFile(it)?.let{
                    call.respondBytes(it)
                }
            }
        }
    }
    routing {
    }
}
