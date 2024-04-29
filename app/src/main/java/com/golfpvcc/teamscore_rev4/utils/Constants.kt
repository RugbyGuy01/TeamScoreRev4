package com.golfpvcc.teamscore_rev4.utils

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.golfpvcc.teamscore_rev4.database.model.CourseRecord

const val VIN_LIGHT_GRAY = 0xFFE0E0E0
const val DISPLAY_HOLE_NUMBER = 0xFF48EFF0
const val YELLOW_GETS_1_STROKES = 0xFFFFFBBB
const val ORANGE_GETS_2_STROKES = 0xFFFFE4C5
const val RED_ONE_UNDER_PAR = 0xFFF53526
const val PURPLE_TWO_UNDER_PAR = 0xFFDC0DFF

const val COLOR_NEXT_HOLE = 0xFF5BC23F
const val COLOR_PREV_HOLE = 0xFFAB3654
const val COLOR_SCREEN_MODE = 0xFF8389C2


const val TEAM_SINGLE_NET_SCORE = 0xFF1BD914
const val TEAM_SINGLE_GROSS_SCORE = 0xFF12B8D9
const val TEAM_DOUBLE_NET_SCORE = 0xFF12910D
const val TEAM_DOUBLE_GROSS_SCORE = 0xFF0D869E


const val FRONT_NINE_DISPLAY = 9
const val BACK_NINE_DISPLAY = 18
const val TOTAL_18_HOLE = 18


const val FRONT_NINE_TOTAL_DISPLAYED = 8    //zero base, user is one the 9 nine hole
const val BACK_NINE_TOTAL_DISPLAYED = 17      //zero base, user is one the 18 nine hole
const val FRONT_NINE_IS_DISPLAYED = true      //zero base, user is one the 18 nine hole
const val BACK_NINE_IS_DISPLAYED = false      //zero base, user is one the 18 nine hole

const val TEAM_CLEAR_SCORE = 0x0F
const val TEAM_GROSS_SCORE = 0x10
const val TEAM_NET_SCORE = 0x20
const val DOUBLE_TEAM_SCORE = 0x40
// const val BIT_NOT_USED = 0x80
const val TEAM_SCORE_MASK = 0x70
const val JUST_RAW_SCORE = 0x0F

object Constants {
    const val SCORE_CARD_REC_ID = 2024
    private const val MAX_PLAYERS = 4
    const val MAX_PLAYER_NAME = 8
    const val MAX_HANDICAP = 2
    const val MAX_COURSE_YARDAGE = 5
    const val MAX_STARTING_HOLE = 2
    const val LAST_PLAYER = MAX_PLAYERS - 1
    const val DISPLAY_SCORE_CARD_SCREEN = 6
//    const val DISPLAY_COURSES_SCREEN = 7
    const val MINIMUM_LEN_OF_PLAYER_NAME = 2

    // text size for forms
    const val DATABASE_NAME = "TeamDatabase"
    const val SCORE_CARD_TEXT = 18
    const val SCORE_CARD_COURSE_NAME_TEXT = 15
    const val SUMMARY_BUTTON_TEXT = 20
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
        return if (!this.isNullOrEmpty()){
            this
        } else courseRecHolderList()
    }
}

@Composable
fun SetScreenOrientation(orientation : Int) {
    val activity = LocalContext.current as Activity
    activity.requestedOrientation = orientation
}


