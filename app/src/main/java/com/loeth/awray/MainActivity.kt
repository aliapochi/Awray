package com.loeth.awray

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.loeth.awray.ui.ChatListScreen
import com.loeth.awray.ui.LoginScreen
import com.loeth.awray.ui.ProfileScreen
import com.loeth.awray.ui.SignupScreen
import com.loeth.awray.ui.SingleChatScreen
import com.loeth.awray.ui.SwipeScreen
import com.loeth.awray.ui.theme.AwrayTheme
import dagger.hilt.android.AndroidEntryPoint

sealed class DestinationScreen(val route: String) {
    object Login : DestinationScreen("login")
    object Signup : DestinationScreen("signup")
    object Profile : DestinationScreen("profile")
    object ChatList : DestinationScreen("chatList")
    object Swipe : DestinationScreen("swipe")
    object SingleChat : DestinationScreen("singleChat/{chatId}") {
        fun createRoute(id: String) = "singleChat/$id"
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AwrayTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SwipeAppNavigation()
                }
            }
        }
    }
}

@Composable
fun SwipeAppNavigation() {
    val navController = rememberNavController()
    val viewModel = hiltViewModel<AwrayViewModel>()
    NotificationMessage(vm = viewModel)

    NavHost(navController = navController, startDestination = DestinationScreen.Signup.route)
    {
        composable(DestinationScreen.Signup.route) {
            SignupScreen(navController, viewModel)
        }
        composable(DestinationScreen.Login.route) {
            LoginScreen(navController, viewModel)
        }
        composable(DestinationScreen.Profile.route) {
            ProfileScreen(navController, viewModel)
        }
        composable(DestinationScreen.ChatList.route) {
            ChatListScreen(navController, viewModel)
        }
        composable(DestinationScreen.SingleChat.route) {
            val chatId = it.arguments?.getString("chatId")
            chatId?.let{
                SingleChatScreen(navController = navController, viewModel = viewModel, chatId = it )
            }
        }
        composable(DestinationScreen.Swipe.route) {
            SwipeScreen(navController, viewModel)
        }
    }
}



