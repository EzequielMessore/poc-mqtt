package br.com.messore.tech.mqtt.ui.features

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import br.com.messore.tech.mqtt.ui.theme.MQTTTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MQTTTheme {
                val state by viewModel.state.collectAsState()

                MainScreen(
                    messages = state.messages,
                    onPublish = viewModel::publish,
                    onSubscribe = viewModel::subscribe,
                    onUnsubscribe = viewModel::unsubscribe
                )
            }
        }
    }
}
