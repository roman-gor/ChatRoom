package com.gorman.chatroom.data.repositoryImpl

import com.gorman.chatroom.data.ExolveApiService
import com.gorman.chatroom.domain.entities.networkData.SmsRequest
import com.gorman.chatroom.domain.entities.networkData.SmsResponse
import com.gorman.chatroom.domain.repository.SmsRepository
import retrofit2.Response
import javax.inject.Inject

class SmsRepositoryImpl @Inject constructor(
    private val api: ExolveApiService
) : SmsRepository{
    override suspend fun sendMessage(request: SmsRequest): Response<SmsResponse> {
        return api.sendSMS(request)
    }
}