package com.golfpvcc.teamscore_rev4.utils

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.golfpvcc.teamscore_rev4.database.model.CourseRecord
import com.golfpvcc.teamscore_rev4.database.model.PlayerRecord

object Constants {
    const val SCORE_CARD_REC_ID = 2024
    const val MAX_PLAYERS = 4
    const val MAX_PLAYER_NAME = 8
    const val MAX_HANDICAP = 3
    const val MAX_COURSE_YARDAGE = 5
    const val MAX_STARTING_HOLE = 2
    const val LAST_PLAYER = MAX_PLAYERS - 1
    const val DISPLAY_SCORE_CARD_SCREEN = 6
    const val DISPLAY_COURSES_SCREEN = 7
    const val MINIMUM_LEN_OF_PLAYER_NAME = 2

    const val DATABASE_NAME = "TeamDatabase"
    const val SCORE_CARD_TEXT = 18

    const val COLUMN_TOTAL_WIDTH = 65

    const val VIN_LIGHT_GRAY = 0xFFE0E0E0

    const val USER_CANCEL = 100
    const val USER_SAVE = 101
    const val USER_TEXT_SAVE = 102
    const val USER_TEXT_UPDATE = 103

        val courseDetailPlaceHolder = CourseRecord(
        "No Course", "", IntArray(18){4}, IntArray(18)
    )
    fun List<CourseRecord>?.orCourseRecHolderList(): List<CourseRecord> {
        fun courseRecHolderList(): List<CourseRecord> {
            return listOf(CourseRecord("No Course", "", IntArray(18){4}, IntArray(18)))
        }
        return if (this != null && this.isNotEmpty()){
            this
        } else courseRecHolderList()
    }
}

@Composable
fun setScreenOrientation(orientation : Int) {
    val activity = LocalContext.current as Activity
    activity.requestedOrientation = orientation
}


