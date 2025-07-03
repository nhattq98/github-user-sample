package com.tahn.assignment.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tahn.assignment.detail.GithubUserDetailScreen
import com.tahn.assignment.home.GithubUserListScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
object UserList

@Serializable
data class UserDetail(
    val username: String,
)

@Composable
fun MyApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = UserList) {
        composable<UserList> { GithubUserListScreen(koinViewModel()) }
        composable<UserDetail> { GithubUserDetailScreen() }
    }
}
