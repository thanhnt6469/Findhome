package com.app.findhome.ui.pages.message

import android.graphics.Color.parseColor
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.app.findhome.R
import com.app.findhome.data.model.ChatMessage
import com.app.findhome.data.model.User
import com.app.findhome.ui.theme.PurpleGrey80
import com.app.findhome.utils.FirebaseUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatPage(
    currentUser: User,
    otherUser: User,
    navController: NavController
) {
    Log.d("ChatPage", "currentUser: $currentUser")
    Log.d("ChatPage", "otherUser: $otherUser")
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            val chatRoomId = generateChatRoomId(currentUser.userId, otherUser.userId)
            Log.d("ChatPage", "generateChatRoomId: $chatRoomId")
            FirebaseUtils.getMessagesFlow(chatRoomId).collect { fetchedMessages ->
                messages = fetchedMessages
                Log.d("ChatPage", "messages: $messages")
            }

        } catch (e: Exception) {
            Log.e("ChatPage", "Error collecting messages", e)
        }
    }

    Log.d("ConstraintLayout", "ConstraintLayout")
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (header, messagesList, chatBox) = createRefs()
        val listState = rememberLazyListState()
        LaunchedEffect(messages.size) {
            listState.animateScrollToItem(messages.size)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 10.dp, 16.dp, 5.dp)
                .constrainAs(header) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_left),
                    contentDescription = "Back",
                    tint = Color(parseColor("#32357a")),
                    modifier = Modifier.size(33.dp)
                )
            }
            Text(
                text = otherUser.fullName,
                modifier = Modifier.align(Alignment.Center),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(parseColor("#32357a"))
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(messagesList) {
                    top.linkTo(header.bottom)
                    bottom.linkTo(chatBox.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                },
            contentPadding = PaddingValues(16.dp)
        ) {
            items(messages) { message ->
                Log.d("ChatItem", "message: $message")
                ChatItem(message, currentUser.userId)
            }
        }

        Log.d("ChatBox", "ChatBox")
        ChatBox(
            onSendChatClickListener = {
                sendMessage(currentUser.fullName, currentUser.userId, otherUser.userId, it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(chatBox) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
    }
}

@Composable
fun ChatItem(message: ChatMessage, currentUserId: String) {
    Log.d("ChatItem", "message: $message")
    val isCurrentUser = message.senderId == currentUserId
    val messageBackgroundColor = if (isCurrentUser) PurpleGrey80 else Color.Gray
    val messageAlignment = if (isCurrentUser) Alignment.End else Alignment.Start

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)) {
        Box(
            modifier = Modifier
                .align(messageAlignment)
                .clip(
                    RoundedCornerShape(
                        topStart = 48f,
                        topEnd = 48f,
                        bottomStart = if (isCurrentUser) 48f else 0f,
                        bottomEnd = if (isCurrentUser) 0f else 48f
                    )
                )
                .background(color = messageBackgroundColor)
                .padding(16.dp)
        ) {
            Text(text = message.message)
        }
        Text(
            text = formatTimestamp(message.timestamp),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.align(messageAlignment)
        )
    }
}

@Composable
fun ChatBox(
    onSendChatClickListener: (String) -> Unit,
    modifier: Modifier
) {
    Log.d("ChatBox", "onSendChatClickListener: $onSendChatClickListener")
    var chatBoxValue by remember { mutableStateOf(TextFieldValue("")) }

    Row(modifier = modifier.padding(16.dp)) {
        TextField(
            value = chatBoxValue,
            onValueChange = { newText ->
                chatBoxValue = newText
            },
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = {
                Text(text = "Nhập nội dung")
            }
        )
        IconButton(
            onClick = {
                val message = chatBoxValue.text.trim()
                if (message.isNotBlank()){
                    onSendChatClickListener(message)
                    chatBoxValue = TextFieldValue("")
                }
            },
            modifier = Modifier
                .clip(CircleShape)
                .background(color = PurpleGrey80)
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = "Send",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            )
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("dd-M-yy h:mm a", Locale.getDefault())
    val date = Date(timestamp)
    return dateFormat.format(date).replace("SA", "AM").replace("CH", "PM")
}

fun generateChatRoomId(userId1: String, userId2: String): String {
    Log.d("generateChatRoomId", "userId1: $userId1")
    Log.d("generateChatRoomId", "userId2: $userId2")
    return if (userId1 < userId2) {
        "$userId1-$userId2"
    } else {
        "$userId2-$userId1"
    }
}

fun sendMessage(senderName: String, senderId: String, receiverId: String, message: String) {
    Log.d("sendMessage", "senderName: $senderName")
    Log.d("sendMessage", "senderId: $senderId")
    Log.d("sendMessage", "receiverId: $receiverId")
    Log.d("sendMessage", "message: $message")
    val chatMessage = ChatMessage(
        senderId = senderId,
        senderName = senderName,
        message = message,
        timestamp = System.currentTimeMillis()
    )
    FirebaseUtils.sendMessage(chatMessage, generateChatRoomId(senderId, receiverId))
}