package br.com.messore.tech.mqtt.domain.usecase

import br.com.messore.tech.mqtt.data.repository.MessagingRepository
import javax.inject.Inject

class PublishOnTopicUseCase @Inject constructor(
    private val messagingRepository: MessagingRepository
) {
    operator fun invoke(message: String) {
        messagingRepository.send(message)
    }
}
