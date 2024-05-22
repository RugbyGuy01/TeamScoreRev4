package com.golfpvcc.teamscore_rev4.ui.navigation

const val ROOT_GRAPH_ROUTE = "root"
const val ROUTE_CONFIGURATION = "Configuration"
const val ROUTE_GAME_ON = "GameOn"
const val ROUTE_EXIT = "Exit"

sealed class TeamScoreScreen(val route: String) {
    object ScreenCourses : TeamScoreScreen(route = "Courses")

    object ScreenDetailCourse : TeamScoreScreen(route = "CourseDetail?id={id}") {
        fun passId(id: Int = -1): String {
            return "CourseDetail?id=$id"
        }
    }
    object ScreenPlayerSetup : TeamScoreScreen(route = "PlayerSetup?id={id}"){
        fun passId(id: Int = -1): String {
            return "PlayerSetup?id=$id"
        }
    }
    object ScreenScoreCard : TeamScoreScreen(route = "ScoreCardScreen"){
        fun passId(id: Int = -1): String {
            return "ScoreCard?id=$id"
        }
    }
    object ScreenSummary : TeamScoreScreen(route = "SummaryScreen"){
        fun passId(id: Int = -1): String {
            return "Summary?id=$id"
        }
    }
}