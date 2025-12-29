package com.gorman.chatroom.data.datasource.remote

enum class FirebaseConstants(val value: String) {
    DATABASE_PATH("ChatRoom"),
    SIGNALING_PATH("WebRTCSignaling"),
    LATEST_EVENT("latest_event"),
    CANDIDATES("candidates"),
    USERS("users"),
    CHATS("chats"),
    MEMBERS("members"),
    GROUPS("groups"),
    MESSAGES("messages"),
    TIMESTAMP("timestamp"),
    PHONE("phone")
}