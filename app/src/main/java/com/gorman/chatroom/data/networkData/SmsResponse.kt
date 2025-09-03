package com.gorman.chatroom.data.networkData

import com.google.gson.annotations.SerializedName

data class SmsResponse(
    @SerializedName("messageId")
    val messageId: String,
    @SerializedName("templateResourceId")
    val templateResourceId: ULong
)
