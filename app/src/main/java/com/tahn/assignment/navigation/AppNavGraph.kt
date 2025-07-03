package com.tahn.assignment.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tahn.assignment.detail.GithubUserDetailScreen
import com.tahn.assignment.home.GithubUserListScreen
import com.tahn.assignment.nav.UserDetail
import com.tahn.assignment.nav.UserList
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    navActions: AppNavAction =
        remember(navController) {
            AppNavAction(navController)
        },
) {
    NavHost(navController = navController, startDestination = UserList) {
        composable<UserList> {
            GithubUserListScreen(viewModel = koinViewModel(), onNavigateToUserDetail = {
                navActions.navigateToDetail(it)
            })
        }
        composable<UserDetail> {
            GithubUserDetailScreen(
                viewModel = koinViewModel(),
                navigateBack = { navController.navigateUp() },
            )
        }
    }
}
