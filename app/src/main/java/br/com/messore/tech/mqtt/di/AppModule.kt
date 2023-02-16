package br.com.messore.tech.mqtt.di

import br.com.messore.tech.mqtt.data.repository.MessagingRepository
import br.com.messore.tech.mqtt.data.repository.MessagingRepositoryImpl
import br.com.messore.tech.mqtt.data.repository.MessagingSource
import br.com.messore.tech.mqtt.data.repository.MessagingSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    @Binds
    fun providesMessagingSource(message: MessagingSourceImpl): MessagingSource

    @Binds
    fun providesMessagingRepository(repository: MessagingRepositoryImpl): MessagingRepository
}
