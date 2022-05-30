package org.example

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.example.eventbus.Event
import org.example.eventbus.EventBus
import org.example.eventbus.TYPES
import org.example.merger.AudioManager
import org.example.plugins.*
import java.lang.Exception
import java.nio.charset.Charset

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        val dc = DeliverCallback { consumerTag: String?, delivery: Delivery ->
            val message = String(delivery.body, Charset.forName("UTF-8"))
            try {
                val event = Gson().fromJson(message, Event::class.java)
                if (event.type == TYPES.PROCESS_FILE.type) {
                    AudioManager.getFile(event.data, true)
                }
            } catch (exception: JsonSyntaxException){
                println("ERROR: $message")
            }
        }
        try{
            EventBus.createQueue(dc)
        } catch(e: Exception){
            println("Couldn't create queue")
        }
    }.start(wait = true)
}
