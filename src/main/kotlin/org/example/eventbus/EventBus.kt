package org.example.eventbus

import com.google.gson.Gson
import com.rabbitmq.client.*
import java.nio.charset.Charset

const val EXCHANGE_NAME = "asdf"

object EventBus {
    private val connectionFactory: ConnectionFactory = ConnectionFactory()
    private val connectionDelegate = lazy { connectionFactory.newConnection() }
    private val connection: Connection by connectionDelegate
    private val channel: Channel by lazy { createChannel() }


    init {
        connectionFactory.host = "localhost"
    }

    private fun createConnection(): Connection {
        return connectionFactory.newConnection()
    }

    private fun createChannel(conn: Connection = connection): Channel {
        return conn.createChannel()
    }

    private fun createExchange(channel: Channel = createChannel(), exchangeName: String = EXCHANGE_NAME){
        channel.exchangeDeclare(exchangeName, "fanout")
    }

    fun createQueue(deliverCallback: DeliverCallback? = null, cancelCallback: CancelCallback? = null, channel: Channel = createChannel(), exchangeName: String = EXCHANGE_NAME, routingKey: String = ""){
        channel.queueDeclare().queue.let {
            createExchange()
            channel.queueBind(it, exchangeName, routingKey)
            val dc = deliverCallback ?: DeliverCallback { consumerTag: String?, delivery: Delivery ->
                val message = String(delivery.body, Charset.forName("UTF-8"))
                println(" [x] Received '$message'")
            }

            val cc = cancelCallback ?: CancelCallback { _ -> {} }

            channel.basicConsume(it, true, dc, cc)
        }
    }

    fun sendEvent(message: String, exchangeName: String = EXCHANGE_NAME, conn2: Connection? = null) {
        createExchange(channel, exchangeName)
        channel.basicPublish(exchangeName, "", null, message.toByteArray())
    }

    fun sendEvent(event: Event, exchangeName: String = EXCHANGE_NAME, conn2: Connection? = null) {
        sendEvent(Gson().toJson(event).toString())
    }
}