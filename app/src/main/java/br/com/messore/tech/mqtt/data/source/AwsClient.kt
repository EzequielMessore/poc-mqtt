package br.com.messore.tech.mqtt.data.source

import android.content.Context
import br.com.messore.tech.mqtt.core.log
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager
import com.amazonaws.mobileconnectors.iot.AWSIotMqttMessageDeliveryCallback
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.amazonaws.mobileconnectors.iot.AWSIotMqttSubscriptionStatusCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

typealias MessageCallback = (topic: String, data: ByteArray) -> Unit

class AwsClient @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val manager by lazy {
        AWSIotMqttManager(
            clientId,
            "INSERT ENDPOINT HERE"
        )
    }

    fun connect(onConnected: () -> Unit) {
        if (!AWSIotKeystoreHelper.isKeystorePresent(context.filesDir.path, KEYSTORE_NAME)) {
            AWSIotKeystoreHelper.saveCertificateAndPrivateKey(
                CERTIFICATE_ID, pem, key, context.filesDir.path,
                KEYSTORE_NAME, KEYSTORE_PASSWORD
            )
        }
        val iotKeystore = AWSIotKeystoreHelper.getIotKeystore(
            CERTIFICATE_ID,
            context.filesDir.path,
            KEYSTORE_NAME,
            KEYSTORE_PASSWORD
        )

        manager.connect(iotKeystore) { status, _ ->
            log("AWS -> $status")
            if (status == AWSIotMqttClientStatus.Connected) {
                onConnected()
            }
        }
    }

    fun subscribe(topic: String) {
        manager.subscribeToTopic(
            topic,
            AWSIotMqttQos.QOS0,
            object : AWSIotMqttSubscriptionStatusCallback {
                override fun onSuccess() {
                    log("successful subscribed")
                }

                override fun onFailure(exception: Throwable?) {
                    if (exception != null) log(exception = exception)
                }
            }) { _: String, data: ByteArray ->
        }
    }

    fun publish(topic: String, message: String) {
        manager.publishString(message, topic, AWSIotMqttQos.QOS0, object : AWSIotMqttMessageDeliveryCallback {
            override fun statusChanged(status: AWSIotMqttMessageDeliveryCallback.MessageDeliveryStatus?, userData: Any?) {
                log(status.toString())
            }
        }, null)
    }

    private val pem = """
        INSERT CERTIFICATE .PEM HERE
    """.trimIndent()

    private val key = """
        INSERT CERTIFICATE .KEY HERE
    """.trimIndent()

    companion object {
        private const val clientId = "MqttPoC"
        private const val CERTIFICATE_ID = "MqttPoC"

        private const val KEYSTORE_NAME = "keystore"
        private const val KEYSTORE_PASSWORD = "password"
    }
}