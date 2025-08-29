package com.gorman.chatroom.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.database
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseDB {
    val database = Firebase.database.getReference("ChatRoom")

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
        val newMessageRef = chatMessagesRef.push()
        val messageId = newMessageRef.key
        val messageData = MessagesData(
            senderId = currentUserId,
            status = mapOf(
                currentUserId to "read",
                getterId to "unread"
            ),
            text = text,
            timestamp = System.currentTimeMillis()
        )

        try {
            newMessageRef.setValue(messageData).await()
            Log.d("Firebase", "Сообщение успешно отправлено")
            val updates = hashMapOf<String, Any>(
                "chats/$chatId/lastMessageId" to messageId!!,
                "chats/$chatId/lastMessageTimestamp" to ServerValue.TIMESTAMP
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
}