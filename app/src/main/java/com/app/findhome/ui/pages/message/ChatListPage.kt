package com.app.findhome.ui.pages.message

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.app.findhome.data.model.ChatCardItemContent
import com.app.findhome.data.model.ChatScreenUiState
import com.app.findhome.data.model.User
import com.app.findhome.ui.navigation.AppPage
import com.app.findhome.ui.theme.Roboto

@Composable
fun ChatListPage(
    currentUser: User,
    chatCardItems: ChatScreenUiState,
    navController: NavController,
    chatViewModel: ChatViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier.background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .border(0.3.dp, Color.Gray)
                .fillMaxWidth()
                .padding(16.dp, 10.dp, 16.dp, 10.dp),

            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = currentUser.profilePictureUrl,
                contentDescription = "Avatar",
                modifier = Modifier.size(35.dp).align(Alignment.CenterStart).clip(CircleShape)
            )
            Text(
                text = "Đoạn chat",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(android.graphics.Color.parseColor("#32357a"))
            )
        }
        LazyColumn(Modifier.fillMaxSize()) {
            items(chatCardItems.messages) {
                ChatCardItem(navController, it)
            }
        }
        LaunchedEffect(Unit) {
            chatViewModel.getMessages()
        }
    }
}

@Composable
fun ChatCardItem(
    navController: NavController,
    chatCardItem: ChatCardItemContent,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable {
                navController.navigate("${AppPage.ChatPage.route}/${chatCardItem.id}/${chatCardItem.messengerName}")
            }
    ) {
        Box(
            Modifier
                .padding(start = 16.dp)
                .size(50.dp)
                .clip(CircleShape)
        ) {
            AsyncImage(
                model = chatCardItem.image,
                contentDescription = "Messenger image",
                contentScale = ContentScale.Crop
            )
        }

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1f)
        ) {
            Text(
                text = chatCardItem.messengerName,
                fontSize = 15.sp,
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,

                )
            Text(
                text = chatCardItem.message,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                )
        }

        Column(
            modifier = Modifier.padding(end = 16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = chatCardItem.messageDate,
                fontFamily = Roboto,
                fontSize = 12.sp,
                fontWeight = FontWeight.Thin
            )
            if (chatCardItem.messageNumberBadge > 0) {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .size(25.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF5350)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = chatCardItem.messageNumberBadge.toString(),
                        fontSize = 12.sp,
                        fontFamily = Roboto,
                        color = Color.White,
                        fontWeight = FontWeight.Thin
                    )
                }
            }
        }
    }
}