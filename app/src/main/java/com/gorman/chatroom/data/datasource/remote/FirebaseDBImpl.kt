package com.gorman.chatroom.data.datasource.remote

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.gorman.chatroom.domain.models.ChatsData
import com.gorman.chatroom.domain.models.GroupsData
import com.gorman.chatroom.domain.models.MessagesData
import com.gorman.chatroom.domain.models.UsersData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class FirebaseDBImpl @Inject constructor(
    private val database: DatabaseReference
) : FirebaseDB {

    override fun getUserChats(userId: String): Flow<List<ChatsData?>> = callbackFlow {
        val userIdRef = database.child(FirebaseConstants.USERS.value)
            .child(userId)
            .child(FirebaseConstants.CHATS.value)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatIds = snapshot.children.mapNotNull { it.key }
                Log.d("Firebase", "Чаты пользователя $userId: $chatIds")
                launch {
                    val chatsList = chatIds.map { chatId ->
                        try {
                            val chatSnapshot = database.child(FirebaseConstants.CHATS.value).child(chatId).get().await()
                            chatSnapshot.getValue(ChatsData::class.java)
                        } catch (e: FirebaseException) {
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

    override fun getUserByIdFlow(userId: String): Flow<UsersData?> = callbackFlow {
        val userRef = database.child(FirebaseConstants.USERS.value).child(userId)

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

    override suspend fun findUserByChatId(chatId: String, currentUserId: String): UsersData {
        val chatMembersRef = database
            .child(FirebaseConstants.CHATS.value)
            .child(chatId)
            .child(FirebaseConstants.MEMBERS.value)
        return try {
            val membersSnapshot = chatMembersRef.get().await()
            val membersMap = membersSnapshot.value as? Map<*, *>
            if (membersMap != null) {
                val getterId = membersMap.keys.find { it != currentUserId }
                if (getterId != null) {
                    val userRef = database
                        .child(FirebaseConstants.USERS.value)
                        .child(getterId as String)
                    val userSnapshot = userRef.get().await()
                    userSnapshot.getValue(UsersData::class.java)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: FirebaseException) {
            Log.e("Firebase", "Ошибка при поиске пользователя: ${e.message}")
        } as UsersData
    }

    override suspend fun findUsersByGroupId(groupId: String, currentUserId: String): List<UsersData?> {
        val groupMembersRef = database
            .child(FirebaseConstants.GROUPS.value)
            .child(groupId)
            .child(FirebaseConstants.MEMBERS.value)
        val gettersList = mutableListOf<UsersData?>()
        return try {
            val membersSnapshot = groupMembersRef.get().await()
            val membersMap = membersSnapshot.value as? Map<*, *>
            if (membersMap != null) {
                val getterIds = membersMap.keys.filter { it != currentUserId }
                for (id in getterIds) {
                    val userRef = database.child(FirebaseConstants.USERS.value).child(id as String)
                    val userSnapshot = userRef.get().await()
                    gettersList.add(userSnapshot.getValue(UsersData::class.java))
                }
                gettersList
            } else {
                emptyList()
            }
        } catch (e: FirebaseException) {
            Log.e("Firebase", "Ошибка при поиске пользователей: ${e.message}")
            emptyList()
        }
    }

    override suspend fun updateUserData(userId: String, user: UsersData?) {
        val userRef = database.child(FirebaseConstants.USERS.value).child(userId)
        try {
            userRef.setValue(user).await()
        } catch (e: FirebaseException) {
            Log.d("Firebase", "Не удалось добавить пользователя ${e.message}")
        }
    }

    override fun getMessages(conversationId: String): Flow<List<MessagesData>> = callbackFlow {
        val chatMessagesRef = database
            .child(FirebaseConstants.MESSAGES.value)
            .child(conversationId)
        val allMessages = mutableListOf<MessagesData>()

        val childEventListener = object : ChildEventListener {
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
                    val index =
                        allMessages.indexOfFirst { it.timestamp == updatedMessage.timestamp }
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

    override fun getLastMessage(conversationId: String): Flow<MessagesData?> = callbackFlow {
        val chatMessagesRef =
            database
                .child(FirebaseConstants.MESSAGES.value)
                .child(conversationId)
                .orderByChild(FirebaseConstants.TIMESTAMP.value)
                .limitToLast(1)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lastMessage = snapshot.children
                    .firstOrNull()
                    ?.getValue(MessagesData::class.java)
                trySend(lastMessage)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        chatMessagesRef.addValueEventListener(listener)
        awaitClose { chatMessagesRef.removeEventListener(listener) }
    }

    override suspend fun sendMessage(chatId: String, currentUserId: String, getterId: String, text: String){
        val chatMessagesRef = database
            .child(FirebaseConstants.MESSAGES.value)
            .child(chatId)
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
                "${FirebaseConstants.CHATS.value}/$chatId/lastMessageId" to messageId!!,
                "${FirebaseConstants.CHATS.value}/$chatId/lastMessageTimestamp" to isoTimestamp
            )
            database.updateChildren(updates).await()
            Log.d("Firebase", "Данные о чате обновлены")
        } catch (e: FirebaseException) {
            Log.e("Firebase", "Ошибка при отправке данных ${e.message}")
            throw e
        }
    }

    override suspend fun sendGroupMessage(groupId: String, currentUserId: String, getterUsers: List<UsersData?>, text: String){
        val groupMessagesRef = database.child(FirebaseConstants.MESSAGES.value).child(groupId)
        val newMessageRef = groupMessagesRef.push()
        val messageId = newMessageRef.key
        val isoTimestamp: String = DateTimeFormatter.ISO_INSTANT
            .format(Instant.now().atOffset(ZoneOffset.UTC))
        val status = HashMap<String, String>(emptyMap<String, String>())
        for (user in getterUsers) {
            user?.userId?.let { status[it] = "unread" }
        }
        status[currentUserId] = "read"
        val messageData = MessagesData(
            messageId = messageId,
            senderId = currentUserId,
            status = status,
            text = text,
            timestamp = isoTimestamp
        )
        try {
            newMessageRef.setValue(messageData).await()
            Log.d("Firebase", "Сообщение успешно отправлено")
            val updates = mapOf(
                "${FirebaseConstants.GROUPS.value}/$groupId/lastMessageId" to messageId,
                "${FirebaseConstants.GROUPS.value}/$groupId/lastMessageTimestamp" to isoTimestamp
            )
            database.updateChildren(updates).await()
            Log.d("Firebase", "Данные о группе обновлены")
        } catch (e: FirebaseException) {
            Log.e("Firebase", "Ошибка при отправке данных ${e.message}")
            throw e
        }
    }

    override suspend fun markMessageAsRead(chatId: String,
                             currentUserId: String){
        val chatMessageRef = database.child(FirebaseConstants.MESSAGES.value).child(chatId)
        val updates = hashMapOf<String, Any>()
        try {
            val dataSnapshot = chatMessageRef.get().await()
            for (messageSnapshot in dataSnapshot.children) {
                val messageId = messageSnapshot.key
                val message = messageSnapshot.getValue(MessagesData::class.java)
                if (message != null && message.status?.get(currentUserId) == "unread") {
                    updates["${FirebaseConstants.MESSAGES.value}/$chatId/$messageId/status/$currentUserId"] = "read"
                }
            }
            if (updates.isNotEmpty()) {
                database.updateChildren(updates).await()
                Log.d("Firebase", "Непрочитанные сообщения помечены как прочитанные")
            }
        } catch (e: FirebaseException) {
            Log.e("Firebase", "Ошибка при обновлении статуса сообщений: ${e.message}")
            throw e
        }
    }

    override fun findUserByPhoneNumber(phoneNumber: String): Flow<UsersData?> = callbackFlow {
        val usersRef = database.child(FirebaseConstants.USERS.value)
        val query = usersRef
            .orderByChild(FirebaseConstants.PHONE.value)
            .equalTo(phoneNumber)
        Log.d("Firebase", "Ищем пользователя $phoneNumber")
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.children.firstOrNull()?.getValue(UsersData::class.java)
                    trySend(user)
                    Log.d("Firebase", "Найден пользователь $user")
                } else {
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

    override suspend fun checkChatForExistence(currentUserId: String, getterUserId: String): String? {
        val currentUserChatsRef = database
            .child(FirebaseConstants.USERS.value)
            .child(currentUserId)
            .child(FirebaseConstants.CHATS.value)
        val gettingUserChatsRef = database
            .child(FirebaseConstants.USERS.value)
            .child(getterUserId)
            .child(FirebaseConstants.CHATS.value)

        return try {
            val currentUserChatsSnapshot = currentUserChatsRef.get().await()
            val gettingUserChatsSnapshot = gettingUserChatsRef.get().await()

            val currentUserChatIds = currentUserChatsSnapshot.children.mapNotNull { it.key }.toSet()
            val gettingUserChatIds = gettingUserChatsSnapshot.children.mapNotNull { it.key }.toSet()

            currentUserChatIds.intersect(gettingUserChatIds).firstOrNull()
        } catch (e: FirebaseException) {
            Log.e("Firebase", "Ошибка при проверке чата на существование ${e.message}")
        } as String?
    }

    override fun createChat(currentUserId: String, getterUserId: String): String? {
        return try {
            val chatId = database.child(FirebaseConstants.CHATS.value).push().key ?: return null
            val messageId = database.child(FirebaseConstants.MESSAGES.value).child(chatId).push().key
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
                "/${FirebaseConstants.CHATS.value}/$chatId" to newChat,
                "/${FirebaseConstants.USERS.value}/$currentUserId/chats/$chatId" to true,
                "/${FirebaseConstants.USERS.value}/$getterUserId/chats/$chatId" to true,
                "/${FirebaseConstants.MESSAGES.value}/$chatId/$messageId" to newMessage
            )
            database.updateChildren(updates)
            chatId
        } catch (e: FirebaseException) {
            Log.e("Firebase", "Ошибка при создании чата: ${e.message}")
            null
        }
    }

    override fun createGroup(currentUserId: String, getterUsers: List<String?>, groupName: String): String? {
        return try {
            val groupId = database.child(FirebaseConstants.GROUPS.value).push().key ?: return null
            val messageId = database.child(FirebaseConstants.MESSAGES.value).child(groupId).push().key
            val members = hashMapOf<String, Boolean>()
            val membersRead = hashMapOf<String, String>()
            for (user in getterUsers) {
                user?.let {
                    members[it] = true
                    membersRead[it] = "read"
                }
            }
            membersRead[currentUserId] = "read"
            members[currentUserId] = true
            val newGroup = GroupsData(
                groupId = groupId,
                admins = mapOf(
                    currentUserId to true
                ),
                groupName = groupName,
                lastMessageId = messageId,
                lastMessageTimestamp = "",
                members = members
            )
            val newMessage = MessagesData(
                messageId = messageId,
                senderId = currentUserId,
                status = membersRead,
                text = "",
                timestamp = ""
            )
            val updates = hashMapOf(
                "/${FirebaseConstants.GROUPS.value}/$groupId" to newGroup,
                "/${FirebaseConstants.USERS.value}/$currentUserId/${FirebaseConstants.GROUPS.value}/$groupId" to true,
                "/${FirebaseConstants.MESSAGES.value}/$groupId/$messageId" to newMessage
            )
            for (user in getterUsers) {
                user?.let {
                    updates["/${FirebaseConstants.USERS.value}/$it/${FirebaseConstants.GROUPS.value}/$groupId"] = true
                }
            }
            database.updateChildren(updates)
            groupId
        } catch (e: FirebaseException) {
            Log.e("Firebase", "Ошибка при создании группы: ${e.message}")
            null
        }
    }

    override suspend fun deleteChat(chatId: String) {
        val chatRef = database.child(FirebaseConstants.CHATS.value).child(chatId)
        try {
            val chatMembersSnapshot = chatRef.child(FirebaseConstants.MEMBERS.value).get().await()
            val chatMembers = chatMembersSnapshot.children.mapNotNull { it.key }
            if (chatMembers.size < 2) {
                Log.e("Firebase", "В чате недостаточно участников для удаления")
                return
            }
            val userOne = chatMembers[0]
            val userTwo = chatMembers[1]
            val updates = hashMapOf<String, Any?>(
                "/${FirebaseConstants.USERS.value}/$userOne/${FirebaseConstants.CHATS.value}/$chatId" to null,
                "/${FirebaseConstants.USERS.value}/$userTwo/${FirebaseConstants.CHATS.value}/$chatId" to null,
                "/${FirebaseConstants.CHATS.value}/$chatId" to null,
                "/${FirebaseConstants.MESSAGES.value}/$chatId" to null
            )
            database.updateChildren(updates).await()
            Log.d("Firebase", "Чат $chatId успешно удален у всех участников")
        } catch (e: FirebaseException) {
            Log.e("Firebase", "Ошибка при удалении чата $chatId: ${e.message}")
            throw e
        }
    }

    override suspend fun loadNewUser(user: UsersData?): Boolean {
        val userId = database.child(FirebaseConstants.USERS.value).push().key ?: return false
        val userRef = database.child(FirebaseConstants.USERS.value).child(userId)
        val newUser = user?.copy(userId = userId)
        return try {
            userRef.setValue(newUser).await()
            Log.d("Firebase", "Пользователь успешно создан: $newUser")
            true
        } catch (e: FirebaseException) {
            Log.e("Firebase", "Ошибка при создании пользователя: ${e.message}")
            false
        }
    }

    override fun getUserGroups(userId: String): Flow<List<GroupsData?>> = callbackFlow {
        val userRef = database
            .child(FirebaseConstants.USERS.value)
            .child(userId)
            .child(FirebaseConstants.GROUPS.value)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val groupsIds = snapshot.children.mapNotNull { it.key }
                Log.d("Firebase", "Группы пользователя $userId: $groupsIds")
                launch {
                    val groupsList = groupsIds.map { groupId ->
                        try {
                            val groupSnapshot =
                                database.child(FirebaseConstants.GROUPS.value).child(groupId).get().await()
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