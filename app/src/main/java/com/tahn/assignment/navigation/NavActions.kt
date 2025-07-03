package com.tahn.assignment.navigation

import androidx.navigation.NavController
import com.tahn.assignment.nav.UserDetail

class AppNavAction(
    private val navController: NavController,
) {
    fun navigateToDetail(username: String) {
        navController.navigate(UserDetail(username))
    }
}
