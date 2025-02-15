package com.loeth.awray.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.loeth.awray.AwrayViewModel
import com.loeth.awray.CommonImage
import com.loeth.awray.CommonProgressSpinner
import com.loeth.awray.DestinationScreen
import com.loeth.awray.navigateTo

@Composable
fun ChatListScreen(navController: NavController, viewModel: AwrayViewModel) {
    val inProgress = viewModel.inProgressChats.value
    if (inProgress)
        CommonProgressSpinner()
    else {
        val chats = viewModel.chats.value
        val userData = viewModel.userData.value

        Column(modifier = Modifier.fillMaxSize()) {
            if (chats.isEmpty())
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "No Chats Available")
                }
            else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(chats) { chat ->
                        val chatUser =
                            if (chat.user1.userId == userData?.userId) chat.user2 else chat.user1
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(75.dp)
                                .clickable {
                                    chat.chatId?.let {
                                        navigateTo(
                                            navController,
                                            DestinationScreen.SingleChat.createRoute(it)
                                        )
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CommonImage(
                                data = chatUser.imageUrl,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red)
                            )
                            Text(
                                text = chatUser.name ?: "---",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
            BottomNavigationMenu(
                selectedItem = BottomNavigationItem.CHATLIST, navController = navController
            )
        }

    }
}