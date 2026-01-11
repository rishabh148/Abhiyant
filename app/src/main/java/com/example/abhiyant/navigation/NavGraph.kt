package com.example.abhiyant.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.abhiyant.ui.screen.InspectionDetailScreen
import com.example.abhiyant.ui.screen.InspectionEntryScreen
import com.example.abhiyant.ui.screen.InspectionListScreen

@Composable
fun InspectionNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.InspectionList.route
    ) {
        composable(route = Screen.InspectionList.route) {
            InspectionListScreen(
                onNavigateToEntry = {
                    navController.navigate(Screen.InspectionEntry.route)
                },
                onNavigateToDetail = { inspectionId ->
                    navController.navigate(Screen.InspectionDetail(0L).createRoute(inspectionId))
                }
            )
        }
        
        composable(route = Screen.InspectionEntry.route) {
            InspectionEntryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetail = { savedId ->
                    navController.popBackStack()
                    navController.navigate(Screen.InspectionDetail(0L).createRoute(savedId))
                }
            )
        }
        
        composable(
            route = Screen.InspectionEntry.ROUTE_WITH_ID,
            arguments = listOf(navArgument("inspectionId") { type = NavType.LongType })
        ) {
            InspectionEntryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetail = { savedId ->
                    navController.popBackStack()
                    navController.navigate(Screen.InspectionDetail(0L).createRoute(savedId))
                }
            )
        }
        
        composable(
            route = Screen.InspectionDetail(0L).route,
            arguments = listOf(navArgument("inspectionId") { type = NavType.LongType })
        ) {
            InspectionDetailScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.InspectionEntry.createRoute(id))
                }
            )
        }
    }
}

