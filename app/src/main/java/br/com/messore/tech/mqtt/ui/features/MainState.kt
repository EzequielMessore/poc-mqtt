package br.com.messore.tech.mqtt.ui.features

data class MainUiState(
    val messages: List<String> = emptyList(),
)

sealed class MainActionState
