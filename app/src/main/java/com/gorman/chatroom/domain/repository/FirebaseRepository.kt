package com.gorman.chatroom.domain.repository

import com.gorman.chatroom.domain.models.ChatsData
import com.gorman.chatroom.domain.models.GroupsData
import com.gorman.chatroom.domain.models.MessagesData
import com.gorman.chatroom.domain.models.UsersData
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {
    val currentUserData: Flow<UsersData?>
    fun getUserChats(userId: String): Flow<List<ChatsData?>>
    fun getUserGroups(userId: String): Flow<List<GroupsData?>>
    fun getMessages(conversationId: String): Flow<List<MessagesData>>
    fun getLastMessage(conversationId: String): Flow<MessagesData?>
    fun getUnreadMessagesQuantity(conversationId: String, userId: String): Flow<Int>
    fun findUserByPhoneNumber(phoneNumber: String): Flow<UsersData?>
    fun createGroup(currentUserId: String, getterUsers: List<String?>, groupName: String): String?
    suspend fun updateUserData(userId: String, user: UsersData?)
    suspend fun sendMessage(chatId: String, currentUserId: String, getterId: String, text: String)
    suspend fun sendGroupMessages(groupId: String, currentUserId: String, getterUsers: List<UsersData?>, text: String)
    suspend fun markMessageAsRead(chatId: String, currentUserId: String)
    suspend fun findUserByChatId(chatId: String, currentUserId: String): UsersData?
    suspend fun findUserByGroupId(groupId: String, currentUserId: String): List<UsersData?>
    suspend fun setupNewConversation(currentUserId: String, getterUserId: String): String?
    suspend fun deleteChat(chatId: String)
    suspend fun loadNewUser(user: UsersData?): Boolean
}