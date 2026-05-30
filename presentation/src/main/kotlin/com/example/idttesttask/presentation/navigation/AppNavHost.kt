package com.example.idttesttask.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.idttesttask.presentation.screen.input.InputScreen
import com.example.idttesttask.presentation.screen.table.TableScreen

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = InputScreenRoute) {
        composable<InputScreenRoute> {
            InputScreen(
                onNavigateToTable = { rows, cols ->
                    navController.navigate(TableScreenRoute(rows, cols))
                }
            )
        }
        composable<TableScreenRoute> {
            TableScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
