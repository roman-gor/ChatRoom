package com.gorman.chatroom.data

import com.gorman.chatroom.domain.models.networkData.SmsRequest
import com.gorman.chatroom.domain.models.networkData.SmsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ExolveApiService {
    @Headers("Content-Type: application/json")
    @POST("v1/SendSMS")
    suspend fun sendSMS(
        @Body request: SmsRequest
    ): Response<SmsResponse>
}