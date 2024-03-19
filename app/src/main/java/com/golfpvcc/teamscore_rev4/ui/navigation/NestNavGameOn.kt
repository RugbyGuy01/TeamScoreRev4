package com.golfpvcc.teamscore_rev4.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.ScoreCardScreen

fun NavGraphBuilder.teamGameOn(navController: NavHostController) {
    navigation(
        startDestination = TeamScoreScreen.ScoreCard.route,
        route = ROUTE_GAME_ON
    ) {
        composable(route = TeamScoreScreen.ScoreCard.route,
            arguments = listOf(
                navArgument(name = "id") {
                    type = NavType.IntType
                    defaultValue = -2
                }
            )) { id ->
            ScoreCardScreen(
                navController, id.arguments?.getInt("id")
            )
        }
    }
}