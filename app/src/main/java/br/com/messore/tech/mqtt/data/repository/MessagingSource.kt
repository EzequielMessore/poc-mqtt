package br.com.messore.tech.mqtt.data.repository

import kotlinx.coroutines.flow.Flow

interface MessagingSource {
    fun listen(topic: String): Flow<String>
    fun publish(topic: String, message: String)
}
