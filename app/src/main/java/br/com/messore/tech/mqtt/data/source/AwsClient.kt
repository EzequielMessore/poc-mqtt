package br.com.messore.tech.mqtt.data.source

import android.content.Context
import br.com.messore.tech.mqtt.BuildConfig
import br.com.messore.tech.mqtt.core.log
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager
import com.amazonaws.mobileconnectors.iot.AWSIotMqttMessageDeliveryCallback
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

typealias MessageCallback = (topic: String, data: ByteArray) -> Unit

@Singleton
class AwsClient @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var status = AWSIotMqttClientStatus.Connecting
    private val client by lazy {
        AWSIotMqttManager(
            clientId,
            BuildConfig.endpoint
        )
    }

    fun connect(onConnected: () -> Unit) {
        val keyStore = getKeyStore()

        client.connect(keyStore) { status, _ ->
            log("connection -> $status")
            this.status = status
            if (isConnected()) onConnected()
        }
    }

    private fun getKeyStore(): KeyStore? {
        if (!AWSIotKeystoreHelper.isKeystorePresent(context.filesDir.path, KEYSTORE_NAME)) {
            AWSIotKeystoreHelper.saveCertificateAndPrivateKey(
                CERTIFICATE_ID, BuildConfig.certPem, BuildConfig.keyPem, context.filesDir.path, KEYSTORE_NAME, KEYSTORE_PASSWORD
            )
        }
        return AWSIotKeystoreHelper.getIotKeystore(
            CERTIFICATE_ID, context.filesDir.path, KEYSTORE_NAME, KEYSTORE_PASSWORD
        )
    }

    fun isConnected() = status == AWSIotMqttClientStatus.Connected

    fun subscribe(topic: String, callback: MessageCallback) {
        client.subscribeToTopic(
            topic,
            AWSIotMqttQos.QOS0,
             callback
        )
    }

    fun publish(topic: String, message: String) {
        client.publishString(message, topic, AWSIotMqttQos.QOS0, object : AWSIotMqttMessageDeliveryCallback {
            override fun statusChanged(status: AWSIotMqttMessageDeliveryCallback.MessageDeliveryStatus?, userData: Any?) {
                log("AWS publish" + status.toString())
            }
        }, null)
    }

    companion object {
        private val clientId = UUID.randomUUID().toString()
        private const val CERTIFICATE_ID = "MqttPoC"

        private const val KEYSTORE_NAME = "keystore"
        private const val KEYSTORE_PASSWORD = "password"
    }
}