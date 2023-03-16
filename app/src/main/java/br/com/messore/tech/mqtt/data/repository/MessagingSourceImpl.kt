package br.com.messore.tech.mqtt.data.repository

import br.com.messore.tech.mqtt.core.log
import br.com.messore.tech.mqtt.data.source.AwsClient
import br.com.messore.tech.mqtt.domain.model.Message
import javax.inject.Inject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MessagingSourceImpl @Inject constructor(
    private val client: AwsClient
) : MessagingSource {

    private val messages = MutableSharedFlow<Message>()

    override fun listen(topic: String): Flow<String> {
        connectIfNeeded {
            client.subscribe(
                topic = topic,
//                callback = { topic, data ->
//                    val message = String(data, Charsets.UTF_8)
//                    log("message arrived -> $message")
//                    publishMessage(topic, message)
//                }
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
//        if (client.isConnected()) {
//            return onConnected()
//        }
        client.connect(onConnected)
    }

    private fun publishMessage(topic: String, message: String) {
        MainScope().launch {
            messages.emit(
                Message(topic, message)
            )
        }
    }
}
