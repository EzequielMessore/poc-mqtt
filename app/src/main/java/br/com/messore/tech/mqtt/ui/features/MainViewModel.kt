package br.com.messore.tech.mqtt.ui.features

import androidx.lifecycle.viewModelScope
import br.com.messore.tech.mqtt.core.BaseViewModel
import br.com.messore.tech.mqtt.domain.usecase.ListenMessagesUseCase
import br.com.messore.tech.mqtt.domain.usecase.PublishOnTopicUseCase
import br.com.messore.tech.mqtt.domain.usecase.UnsubscribeTopicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val listenMessagesUseCase: ListenMessagesUseCase,
    private val publishOnTopicUseCase: PublishOnTopicUseCase,
    private val unsubscribeTopicUseCase: UnsubscribeTopicUseCase,
) : BaseViewModel<MainUiState, MainActionState>(MainUiState()) {

    fun publish(topic: String, message: String) {
        publishOnTopicUseCase(topic, message)
    }

    fun subscribe(topic: String) = viewModelScope.launch {
        listenMessagesUseCase(topic)
            .collect {
                setState { copy(messages = messages + it) }
            }
    }

    fun unsubscribe() = viewModelScope.launch {
        unsubscribeTopicUseCase()
    }
}
