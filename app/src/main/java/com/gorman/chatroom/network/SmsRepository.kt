package com.gorman.chatroom.network

import com.gorman.chatroom.data.networkData.SmsRequest
import com.gorman.chatroom.data.networkData.SmsResponse
import retrofit2.Response
import javax.inject.Inject

class SmsRepository @Inject constructor(
    private val api: ExolveApiService
) {
    suspend fun sendMessage(request: SmsRequest): Response<SmsResponse> {
        return api.sendSMS(request)
    }
}