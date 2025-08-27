package com.gorman.chatroom.di

import android.content.Context
import com.gorman.chatroom.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSettingRepository(@ApplicationContext context: Context): SettingsRepository {
        return SettingsRepository(context)
    }
}