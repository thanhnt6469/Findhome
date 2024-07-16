package com.app.findhome.utils

import android.util.Log
import com.app.findhome.data.model.ChatCardItemContent
import com.app.findhome.data.model.ChatMessage
import com.app.findhome.data.model.User
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

object FirebaseUtils {
    private const val CHAT_MESSAGES_NODE = "chat_messages"
    private const val USERS_NODE = "users"
    private val database: DatabaseReference by lazy {
        Firebase.database.reference
    }

    fun sendMessage(chatMessage: ChatMessage, roomId: String) {
        val messageId = database.child(CHAT_MESSAGES_NODE).child(roomId).push().key ?: ""
        chatMessage.copy(messageId = messageId).also {
            database.child(CHAT_MESSAGES_NODE).child(roomId).child(messageId).setValue(it)
        }
    }

    fun getMessagesFlow(roomId: String): StateFlow<List<ChatMessage>> {
        val messagesFlow = MutableStateFlow(emptyList<ChatMessage>())
        val messagesRef = database.child(CHAT_MESSAGES_NODE).child(roomId)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { it.getValue(ChatMessage::class.java) }
                messagesFlow.value = messages
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseUtils", "Error fetching messages", error.toException())
            }
        }
        messagesRef.addValueEventListener(valueEventListener)
        return messagesFlow
    }


    fun getLatestMessagesForUser(currentUserId: String): StateFlow<List<ChatCardItemContent>> {
        val latestMessagesFlow = MutableStateFlow<List<ChatCardItemContent>>(emptyList())
        val messagesRef = database.child(CHAT_MESSAGES_NODE)

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val roomId = snapshot.key ?: ""
                if (roomId.contains(currentUserId)) {
                    val otherUserId = roomId.replace(currentUserId, "").replace("-", "")
                    val latestMessageRef = messagesRef.child(roomId).limitToLast(1)
                    latestMessageRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(messageSnapshot: DataSnapshot) {
                            val latestMessage = messageSnapshot.children.firstOrNull()?.getValue(ChatMessage::class.java)
                            latestMessage?.let { message ->
                                val userRef = database.child(USERS_NODE).child(otherUserId)
                                userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(userSnapshot: DataSnapshot) {
                                        val user = userSnapshot.getValue(User::class.java)
                                        user?.let { userData ->
                                            val newChatItem = ChatCardItemContent(
                                                id = otherUserId,
                                                image = userData.profilePictureUrl,
                                                messengerName = userData.fullName,
                                                message = message.message,
                                                messageDate = formatDate(message.timestamp)
                                            )
                                            latestMessagesFlow.update { currentList ->
                                                val newList = currentList.toMutableList()
                                                newList.removeAll { it.id == otherUserId }
                                                newList.add(newChatItem)
                                                newList
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.e("FirebaseUtils", "Error fetching user data", error.toException())
                                    }
                                })
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("FirebaseUtils", "Error fetching latest message", error.toException())
                        }
                    })
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle change if necessary
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Handle removal if necessary
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle movement if necessary
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseUtils", "Error fetching room ids", error.toException())
            }
        }

        messagesRef.addChildEventListener(childEventListener)

        return latestMessagesFlow
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("HH:mm dd/MM/yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }

}
