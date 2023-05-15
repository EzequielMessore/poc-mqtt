package br.com.messore.tech.mqtt.domain.usecase

import br.com.messore.tech.mqtt.data.repository.MessagingRepository
import javax.inject.Inject

class PublishOnTopicUseCase @Inject constructor(
    private val messagingRepository: MessagingRepository
) {
    operator fun invoke(topic: String, message: String) {
        messagingRepository.send(topic, message)
    }
}
