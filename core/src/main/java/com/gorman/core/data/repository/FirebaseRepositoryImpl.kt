package com.gorman.core.data.repository

import com.gorman.core.data.datasource.remote.FirebaseDB
import com.gorman.core.domain.models.ChatsData
import com.gorman.core.domain.models.GroupsData
import com.gorman.core.domain.models.MessagesData
import com.gorman.core.domain.models.UsersData
import com.gorman.core.domain.repository.FirebaseRepository
import com.gorman.core.domain.repository.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val firebaseDB: FirebaseDB,
    settingsRepository: SettingsRepository
) : FirebaseRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentUserData: Flow<UsersData?> = settingsRepository.userIdFlow
        .filterNotNull()
        .flatMapLatest { userId ->
            firebaseDB.getUserByIdFlow(userId)
        }

    override fun getUserChats(userId: String): Flow<List<ChatsData?>> {
        return firebaseDB.getUserChats(userId)
    }

    override fun getUserGroups(userId: String): Flow<List<GroupsData?>> {
        return firebaseDB.getUserGroups(userId)
    }

    override fun getMessages(conversationId: String): Flow<List<MessagesData>> {
        return firebaseDB.getMessages(conversationId = conversationId)
    }

    override fun getLastMessage(conversationId: String): Flow<MessagesData?> {
        return firebaseDB.getLastMessage(conversationId)
    }

    override fun getUnreadMessagesQuantity(conversationId: String, userId: String): Flow<Int> {
        return firebaseDB.getMessages(conversationId).map { messagesData ->
            messagesData.count { message ->
                message.status?.get(userId) == "unread"
            }
        }
    }

    override fun findUserByPhoneNumber(phoneNumber: String): Flow<UsersData?> {
        return firebaseDB.findUserByPhoneNumber(phoneNumber)
    }

    override fun createGroup(currentUserId: String, getterUsers: List<String?>, groupName: String): String?{
        return firebaseDB.createGroup(currentUserId, getterUsers, groupName)
    }

    override suspend fun updateUserData(userId: String, user: UsersData?) {
        firebaseDB.updateUserData(userId, user)
    }

    override suspend fun sendMessage(chatId: String, currentUserId: String, getterId: String, text: String) {
        firebaseDB.sendMessage(chatId, currentUserId, getterId, text)
    }

    override suspend fun sendGroupMessages(groupId: String, currentUserId: String, getterUsers: List<UsersData?>, text: String){
        firebaseDB.sendGroupMessage(groupId, currentUserId, getterUsers, text)
    }

    override suspend fun markMessageAsRead(chatId: String, currentUserId: String) {
        firebaseDB.markMessageAsRead(chatId, currentUserId)
    }

    override suspend fun findUserByChatId(chatId: String, currentUserId: String): UsersData? {
        return firebaseDB.findUserByChatId(chatId, currentUserId)
    }

    override suspend fun findUserByGroupId(groupId: String, currentUserId: String): List<UsersData?> {
        return firebaseDB.findUsersByGroupId(groupId, currentUserId)
    }

    override suspend fun setupNewConversation(currentUserId: String, getterUserId: String): String? {
        val existingChatId = firebaseDB.checkChatForExistence(currentUserId,getterUserId)
        return existingChatId ?: firebaseDB.createChat(currentUserId, getterUserId)
    }

    override suspend fun deleteChat(chatId: String) {
        firebaseDB.deleteChat(chatId)
    }

    override suspend fun loadNewUser(user: UsersData?): Boolean {
        return firebaseDB.loadNewUser(user)
    }
}
