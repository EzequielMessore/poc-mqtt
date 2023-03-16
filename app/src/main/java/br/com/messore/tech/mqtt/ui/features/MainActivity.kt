package br.com.messore.tech.mqtt.ui.features

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import br.com.messore.tech.mqtt.core.log
import br.com.messore.tech.mqtt.ui.theme.MQTTTheme
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager
import com.amazonaws.mobileconnectors.iot.AWSIotMqttMessageDeliveryCallback
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.amazonaws.mobileconnectors.iot.AWSIotMqttSubscriptionStatusCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    private val topic = "sdk/test/java"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MQTTTheme {
                val state by viewModel.state.collectAsState()

                MainScreen(
                    messages = state.messages,
                    onPublish = {
                        viewModel.publish(it)
//                        publish(it)
                    },
                    onSubscribe = {
                        viewModel.subscribe()
//                        subscribe()
                    },
                )
            }
        }
        //   connect()
    }

    private val manager by lazy {
        AWSIotMqttManager(
            clientId,
            "ENDPOINT HERE"
        )
    }

    private fun connect() {
        if (!AWSIotKeystoreHelper.isKeystorePresent(filesDir.path, KEYSTORE_NAME)) {
            AWSIotKeystoreHelper.saveCertificateAndPrivateKey(
                CERTIFICATE_ID, pem, key, filesDir.path,
                KEYSTORE_NAME, KEYSTORE_PASSWORD
            )
        }
        val iotKeystore = AWSIotKeystoreHelper.getIotKeystore(
            CERTIFICATE_ID,
            filesDir.path,
            KEYSTORE_NAME,
            KEYSTORE_PASSWORD
        )

        manager.connect(iotKeystore) { status, throwable ->
            log("test: $status", throwable)
            //subscribe()
        }
    }

    private fun subscribe() {
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
            viewModel.setState {
                copy(messages = messages + String(data))
            }
        }
    }

    fun publish(message: String) {
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
