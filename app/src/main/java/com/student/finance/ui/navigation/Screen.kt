package com.student.finance.ui.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object Transactions : Screen("transactions")
    data object AddTransaction : Screen("add_transaction")
    data object Budget : Screen("budget")
    data object Savings : Screen("savings")
    data object Reports : Screen("reports")
    data object Reminders : Screen("reminders")
    data object Settings : Screen("settings")
}
