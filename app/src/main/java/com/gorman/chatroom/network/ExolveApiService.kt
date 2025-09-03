package com.gorman.chatroom.network

import com.gorman.chatroom.data.networkData.SmsRequest
import com.gorman.chatroom.data.networkData.SmsResponse
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