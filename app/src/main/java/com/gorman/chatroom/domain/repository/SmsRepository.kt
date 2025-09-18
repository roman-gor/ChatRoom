package com.gorman.chatroom.domain.repository

import com.gorman.chatroom.domain.entities.networkData.SmsRequest
import com.gorman.chatroom.domain.entities.networkData.SmsResponse
import retrofit2.Response

interface SmsRepository {
    suspend fun sendMessage(request: SmsRequest): Response<SmsResponse>
}