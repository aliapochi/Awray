package com.loeth.awray.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.loeth.awray.DestinationScreen
import com.loeth.awray.R
import com.loeth.awray.navigateTo

enum class BottomNavigationItem(val icon: Int, val navDestination: DestinationScreen) {
    SWIPE(R.drawable.baseline_swipe, DestinationScreen.Swipe),
    CHATLIST(R.drawable.baseline_chat, DestinationScreen.ChatList),
    PROFILE(R.drawable.baseline_person, DestinationScreen.Profile)
}

@Composable
fun BottomNavigationMenu(
    selectedItem: BottomNavigationItem,
    navController: NavController,
) {
    val insets = WindowInsets.navigationBars
    val navigationBarHeight = insets.getBottom(density = LocalDensity.current)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(WindowInsets.navigationBars.asPaddingValues())
            .background(Color.White)
    ) {
        for (item in BottomNavigationItem.entries) {
            Image(
                painter = painterResource(item.icon), contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f)
                    .size(40.dp)
                    .clickable {
                        navigateTo(navController, item.navDestination.route)
                    },
                colorFilter = if (item == selectedItem) ColorFilter.tint(Color.Black)
                else ColorFilter.tint(Color.Gray)
            )
        }

    }
}