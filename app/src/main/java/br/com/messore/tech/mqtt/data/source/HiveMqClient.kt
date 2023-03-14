package br.com.messore.tech.mqtt.data.source

import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient.Mqtt3SubscribeAndCallbackBuilder
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import java.util.UUID
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class HiveMqClient @Inject constructor() {
    private val client by lazy {
        MqttClient.builder()
            .identifier(UUID.randomUUID().toString())
            .serverHost("broker.hivemq.com")
            .serverPort(1883)
            .useMqttVersion3()
            .build()
            .toAsync()
    }

    fun connect(): CompletableFuture<Mqtt3ConnAck> {
        return client.connectWith()
            .cleanSession(false)
            .send()
    }

    fun isConnected(): Boolean {
        return client.state.isConnected
    }

    fun subscribe(topic: String, qos: MqttQos = MqttQos.AT_LEAST_ONCE, receiveMessage: (String) -> Unit) {
        client.subscribeWith()
            .addTopic(topic = topic, qos = qos)
            .callback {
                receiveMessage("${it.topic} -> ${String(it.payloadAsBytes)}")
            }
            .send()
    }

    private fun Mqtt3SubscribeAndCallbackBuilder.addTopic(
        topic: String,
        qos: MqttQos = MqttQos.AT_LEAST_ONCE,
    ): Mqtt3SubscribeAndCallbackBuilder.Complete {
        return addSubscription().topicFilter(topic).qos(qos).applySubscription()
    }

    fun publish(topic: String, message: String, qos: MqttQos = MqttQos.AT_LEAST_ONCE) {
        client.publishWith()
            .topic(topic)
            .qos(qos)
            .payload(message.toByteArray())
            .send()
    }
}