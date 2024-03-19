package com.golfpvcc.teamscore_rev4.ui.navigation

const val ROOT_GRAPH_ROUTE = "root"
const val ROUTE_CONFIGURATION = "Configuration"
const val ROUTE_GAME_ON = "GameOn"
sealed class TeamScoreScreen(val route: String) {
    object Courses : TeamScoreScreen(route = "Courses")

    object DetailCourse : TeamScoreScreen(route = "CourseDetail?id={id}") {
        fun passId(id: Int = -1): String {
            return "CourseDetail?id=$id"
        }
    }
    object PlayerSetup : TeamScoreScreen(route = "PlayerSetup?id={id}"){
        fun passId(id: Int = -1): String {
            return "PlayerSetup?id=$id"
        }
    }
    object ScoreCard : TeamScoreScreen(route = "ScoreCardScreen"){
        fun passId(id: Int = -1): String {
            return "ScoreCard?id=$id"
        }
    }
}