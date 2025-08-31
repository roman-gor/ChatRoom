package com.gorman.chatroom.di

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.gorman.chatroom.data.FirebaseDB
import com.gorman.chatroom.repository.FirebaseRepository
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

    @Provides
    @Singleton
    fun provideFirebaseDB(databaseReference: DatabaseReference): FirebaseDB {
        return FirebaseDB(databaseReference)
    }

    @Provides
    @Singleton
    fun provideFirebaseRepository(firebaseDB: FirebaseDB,
                                  settingsRepository: SettingsRepository): FirebaseRepository {
        return FirebaseRepository(firebaseDB = firebaseDB, settingsRepository = settingsRepository)
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