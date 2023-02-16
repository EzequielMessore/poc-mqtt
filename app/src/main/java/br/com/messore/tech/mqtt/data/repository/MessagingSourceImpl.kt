package br.com.messore.tech.mqtt.data.repository

import br.com.messore.tech.mqtt.core.log
import br.com.messore.tech.mqtt.data.source.MQTTClient
import br.com.messore.tech.mqtt.domain.model.Message
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import javax.inject.Inject

class MessagingSourceImpl @Inject constructor(
    private val client: MQTTClient
) : MessagingSource {

    private val messages = MutableSharedFlow<Message>()

    override fun listen(topic: String): Flow<String> {
        connectIfNeeded {
            client.subscribe(
                topic = topic,
                onSubscribe = object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {

                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        log("Subscribed to topic")
                    }
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
        client.connect(
            onConnect = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    log("Connected")
                    onConnected()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    log("onFailure:", exception)
                }

            },
            onClientCallback = object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {

                }

                override fun messageArrived(topic: String, message: MqttMessage) {
                    log("messageArrived: $message")
                    MainScope().launch {
                        messages.emit(
                            Message(topic, message.toString())
                        )
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {

                }
            }
        )
    }
}
