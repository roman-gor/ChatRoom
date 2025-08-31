package com.gorman.chatroom.repository

import com.gorman.chatroom.data.ChatsData
import com.gorman.chatroom.data.FirebaseDB
import com.gorman.chatroom.data.MessagesData
import com.gorman.chatroom.data.UsersData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firebaseDB: FirebaseDB
) {
    fun getUserChats(userId: String): Flow<List<ChatsData?>> {
        return firebaseDB.getUserChats(userId)
    }

    suspend fun getUserById(userId: String): UsersData {
        return firebaseDB.getUserById(userId)
    }

    fun getMessages(chatId: String): Flow<List<MessagesData>> {
        return firebaseDB.getMessages(chatId = chatId)
    }

    fun getUnreadMessagesQuantity(chatId: String, userId: String): Flow<Int>{
        return firebaseDB.getMessages(chatId).map { messagesData ->
            messagesData.count { message ->
                message.status?.get(userId) == "unread"
            }
        }
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

    suspend fun findUserByChatId(chatId: String, currentUserId: String): UsersData? {
        return firebaseDB.findUserByChatId(chatId, currentUserId)
    }

    fun findUserByPhoneNumber(phoneNumber: String): Flow<UsersData?> {
        return firebaseDB.findUserByPhoneNumber(phoneNumber)
    }
}