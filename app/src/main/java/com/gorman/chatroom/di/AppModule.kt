package com.gorman.chatroom.di

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.gorman.chatroom.data.datasource.remote.FirebaseDBImpl
import com.gorman.chatroom.data.datasource.remote.FirebaseDB
import com.gorman.chatroom.data.repository.FirebaseRepositoryImpl
import com.gorman.chatroom.data.repository.SettingsRepositoryImpl
import com.gorman.chatroom.domain.repository.FirebaseRepository
import com.gorman.chatroom.domain.repository.SettingsRepository
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
        return SettingsRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideFirebaseDBImpl(databaseReference: DatabaseReference): FirebaseDB {
        return FirebaseDBImpl(databaseReference)
    }

    @Provides
    @Singleton
    fun provideFirebaseRepositoryImpl(firebaseDB: FirebaseDB, settingsRepository: SettingsRepository): FirebaseRepository {
        return FirebaseRepositoryImpl(firebaseDB = firebaseDB, settingsRepository = settingsRepository)
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun provideDatabaseReference(database: FirebaseDatabase): DatabaseReference {
        return database.getReference("ChatRoom")
    }
}