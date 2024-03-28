package com.golfpvcc.teamscore_rev4.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost


@Composable
fun SetupNavGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_CONFIGURATION,
        //       startDestination = ROUTE_GAME_ON,

        route = ROOT_GRAPH_ROUTE
    ) {
        teamConfiguration(navController)
        teamGameOn(navController)
    }
}

// get view model from a shared nest navigation graph
@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navHostController: NavHostController): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navHostController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}