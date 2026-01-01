package com.gorman.chatroom.di

import com.gorman.core.data.datasource.remote.FirebaseCallClient
import com.gorman.core.data.datasource.remote.FirebaseCallClientImpl
import com.gorman.core.data.datasource.remote.FirebaseDB
import com.gorman.core.data.datasource.remote.FirebaseDBImpl
import com.gorman.core.data.repository.FirebaseRepositoryImpl
import com.gorman.core.data.repository.SettingsRepositoryImpl
import com.gorman.core.domain.repository.FirebaseRepository
import com.gorman.core.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindFirebaseRepository(impl: FirebaseRepositoryImpl): FirebaseRepository
}
