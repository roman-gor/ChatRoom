package com.gorman.chatroom.repository

import com.gorman.chatroom.data.ChatsData
import com.gorman.chatroom.data.FirebaseDB
import com.gorman.chatroom.data.MessagesData
import com.gorman.chatroom.data.UsersData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firebaseDB: FirebaseDB,
    settingsRepository: SettingsRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUserData: Flow<UsersData?> = settingsRepository.userIdFlow
        .filterNotNull()
        .flatMapLatest { userId ->
            firebaseDB.getUserByIdFlow(userId)
        }

    fun getUserChats(userId: String): Flow<List<ChatsData?>> {
        return firebaseDB.getUserChats(userId)
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

    suspend fun setupNewConversation(currentUserId: String, getterUserId: String): String? {
        val existingChatId = firebaseDB.checkChatForExistence(currentUserId,getterUserId)
        return existingChatId ?: firebaseDB.createChat(currentUserId, getterUserId)
    }
}