package com.gorman.chatroom.domain.repository

import com.gorman.chatroom.data.ExolveApiService
import com.gorman.chatroom.domain.entities.networkData.SmsRequest
import com.gorman.chatroom.domain.entities.networkData.SmsResponse
import retrofit2.Response
import javax.inject.Inject

class SmsRepository @Inject constructor(
    private val api: ExolveApiService
) {
    suspend fun sendMessage(request: SmsRequest): Response<SmsResponse> {
        return api.sendSMS(request)
    }
}