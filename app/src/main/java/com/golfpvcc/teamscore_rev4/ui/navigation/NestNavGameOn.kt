package com.golfpvcc.teamscore_rev4.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.ScoreCardScreen
import com.golfpvcc.teamscore_rev4.ui.screens.summary.SummaryScreen
import kotlin.system.exitProcess

fun NavGraphBuilder.teamGameOn(navController: NavHostController) {
    navigation(
        startDestination = TeamScoreScreen.ScreenSummary.route,
        route = ROUTE_GAME_ON
    ) {
        composable(route = TeamScoreScreen.ScreenSummary.route,
            arguments = listOf(
                navArgument(name = "id") {
                    type = NavType.IntType
                    defaultValue = -2
                }
            )) { id ->
            SummaryScreen(
                navController, id.arguments?.getInt("id")
            )
        }
        composable(route = ROUTE_EXIT) {
            exitProcess(-1)
        }
        composable(route = TeamScoreScreen.ScreenScoreCard.route,
            arguments = listOf(
                navArgument(name = "id") {
                    type = NavType.IntType
                    defaultValue = -2
                }
            )) { id ->
            ScoreCardScreen(
                navController
            )
        }
    }
}