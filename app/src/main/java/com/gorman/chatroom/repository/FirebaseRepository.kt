package com.gorman.chatroom.repository

import com.gorman.chatroom.data.ChatsData
import com.gorman.chatroom.data.FirebaseDB
import com.gorman.chatroom.data.MessagesData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firebaseDB: FirebaseDB
) {
    fun getUserChats(userId: String): Flow<List<ChatsData?>> {
        return firebaseDB.getUserChats(userId)
    }

    fun getMessages(chatId: String): Flow<List<MessagesData>> {
        return firebaseDB.getMessages(chatId = chatId)
    }

    suspend fun sendMessage(chatId: String,
                            currentUserId: String,
                            getterId: String,
                            text: String) {
        firebaseDB.sendMessage(chatId, currentUserId, getterId, text)
    }

    suspend fun markMessageAsRead(chatId: String, currentUserId: String) {
        firebaseDB.markMessageAsRead(chatId, currentUserId)
    }
}