package br.com.messore.tech.mqtt.data.repository

import br.com.messore.tech.mqtt.data.source.HiveMqClient
import br.com.messore.tech.mqtt.domain.model.Message
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAckReturnCode
import javax.inject.Inject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MessagingSourceImpl @Inject constructor(
    private val client: HiveMqClient
) : MessagingSource {

    private val messages = MutableSharedFlow<Message>()

    override fun listen(topic: String): Flow<String> {
        connectIfNeeded {
            client.subscribe(
                topic = topic,
                receiveMessage = { message ->
                    publishMessage(topic, message)
                }
            )
        }

        return messages
            .filter { message -> message.topic == topic }
            .map { it.content }
    }

    override fun publish(topic: String, message: String) {
        connectIfNeeded {
            client.publish(topic, message)
        }
    }

    private fun connectIfNeeded(onConnected: () -> Unit) {
        if (client.isConnected()) {
            return onConnected()
        }
        client.connect()
            .thenAccept { conn ->
                if (conn.returnCode == Mqtt3ConnAckReturnCode.SUCCESS) {
                    onConnected()
                }
            }
    }

    private fun publishMessage(topic: String, message: String) {
        MainScope().launch {
            messages.emit(
                Message(topic, message)
            )
        }
    }
}
