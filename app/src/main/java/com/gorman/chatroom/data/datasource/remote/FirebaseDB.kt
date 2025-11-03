package com.gorman.chatroom.data.datasource.remote

import com.gorman.chatroom.domain.models.ChatsData
import com.gorman.chatroom.domain.models.GroupsData
import com.gorman.chatroom.domain.models.MessagesData
import com.gorman.chatroom.domain.models.UsersData
import kotlinx.coroutines.flow.Flow

interface FirebaseDB {
    fun getUserChats(userId: String): Flow<List<ChatsData?>>
    fun getUserByIdFlow(userId: String): Flow<UsersData?>
    fun getMessages(conversationId: String): Flow<List<MessagesData>>
    fun getLastMessage(conversationId: String): Flow<MessagesData?>
    fun findUserByPhoneNumber(phoneNumber: String): Flow<UsersData?>
    fun createChat(currentUserId: String, getterUserId: String): String?
    fun createGroup(currentUserId: String, getterUsers: List<String?>, groupName: String): String?
    fun getUserGroups(userId: String): Flow<List<GroupsData?>>
    suspend fun findUserByChatId(chatId: String, currentUserId: String): UsersData
    suspend fun findUsersByGroupId(groupId: String, currentUserId: String): List<UsersData?>
    suspend fun updateUserData(userId: String, user: UsersData?)
    suspend fun sendMessage(chatId: String, currentUserId: String, getterId: String, text: String)
    suspend fun sendGroupMessage(groupId: String, currentUserId: String, getterUsers: List<UsersData?>, text: String)
    suspend fun checkChatForExistence(currentUserId: String, getterUserId: String): String?suspend fun markMessageAsRead(chatId: String, currentUserId: String)
    suspend fun deleteChat(chatId: String)
    suspend fun loadNewUser(user: UsersData?): Boolean
}