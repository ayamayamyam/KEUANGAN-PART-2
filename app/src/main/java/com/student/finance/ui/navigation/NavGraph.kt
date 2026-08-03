package com.student.finance.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.student.finance.ui.screens.account.AccountScreen
import com.student.finance.ui.screens.budget.BudgetScreen
import com.student.finance.ui.screens.category.CategoryManagementScreen
import com.student.finance.ui.screens.dashboard.DashboardScreen
import com.student.finance.ui.screens.debt.DebtScreen
import com.student.finance.ui.screens.export.ExportScreen
import com.student.finance.ui.screens.reports.ReportsScreen
import com.student.finance.ui.screens.savings.SavingGoalScreen
import com.student.finance.ui.screens.settings.SettingsScreen
import com.student.finance.ui.screens.transaction.AddEditTransactionScreen
import com.student.finance.ui.screens.transaction.TransactionListScreen

data class BottomNavItem(val screen: Screen, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard, "Beranda", Icons.Filled.Home),
    BottomNavItem(Screen.Transactions, "Transaksi", Icons.AutoMirrored.Filled.List),
    BottomNavItem(Screen.Budget, "Anggaran", Icons.Filled.PieChart),
    BottomNavItem(Screen.Debt, "Arus Kas", Icons.Filled.AccountBalanceWallet),
    BottomNavItem(Screen.Savings, "Tabungan", Icons.Filled.Savings),
    BottomNavItem(Screen.Settings, "Pengaturan", Icons.Filled.Settings)
)

private val screenTitles = mapOf(
    Screen.Dashboard.route to "Smart Money Manage",
    Screen.Transactions.route to "Semua Transaksi",
    Screen.AddTransaction.route to "Tambah Transaksi",
    Screen.Budget.route to "Anggaran Bulan Ini",
    Screen.Debt.route to "Arus Kas",
    Screen.Savings.route to "Target Menabung",
    Screen.Reports.route to "Laporan Pengeluaran",
    Screen.Settings.route to "Pengaturan",
    Screen.Account.route to "Pilih Akun",
    Screen.Category.route to "Kelola Kategori",
    Screen.Export.route to "Ekspor Data"
)

@Composable
fun StudentFinanceNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var budgetAddTrigger by remember { mutableStateOf(0) }
    var savingsAddTrigger by remember { mutableStateOf(0) }
    var debtAddTrigger by remember { mutableStateOf(0) }

    val showFab = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Transactions.route,
        Screen.Budget.route,
        Screen.Savings.route,
        Screen.Debt.route
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screenTitles[currentRoute] ?: "Smart Money Manage") },
                actions = {
                    if (currentRoute == Screen.Dashboard.route) {
                        IconButton(onClick = { navController.navigate(Screen.Reports.route) }) {
                            Icon(Icons.Filled.Assessment, contentDescription = "Laporan")
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    val selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == item.screen.route } == true
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selected,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(onClick = {
                    when (currentRoute) {
                        Screen.Dashboard.route, Screen.Transactions.route ->
                            navController.navigate(Screen.AddTransaction.route)
                        Screen.Budget.route -> budgetAddTrigger++
                        Screen.Savings.route -> savingsAddTrigger++
                        Screen.Debt.route -> debtAddTrigger++
                    }
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Tambah")
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen() }
            composable(Screen.Transactions.route) { TransactionListScreen() }
            composable(Screen.AddTransaction.route) {
                AddEditTransactionScreen(onDone = { navController.popBackStack() })
            }
            composable(Screen.Budget.route) { BudgetScreen(addTrigger = budgetAddTrigger) }
            composable(Screen.Debt.route) { DebtScreen(addTrigger = debtAddTrigger) }
            composable(Screen.Savings.route) { SavingGoalScreen(addTrigger = savingsAddTrigger) }
            composable(Screen.Reports.route) { ReportsScreen() }
            composable(Screen.Settings.route) { SettingsScreen(navController = navController) }
            composable(Screen.Account.route) { AccountScreen() }
            composable(Screen.Category.route) { CategoryManagementScreen() }
            composable(Screen.Export.route) { ExportScreen() }
        }
    }
}
