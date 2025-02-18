package com.golfpvcc.teamscore_rev4.utils

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.golfpvcc.teamscore_rev4.database.model.CourseRecord
var REV_DATE = buildTime()
const val REVISION = "2.9"

const val VIN_LIGHT_GRAY = 0xFFE0E0E0
const val DISPLAY_HOLE_NUMBER = 0xFF48EFF0
const val DISPLAY_NOTE_ON_HOLE = 0xFFFDAFFF
const val YELLOW_GETS_1_STROKES = 0xFFFFFBBB
const val ORANGE_GETS_2_STROKES = 0xFFFFE4C5
const val RED_ONE_UNDER_PAR    = 0xFFF53526
const val PURPLE_TWO_UNDER_PAR = 0xFF3633F6
const val MENU_ROW_LIGHT_GRAY = 0xFFDBDBD8

const val COLOR_NEXT_HOLE = 0xFF5BC23F
const val COLOR_PREV_HOLE = 0xFFAB3654
const val COLOR_SCREEN_MODE = 0xFF8389C2
const val COLOR_ENTER_SCORE_CURSOR = 0xFFF0A1C5
                                   //0xFFF0A1C5

// Colors
const val TEAM_SINGLE_NET_SCORE = 0xFF1BD914
const val TEAM_SINGLE_GROSS_SCORE = 0xFF12B8D9
const val TEAM_DOUBLE_NET_SCORE = 0xFF12910D
const val TEAM_DOUBLE_GROSS_SCORE = 0xFF0D869E

// Screen Summary colors
const val SUMMARY_PAYOUT_COLOR = 0xFFDFFFE5

const val HOLE_ARRAY_SIZE = 18

const val FRONT_NINE_DISPLAY = 9
const val BACK_NINE_DISPLAY = 18
const val TOTAL_18_HOLE = 18


const val FRONT_NINE_TOTAL_DISPLAYED = 8    //zero base, user is one the 9 nine hole
const val BACK_NINE_TOTAL_DISPLAYED = 17      //zero base, user is one the 18 nine hole

const val TEAM_CLEAR_SCORE = 0
const val TEAM_GROSS_SCORE = 1
const val TEAM_NET_SCORE = 2
const val DOUBLE_TEAM_SCORE = 3
// handicap masks
const val PLAYER_STROKES_1 = 1
const val PLAYER_STROKES_2 = 2
const val PLAYER_STROKES_3 = 3

const val ALBATROSS_ON_HOLE = -3
const val EAGLE_ON_HOLE = -2
const val BIRDIES_ON_HOLE = -1
const val PAR_ON_HOLE = 0
const val BOGGY_ON_HOLE = 1
const val DOUBLE_ON_HOLE = 2
const val OTHER_ON_HOLE = 3
const val SCORE_CARD_REC_ID = 2024
const val MAX_PLAYERS = 5
const val MAX_PLAYER_NAME = 8
const val MAX_HANDICAP = 2
const val MAX_COURSE_YARDAGE = 5
const val MAX_STARTING_HOLE = 2
const val LAST_PLAYER = MAX_PLAYERS - 1
const val DISPLAY_SCORE_CARD_SCREEN = 6
const val MINIMUM_LEN_OF_PLAYER_NAME = 1
const val MAX_EMAIL_NAME_LEN = 25
const val MAX_EMAIL_ADDRESS_LEN = 50
const val MAX_JUNK_TEXT_LEN = 15
const val SUMMARY_CARD_WIDTH = 575

// text size for forms
const val DATABASE_NAME = "TeamDatabase.db"
const val SCORE_CARD_TEXT = 18
const val SCORE_CARD_COURSE_NAME_TEXT = 15
const val COURSE_NAME_MINIMUM =5
const val SUMMARY_BUTTON_TEXT = 20
const val MENU_BUTTON_TEXT = 20
const val COLUMN_TOTAL_WIDTH = 65
const val CARD_CELL_HEIGHT = 33
const val SUMMARY_TEXT_SIZE = 20
const val SUMMARY_NAME_TEXT_SIZE = 22
const val SUMMARY_DIALOG_TEXT_SIZE = 20
const val DIALOG_BUTTON_TEXT_SIZE = 20
const val DIALOG_BACKUP_RESTORE_TEXT_SIZE = 20
const val DIALOG_NOTE_HEADER_SIZE = 20


const val USER_CANCEL = 100
const val USER_SAVE = 101
const val USER_TEXT_SAVE = 102
const val USER_TEXT_UPDATE = 103

object Constants {
        val courseDetailPlaceHolder = CourseRecord(
            "No Course",
            "",
            mPar = IntArray(18) { 4 },
            mHandicap = IntArray(18),
            mNotes = Array(18) { "" }
        )
    fun List<CourseRecord>?.orCourseRecHolderList(): List<CourseRecord> {
        fun courseRecHolderList(): List<CourseRecord> {
            return listOf(CourseRecord(
                "No Course",
                "",
                mPar = IntArray(18) { 4 },
                mHandicap = IntArray(18),
                mNotes = Array(18) { "" }
            ))
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


