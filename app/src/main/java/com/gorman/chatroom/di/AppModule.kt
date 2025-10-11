package com.gorman.chatroom.di

import android.content.Context
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.gorman.chatroom.BuildConfig
import com.gorman.chatroom.data.datasource.FirebaseDBImpl
import com.gorman.chatroom.data.ExolveApiService
import com.gorman.chatroom.data.datasource.FirebaseDB
import com.gorman.chatroom.data.repositoryImpl.FirebaseRepositoryImpl
import com.gorman.chatroom.data.repositoryImpl.SettingsRepositoryImpl
import com.gorman.chatroom.data.repositoryImpl.SmsRepositoryImpl
import com.gorman.chatroom.domain.repository.FirebaseRepository
import com.gorman.chatroom.domain.repository.SettingsRepository
import com.gorman.chatroom.domain.repository.SmsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://api.exolve.ru/messaging/"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideApiKey(): String =
        BuildConfig.EXOLVE_API_KEY

    @Provides
    @Singleton
    fun provideAuthInterceptor(apiKey: String): Interceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $apiKey")
            .build()
        Log.d("EXOLVE", apiKey)
        chain.proceed(request)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: Interceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideRetrofitClient(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ExolveApiService =
        retrofit.create(ExolveApiService::class.java)

    @Provides
    @Singleton
    fun provideSmsRepository(api: ExolveApiService): SmsRepository {
        return SmsRepositoryImpl(api)
    }

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