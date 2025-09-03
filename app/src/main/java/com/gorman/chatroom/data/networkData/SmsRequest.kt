package com.gorman.chatroom.data.networkData

import com.google.gson.annotations.SerializedName

data class SmsRequest(
    @SerializedName("number")
    val number: String,
    @SerializedName("destination")
    val destination: String,
    @SerializedName("text")
    val text: String
)
