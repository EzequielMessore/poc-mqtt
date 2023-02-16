package br.com.messore.tech.mqtt.data.source

import br.com.messore.tech.mqtt.core.log
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken

val defaultCbPublish = object : IMqttActionListener {
    override fun onSuccess(asyncActionToken: IMqttToken?) {
        log("Message published to topic")
    }

    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
        log("Failed to publish message to topic", exception)
    }
}
