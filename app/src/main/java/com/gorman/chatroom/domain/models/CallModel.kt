package com.gorman.chatroom.domain.models

enum class CallModelType {
    StartAudioCall,
    StartVideoCall,
    Offer,
    Answer,
    IceCandidates,
    EndCall
}
data class CallModel(
    val sender: String ?= null,
    val target: String,
    val type: CallModelType,
    val data: String ?= null,
    val timeStamp:Long = System.currentTimeMillis()
)


fun CallModel.isValid(): Boolean {
    return System.currentTimeMillis() - this.timeStamp < 60000
}
