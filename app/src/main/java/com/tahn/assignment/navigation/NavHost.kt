package com.tahn.assignment.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tahn.assignment.GithubUserListScreen
import kotlinx.serialization.Serializable

@Serializable
object GithubUserList


@Composable
fun MyApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = GithubUserList) {
        composable<GithubUserList> { GithubUserListScreen() }
    }
}