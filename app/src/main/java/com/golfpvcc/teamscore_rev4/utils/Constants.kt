package com.golfpvcc.teamscore_rev4.utils

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.golfpvcc.teamscore_rev4.database.model.CourseRecord

const val FRONT_NINE_DISPLAY = 9
const val BACK_NINE_DISPLAY = 18
const val TOTAL_18_HOLE = 18
const val VIN_LIGHT_GRAY = 0xFFE0E0E0
const val VIN_HOLE_PLAYED = 0xFF48EFF0
const val GETS_1_STROKES = 0xFFFFF887
const val GETS_2_STROKES = 0xFFFFE4C5
const val FRONT_NINE_TOTAL_DISPLAYED = true
const val BACK_NINE_TOTAL_DISPLAYED = false
const val FRONT_NINE_IS_DISPLAYED = true
const val BACK_NINE_IS_DISPLAY = false
object Constants {
    const val SCORE_CARD_REC_ID = 2024
    const val MAX_PLAYERS = 4
    const val MAX_PLAYER_NAME = 8
    const val MAX_HANDICAP = 2
    const val MAX_COURSE_YARDAGE = 5
    const val MAX_STARTING_HOLE = 2
    const val LAST_PLAYER = MAX_PLAYERS - 1
    const val DISPLAY_SCORE_CARD_SCREEN = 6
    const val DISPLAY_COURSES_SCREEN = 7
    const val MINIMUM_LEN_OF_PLAYER_NAME = 2

    const val DATABASE_NAME = "TeamDatabase"
    const val SCORE_CARD_TEXT = 18
    const val SCORE_CARD_COURSE_NAME_TEXT = 15

    const val COLUMN_TOTAL_WIDTH = 65


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


