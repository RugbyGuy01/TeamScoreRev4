package com.golfpvcc.teamscore_rev4.ui.navigation

import android.util.Log
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.golfpvcc.teamscore_rev4.ui.screens.coursedetail.CourseDetailScreen
import com.golfpvcc.teamscore_rev4.ui.screens.courses.CoursesScreen
import com.golfpvcc.teamscore_rev4.ui.screens.playersetup.PlayerSetupScreen

fun NavGraphBuilder.teamConfiguration(navController: NavHostController) {
    navigation(
        startDestination = TeamScoreScreen.ScreenCourses.route,
        route = ROUTE_CONFIGURATION
    ) {
        composable(route = TeamScoreScreen.ScreenCourses.route) {
            CoursesScreen(navController)
        }

        composable(route = TeamScoreScreen.ScreenDetailCourse.route,
            arguments = listOf(
                navArgument(name = "id") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )) { id ->
            CourseDetailScreen(
                navController, id.arguments?.getInt("id")
            )
        }

        composable(route = TeamScoreScreen.ScreenPlayerSetup.route,
            arguments = listOf(
                navArgument(name = "id") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )) { id ->
            PlayerSetupScreen(
                navController, id.arguments?.getInt("id")
            )
        }
    } // end of nested navation
}