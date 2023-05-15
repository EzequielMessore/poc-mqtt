package br.com.messore.tech.mqtt.data.repository

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
class MessagingRepositoryImpl @Inject constructor(
    private val source: MessagingSource
) : MessagingRepository {

    override fun listen(topic: String): Flow<String> {
        return source.listen(topic)
    }

    override fun send(topic: String, message: String) {
        source.publish(topic, message)
    }

    override fun disconnect() {
        source.disconnect()
    }

    override fun unsubscribe(topic: String) {
        source.unsubscribe(topic)
    }
}
