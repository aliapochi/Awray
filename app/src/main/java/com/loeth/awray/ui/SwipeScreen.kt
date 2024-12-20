package com.loeth.awray.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.loeth.awray.AwrayViewModel
import com.loeth.awray.CommonImage
import com.loeth.awray.CommonProgressSpinner
import com.loeth.awray.data.UserData
import com.loeth.awray.swipecards.Direction
import com.loeth.awray.swipecards.rememberSwipeableCardState
import com.loeth.awray.swipecards.swipableCard
import kotlinx.coroutines.launch

@Composable
fun SwipeScreen(navController: NavController, viewModel: AwrayViewModel) {
    val inProgress = viewModel.inProgressProfiles.value
    if (inProgress)
        CommonProgressSpinner()
    else {
        val profiles = viewModel.matchProfiles.value
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            // Spacer
            Spacer(modifier = Modifier.height(1.dp))

            // Cards
            val states = profiles.map { it to rememberSwipeableCardState() }
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .aspectRatio(1f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "No more profiles available")
                }
                states.forEach { (matchProfile, state) ->
                    ProfileCard(
                        modifier = Modifier
                            .fillMaxSize()
                            .swipableCard(
                                state = state,
                                blockedDirections = listOf(Direction.Down),
                                onSwiped = {},
                                onSwipeCancel = { Log.d("Swipeable card", "Cancelled swipe") }),
                        matchProfile = matchProfile
                    )
                    LaunchedEffect(matchProfile, state.swipedDirection) {
                        if (state.swipedDirection != null) {
                            if (state.swipedDirection == Direction.Left ||
                                state.swipedDirection == Direction.Down
                            ) {
                                viewModel.onDislike(matchProfile)
                            } else {
                                viewModel.onLike(matchProfile)
                            }
                            viewModel.removeProfile(matchProfile)
                        }
                    }
                }
            }

            // Buttons
            val scope = rememberCoroutineScope()
            Row(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CircleButton(onClick = {
                    scope.launch {
                        val last = states.reversed().firstOrNull {
                            it.second.offset.value == Offset(0f, 0f)
                        }?.second
                        last?.swipe(Direction.Left)
                    }
                }, icon = Icons.Rounded.Close)
                CircleButton(onClick = {
                    scope.launch {
                        val last = states.reversed().firstOrNull {
                            it.second.offset.value == Offset(0f, 0f)
                        }?.second
                        last?.swipe(Direction.Right)
                    }
                }, icon = Icons.Rounded.Favorite)
            }

            // Bottom nav bar
            BottomNavigationMenu(
                selectedItem = BottomNavigationItem.SWIPE,
                navController = navController
            )
        }
    }
}

@Composable
private fun CircleButton(
    onClick: () -> Unit,
    icon: ImageVector,
) {
    IconButton(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .size(56.dp)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
        onClick = onClick
    ) {
        Icon(
            icon, null,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun ProfileCard(
    modifier: Modifier,
    matchProfile: UserData,
) {
    Card(modifier) {
        Box {
            CommonImage(data = matchProfile.imageUrl, modifier = Modifier.fillMaxSize())
            Scrim(Modifier.align(Alignment.BottomCenter))
            Column(Modifier.align(Alignment.BottomStart)) {
                Text(
                    text = matchProfile.name ?: matchProfile.username ?: "",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}


@Composable
fun Scrim(modifier: Modifier = Modifier) {
    Box(
        modifier
            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
            .height(180.dp)
            .fillMaxWidth()
    )
}