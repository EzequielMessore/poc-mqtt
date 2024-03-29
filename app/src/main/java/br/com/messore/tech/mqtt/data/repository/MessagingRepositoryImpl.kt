package br.com.messore.tech.mqtt.data.repository

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
class MessagingRepositoryImpl @Inject constructor(
    private val source: MessagingSource
) : MessagingRepository {

    private val topic = "sdk/test/java"

    override fun listen(): Flow<String> {
        return source.listen(topic)
    }

    override fun send(message: String) {
        source.publish(topic, message)
    }
}
