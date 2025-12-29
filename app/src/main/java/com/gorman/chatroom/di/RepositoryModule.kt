package com.gorman.chatroom.di

import com.gorman.chatroom.data.datasource.remote.FirebaseCallClient
import com.gorman.chatroom.data.datasource.remote.FirebaseCallClientImpl
import com.gorman.chatroom.data.datasource.remote.FirebaseDB
import com.gorman.chatroom.data.datasource.remote.FirebaseDBImpl
import com.gorman.chatroom.data.repository.CallRepositoryImpl
import com.gorman.chatroom.data.repository.FirebaseRepositoryImpl
import com.gorman.chatroom.data.repository.SettingsRepositoryImpl
import com.gorman.chatroom.domain.repository.CallRepository
import com.gorman.chatroom.domain.repository.FirebaseRepository
import com.gorman.chatroom.domain.repository.SettingsRepository
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
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindCallRepository(impl: CallRepositoryImpl): CallRepository

    @Binds
    @Singleton
    abstract fun bindFirebaseDB(impl: FirebaseDBImpl): FirebaseDB

    @Binds
    @Singleton
    abstract fun bindFirebaseRepository(impl: FirebaseRepositoryImpl): FirebaseRepository

    @Binds
    @Singleton
    abstract fun bindFirebaseCallClient(impl: FirebaseCallClientImpl): FirebaseCallClient

}