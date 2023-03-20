package br.com.messore.tech.mqtt.domain.usecase

import br.com.messore.tech.mqtt.data.repository.MessagingRepository
import javax.inject.Inject

class UnsubscribeTopicUseCase @Inject constructor(
    private val repository: MessagingRepository
) {
    operator fun invoke() = repository.unsubscribe()
}
