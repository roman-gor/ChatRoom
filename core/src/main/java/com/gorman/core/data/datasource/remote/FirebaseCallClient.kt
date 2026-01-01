package com.gorman.core.data.datasource.remote

import com.gorman.core.domain.models.CallModel

interface FirebaseCallClient {
    fun setClientId(id: String)
    fun subscribeForLatestEvent(listener: Listener)
    suspend fun sendMessageToOtherClient(message: CallModel): Boolean
    interface Listener {
        fun onLatestEventReceived(event: CallModel)
    }
}
