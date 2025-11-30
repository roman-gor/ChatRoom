package com.gorman.chatroom.di

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.gorman.chatroom.BuildConfig
import com.gorman.chatroom.data.datasource.remote.FirebaseCallClient
import com.gorman.chatroom.data.datasource.remote.FirebaseCallClientImpl
import com.gorman.chatroom.data.datasource.remote.FirebaseDBImpl
import com.gorman.chatroom.data.datasource.remote.FirebaseDB
import com.gorman.chatroom.data.repository.CallRepositoryImpl
import com.gorman.chatroom.data.repository.FirebaseRepositoryImpl
import com.gorman.chatroom.data.repository.SettingsRepositoryImpl
import com.gorman.chatroom.domain.repository.CallRepository
import com.gorman.chatroom.domain.repository.FirebaseRepository
import com.gorman.chatroom.domain.repository.SettingsRepository
import com.gorman.chatroom.service.CallServiceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContext(@ApplicationContext context:Context) : Context = context.applicationContext

    @Provides
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideSettingRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideCallRepository(firebaseClient: FirebaseCallClient,
                              webRtcClient: WebRtcClient,
                              gson: Gson): CallRepository =
        CallRepositoryImpl(firebaseClient, webRtcClient, gson)

    @Provides
    @Singleton
    fun provideCallServiceRepository(context: Context): CallServiceRepository =
        CallServiceRepository(context)

    @Provides
    @Singleton
    fun provideFirebaseDBImpl(databaseReference: DatabaseReference): FirebaseDB {
        return FirebaseDBImpl(databaseReference)
    }

    @Provides
    @Singleton
    fun provideFirebaseCallClient(databaseReference: DatabaseReference, gson: Gson): FirebaseCallClient =
        FirebaseCallClientImpl(databaseReference, gson)

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