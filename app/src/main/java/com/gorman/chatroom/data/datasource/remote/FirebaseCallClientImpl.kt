package com.gorman.chatroom.data.datasource.remote

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.gorman.chatroom.domain.models.CallModel
import com.gorman.chatroom.domain.models.CallModelType
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class FirebaseCallClientImpl @Inject constructor(
    private val db: DatabaseReference,
    private val gson: Gson
) : FirebaseCallClient {

    private var clientId: String? = null

    override fun setClientId(id: String) {
        this.clientId = id
    }

    override fun subscribeForLatestEvent(listener: FirebaseCallClient.Listener) {
        if (clientId == null) return
        val myRef = db.child("WebRTCSignaling").child(clientId!!)
        myRef.child("latest_event").addValueEventListener(object : MyEventListener() {
            override fun onDataChange(snapshot: DataSnapshot) {
                super.onDataChange(snapshot)
                if (!snapshot.exists()) return

                val event = try {
                    gson.fromJson(snapshot.value.toString(), CallModel::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }

                event?.let { listener.onLatestEventReceived(it) }
            }
        })
        myRef.child("candidates").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val event = try {
                    gson.fromJson(snapshot.value.toString(), CallModel::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
                if (event != null && event.type == CallModelType.IceCandidates) {
                    listener.onLatestEventReceived(event)
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override suspend fun sendMessageToOtherClient(
        message: CallModel
    ): Boolean = suspendCancellableCoroutine { continuation ->

        val convertedMessage = gson.toJson(message.copy(sender = clientId))
        val targetRef = db.child("WebRTCSignaling").child(message.target)

        val task = if (message.type == CallModelType.IceCandidates) {
            targetRef.child("candidates")
                .push()
                .setValue(convertedMessage)
        } else {
            if (message.type == CallModelType.Offer) {
                targetRef.child("candidates").removeValue()
                targetRef.child("latest_event").removeValue()
            }

            targetRef.child("latest_event")
                .setValue(convertedMessage)
        }
        task.addOnCompleteListener { result ->
            if (continuation.isActive) {
                continuation.resume(result.isSuccessful)
            }
        }.addOnFailureListener {
            if (continuation.isActive) {
                continuation.resume(false)
            }
        }
    }
}