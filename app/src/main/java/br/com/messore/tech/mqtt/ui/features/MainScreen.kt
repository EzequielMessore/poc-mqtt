package br.com.messore.tech.mqtt.ui.features

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

const val TAG = "MQTT:"

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainScreen(
    onSubscribe: (String) -> Unit = {},
    onUnsubscribe: () -> Unit = {},
    messages: List<String> = emptyList(),
    onPublish: (topic: String, message: String) -> Unit = { _, _ -> },
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("MQTT Sample") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {

            SendScope(onPublish)

            Spacer(modifier = Modifier.height(40.dp))

            SubscribeScope(onSubscribe, onUnsubscribe)

            Spacer(modifier = Modifier.height(40.dp))

            Messages(messages)
        }
    }
}

@Composable
fun ColumnScope.SendScope(onPublish: (String, String) -> Unit) {
    val message = remember { mutableStateOf("") }
    val topic = remember { mutableStateOf("") }

    Row {
        TextField(modifier = Modifier.fillMaxWidth(0.5f), label = "Message", state = message)
        TextField(modifier = Modifier.fillMaxWidth(0.5f), label = "Topic", state = topic)
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = { onPublish(topic.value, message.value) },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .align(Alignment.CenterHorizontally)
    ) {
        Text(text = "Publish")
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TextField(
    state: MutableState<String>,
    modifier: Modifier = Modifier,
    label: String, default: String = "",
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(0.9f),
        value = state.value.ifEmpty { default },
        onValueChange = { state.value = it },
        label = { Text(text = label) },
    )
}

@Composable
fun ColumnScope.SubscribeScope(onSubscribe: (String) -> Unit, onUnsubscribe: () -> Unit) {
    val topic = remember { mutableStateOf("") }

    TextField(label = "Topic", state = topic)

    Spacer(modifier = Modifier.width(8.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .align(Alignment.CenterHorizontally)
    ) {
        Button(
            onClick = { onSubscribe(topic.value) },
            modifier = Modifier.fillMaxWidth(.5f)
        ) {
            Text(text = "Subscribe")
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = { onUnsubscribe() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Unsubscribe")
        }
    }

}

@Composable
private fun Messages(messages: List<String>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(8.dp))
    ) {
        items(messages) { message ->
            Text(
                text = message,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(messages = listOf("First Message", "Second Message"))
}