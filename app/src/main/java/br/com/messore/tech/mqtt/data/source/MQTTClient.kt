package br.com.messore.tech.mqtt.data.source

import android.content.Context
import br.com.messore.tech.mqtt.core.log
import dagger.hilt.android.qualifiers.ApplicationContext
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import javax.inject.Inject

class MQTTClient @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val clientId = MqttClient.generateClientId()
    private val serverURI = "tcp://broker.hivemq.com:1883"
    private val mqttClient = MqttAndroidClient(context, serverURI, clientId)

    fun connect(username: String = "", password: String = "", onClientCallback: MqttCallback, onConnect: IMqttActionListener) {
        mqttClient.setCallback(onClientCallback)

        val options = MqttConnectOptions().apply {
            this.userName = username
            this.password = password.toCharArray()
            this.isCleanSession = false
        }

        /**  Only use when you need certificates **/
        //  val (ca, cert, key) = SslUtil.getCertificates(context)
        //  options.socketFactory = SslUtil.getSocketFactory(ca, cert, key, "")

        running {
            mqttClient.connect(options, null, onConnect)
        }
    }

    fun isConnected(): Boolean {
        return mqttClient.isConnected
    }

    fun subscribe(topic: String, qos: Int = 1, onSubscribe: IMqttActionListener) {
        running {
            mqttClient.subscribe(topic, qos, null, onSubscribe)
        }
    }

    fun unsubscribe(topic: String, onUnsubscribe: IMqttActionListener) {
        running {
            mqttClient.unsubscribe(topic, null, onUnsubscribe)
        }
    }

    fun publish(topic: String, msg: String, qos: Int = 1, retained: Boolean = false, onPublish: IMqttActionListener = defaultCbPublish) {
        running {
            val message = MqttMessage().apply {
                this.payload = msg.toByteArray()
                this.qos = qos
                this.isRetained = retained
            }
            mqttClient.publish(topic, message, null, onPublish)
        }
    }

    fun disconnect(onDisconnect: IMqttActionListener) {
        running {
            mqttClient.disconnect(null, onDisconnect)
        }
    }

    private fun <T, R> T.running(block: T.() -> R) {
        runCatching { block() }
        .onFailure { log(exception = it) }
    }
}