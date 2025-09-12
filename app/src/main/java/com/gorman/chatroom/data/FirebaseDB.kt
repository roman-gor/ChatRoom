package com.gorman.chatroom.data

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class FirebaseDB @Inject constructor(
    private val database: DatabaseReference
){

    fun getUserChats(userId: String): Flow<List<ChatsData?>> = callbackFlow {
        val userIdRef = database.child("users").child(userId).child("chats")

        val valueEventListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatIds = snapshot.children.mapNotNull { it.key }
                Log.d("Firebase", "Чаты пользователя $userId: $chatIds")
                launch {
                    val chatsList = chatIds.map { chatId ->
                        try {
                            val chatSnapshot = database.child("chats").child(chatId).get().await()
                            chatSnapshot.getValue(ChatsData::class.java)
                        } catch (e: Exception) {
                            Log.e("Firebase", "Ошибка при загрузке чата $chatId: ${e.message}")
                            null
                        }
                    }
                    Log.d("Firebase", "${chatsList.size}")
                    trySend(chatsList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Ошибка при извлечении списка чатов $error")
                close(error.toException())
            }

        }
        userIdRef.addValueEventListener(valueEventListener)
        awaitClose { userIdRef.removeEventListener(valueEventListener) }
    }

    fun getUserByIdFlow(userId: String): Flow<UsersData?> = callbackFlow {
        val userRef = database.child("users").child(userId)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UsersData::class.java)
                trySend(user)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        userRef.addValueEventListener(valueEventListener)
        awaitClose { userRef.removeEventListener(valueEventListener) }
    }

    suspend fun findUserByChatId(chatId: String, currentUserId: String): UsersData {
        val chatMembersRef = database.child("chats").child(chatId).child("members")
        return try {
            val membersSnapshot = chatMembersRef.get().await()
            val membersMap = membersSnapshot.value as? Map<*, *>
            if (membersMap != null) {
                val getterId = membersMap.keys.find { it != currentUserId }
                if (getterId != null) {
                    val userRef = database.child("users").child(getterId as String)
                    val userSnapshot = userRef.get().await()
                    userSnapshot.getValue(UsersData::class.java)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("Firebase", "Ошибка при поиске пользователя: ${e.message}")
        } as UsersData
    }

    fun getMessages(chatId: String): Flow<List<MessagesData>> = callbackFlow {
        val chatMessagesRef = database.child("messages").child(chatId)
        val allMessages = mutableListOf<MessagesData>()

        val childEventListener = object: ChildEventListener {
            override fun onChildAdded(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) {
                val message = snapshot.getValue(MessagesData::class.java)
                if (message != null) {
                    allMessages.add(message)
                    trySend(allMessages.toList())
                }
            }

            override fun onChildChanged(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) {
                val updatedMessage = snapshot.getValue(MessagesData::class.java)
                if (updatedMessage != null) {
                    val index = allMessages.indexOfFirst { it.timestamp == updatedMessage.timestamp }
                    if (index != -1) {
                        allMessages[index] = updatedMessage
                        trySend(allMessages.toList())
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Ошибка при чтении сообщений ${error.message}")
            }
        }
        chatMessagesRef.addChildEventListener(childEventListener)
        awaitClose { chatMessagesRef.removeEventListener(childEventListener) }
    }

    suspend fun sendMessage(chatId: String,
                            currentUserId: String,
                            getterId: String,
                            text: String){
        val chatMessagesRef = database.child("messages").child(chatId)
        Log.d("Firebase", "Проверка ChatId $chatId")
        val newMessageRef = chatMessagesRef.push()
        val messageId = newMessageRef.key
        val isoTimestamp: String = DateTimeFormatter.ISO_INSTANT
            .format(Instant.now().atOffset(ZoneOffset.UTC))
        val messageData = MessagesData(
            messageId = messageId,
            senderId = currentUserId,
            status = mapOf(
                currentUserId to "read",
                getterId to "unread"
            ),
            text = text,
            timestamp = isoTimestamp
        )
        try {
            newMessageRef.setValue(messageData).await()
            Log.d("Firebase", "Сообщение успешно отправлено")
            val updates = mapOf(
                "chats/$chatId/lastMessageId" to messageId!!,
                "chats/$chatId/lastMessageTimestamp" to isoTimestamp
            )
            database.updateChildren(updates).await()
            Log.d("Firebase", "Данные о чате обновлены")
        } catch (e: Exception) {
            Log.e("Firebase", "Ошибка при отправке данных ${e.message}")
            throw e
        }
    }

    suspend fun markMessageAsRead(chatId: String,
                             currentUserId: String){
        val chatMessageRef = database.child("messages").child(chatId)
        val updates = hashMapOf<String, Any>()
        try {
            val dataSnapshot = chatMessageRef.get().await()
            for (messageSnapshot in dataSnapshot.children) {
                val messageId = messageSnapshot.key
                val message = messageSnapshot.getValue(MessagesData::class.java)
                if (message != null && message.status?.get(currentUserId) == "unread") {
                    updates["messages/$chatId/$messageId/status/$currentUserId"] = "read"
                }
            }
            if (updates.isNotEmpty()) {
                database.updateChildren(updates).await()
                Log.d("Firebase", "Непрочитанные сообщения помечены как прочитанные")
            }
        } catch (e: Exception) {
            Log.e("Firebase", "Ошибка при обновлении статуса сообщений: ${e.message}")
            throw e
        }
    }

    fun findUserByPhoneNumber(phoneNumber: String): Flow<UsersData?> = callbackFlow {
        val usersRef = database.child("users")
        val query = usersRef.orderByChild("phone").equalTo(phoneNumber)
        Log.d("Firebase", "Ищем пользователя $phoneNumber")
        val valueEventListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.children.firstOrNull()?.getValue(UsersData::class.java)
                    trySend(user)
                    Log.d("Firebase", "Найден пользователь $user")
                }
                else {
                    Log.d("Firebase", "Пользователь не найден")
                    trySend(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
                Log.d("Firebase", "Ошибка при поиске ${error.message}")
            }
        }
        query.addValueEventListener(valueEventListener)
        awaitClose { query.removeEventListener(valueEventListener) }
    }

    suspend fun checkChatForExistence(currentUserId: String, getterUserId: String): String? {
        val currentUserChatsRef = database.child("users").child(currentUserId).child("chats")
        val gettingUserChatsRef = database.child("users").child(getterUserId).child("chats")

        return try {
            val currentUserChatsSnapshot = currentUserChatsRef.get().await()
            val gettingUserChatsSnapshot = gettingUserChatsRef.get().await()

            val currentUserChatIds = currentUserChatsSnapshot.children.mapNotNull { it.key }.toSet()
            val gettingUserChatIds = gettingUserChatsSnapshot.children.mapNotNull { it.key }.toSet()

            currentUserChatIds.intersect(gettingUserChatIds).firstOrNull()
        } catch (e: Exception) {
            Log.e("Firebase", "Ошибка при проверке чата на существование ${e.message}")
        } as String?
    }

    fun createChat(currentUserId: String, getterUserId: String): String? {
        return try {
            val chatId = database.child("chats").push().key ?: return null
            val messageId = database.child("messages").child(chatId).push().key
            val newChat = ChatsData(
                chatId = chatId,
                isGroup = false,
                lastMessageId = messageId,
                lastMessageTimestamp = "",
                members = mapOf(
                    currentUserId to true,
                    getterUserId to true
                )
            )
            val newMessage = MessagesData(
                messageId = messageId,
                senderId = currentUserId,
                status = mapOf(
                    currentUserId to "read",
                    getterUserId to "read"
                ),
                text = "",
                timestamp = ""
            )

            val updates = hashMapOf(
                "/chats/$chatId" to newChat,
                "/users/$currentUserId/chats/$chatId" to true,
                "/users/$getterUserId/chats/$chatId" to true,
                "/messages/$chatId/$messageId" to newMessage
            )
            database.updateChildren(updates)
            chatId
        } catch (e: Exception) {
            Log.e("Firebase", "Ошибка при создании чата: ${e.message}")
            null
        }
    }

    suspend fun deleteChat(chatId: String) {
        val chatRef = database.child("chats").child(chatId)
        try {
            val chatMembersSnapshot = chatRef.child("members").get().await()
            val chatMembers = chatMembersSnapshot.children.mapNotNull { it.key }
            if (chatMembers.size < 2) {
                Log.e("Firebase", "В чате недостаточно участников для удаления")
                return
            }
            val userOne = chatMembers[0]
            val userTwo = chatMembers[1]
            val updates = hashMapOf<String, Any?>(
                "/users/$userOne/chats/$chatId" to null,
                "/users/$userTwo/chats/$chatId" to null,
                "/chats/$chatId" to null,
                "/messages/$chatId" to null
            )
            database.updateChildren(updates).await()
            Log.d("Firebase", "Чат $chatId успешно удален у всех участников")
        } catch (e: Exception) {
            Log.e("Firebase", "Ошибка при удалении чата $chatId: ${e.message}")
            throw e
        }
    }

    suspend fun loadNewUser(user: UsersData?): Boolean {
        val userId = database.child("users").push().key ?: return false
        val userRef = database.child("users").child(userId)
        val newUser = user?.copy(userId = userId)
        return try {
            userRef.setValue(newUser).await()
            Log.d("Firebase", "Пользователь успешно создан: $newUser")
            true
        } catch (e: Exception) {
            Log.e("Firebase", "Ошибка при создании пользователя: ${e.message}")
            false
        }
    }

    fun getUserGroups(userId: String): Flow<List<GroupsData?>> = callbackFlow {
        val userRef = database.child("users").child(userId).child("groups")
        val valueEventListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val groupsIds = snapshot.children.mapNotNull { it.key }
                Log.d("Firebase", "Группы пользователя $userId: $groupsIds")
                launch {
                    val groupsList = groupsIds.map { groupId ->
                        try {
                            val groupSnapshot = database.child("groups").child(groupId).get().await()
                            groupSnapshot.getValue(GroupsData::class.java)
                        } catch (e: Exception) {
                            Log.e("Firebase", "Ошибка при загрузке чата $groupId: ${e.message}")
                            null
                        }
                    }
                    Log.d("Firebase", "${groupsList.size}")
                    trySend(groupsList)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Ошибка при извлечении списка чатов $error")
                close(error.toException())
            }

        }
        userRef.addValueEventListener(valueEventListener)
        awaitClose { userRef.removeEventListener(valueEventListener) }
    }
}