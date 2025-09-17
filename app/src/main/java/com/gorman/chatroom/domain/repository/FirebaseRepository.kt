package com.gorman.chatroom.domain.repository

import com.gorman.chatroom.domain.entities.ChatsData
import com.gorman.chatroom.data.FirebaseDB
import com.gorman.chatroom.domain.entities.GroupsData
import com.gorman.chatroom.domain.entities.MessagesData
import com.gorman.chatroom.domain.entities.UsersData
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

    fun getUserGroups(userId: String): Flow<List<GroupsData?>> {
        return firebaseDB.getUserGroups(userId)
    }

    fun getMessages(conversationId: String): Flow<List<MessagesData>> {
        return firebaseDB.getMessages(conversationId = conversationId)
    }

    fun getLastMessage(conversationId: String): Flow<MessagesData?> {
        return firebaseDB.getLastMessage(conversationId)
    }

    fun getUnreadMessagesQuantity(conversationId: String, userId: String): Flow<Int> {
        return firebaseDB.getMessages(conversationId).map { messagesData ->
            messagesData.count { message ->
                message.status?.get(userId) == "unread"
            }
        }
    }

    suspend fun updateUserData(userId: String, user: UsersData?) {
        firebaseDB.updateUserData(userId, user)
    }

    suspend fun sendMessage(chatId: String, currentUserId: String, getterId: String, text: String) {
        firebaseDB.sendMessage(chatId, currentUserId, getterId, text)
    }

    suspend fun sendGroupMessages(groupId: String, currentUserId: String, getterUsers: List<UsersData?>, text: String){
        firebaseDB.sendGroupMessage(groupId, currentUserId, getterUsers, text)
    }

    suspend fun markMessageAsRead(chatId: String, currentUserId: String) {
        firebaseDB.markMessageAsRead(chatId, currentUserId)
    }

    suspend fun findUserByChatId(chatId: String, currentUserId: String): UsersData? {
        return firebaseDB.findUserByChatId(chatId, currentUserId)
    }

    suspend fun findUserByGroupId(groupId: String, currentUserId: String): List<UsersData?> {
        return firebaseDB.findUsersByGroupId(groupId, currentUserId)
    }

    fun findUserByPhoneNumber(phoneNumber: String): Flow<UsersData?> {
        return firebaseDB.findUserByPhoneNumber(phoneNumber)
    }

    suspend fun setupNewConversation(currentUserId: String, getterUserId: String): String? {
        val existingChatId = firebaseDB.checkChatForExistence(currentUserId,getterUserId)
        return existingChatId ?: firebaseDB.createChat(currentUserId, getterUserId)
    }

    fun createGroup(currentUserId: String, getterUsers: List<String?>, groupName: String): String?{
        return firebaseDB.createGroup(currentUserId, getterUsers, groupName)
    }

    suspend fun deleteChat(chatId: String) {
        firebaseDB.deleteChat(chatId)
    }

    suspend fun loadNewUser(user: UsersData?): Boolean {
        return firebaseDB.loadNewUser(user)
    }
}