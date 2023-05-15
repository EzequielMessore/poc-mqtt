package br.com.messore.tech.mqtt.data.repository

import kotlinx.coroutines.flow.Flow

interface MessagingRepository {
    fun send(topic: String, message: String)
    fun listen(topic: String): Flow<String>

    fun disconnect()
    fun unsubscribe(topic: String)
}
