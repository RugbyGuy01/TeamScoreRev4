package com.golfpvcc.teamscore_rev4.utils

import com.golfpvcc.teamscore_rev4.database.model.CourseRecord

object Constants {
    const val SCORE_CARD_REC_ID = 2024
    const val MAX_PLAYERS = 4
    const val MAX_PLAYER_NAME = 8
    const val MAX_HANDICAP = 2
    const val LAST_PLAYER = MAX_PLAYERS - 1
    const val MAX_COURSE_YARDAGE = 5
    const val DISPLAY_SCORE_CARD_SCREEN = 6
    const val DISPLAY_CORSES_SCREEN = 7
    const val MINIMUM_LEN_OF_PLAYER_NAME = 2
    const val DISPLAY_CONFIGATION_SCREEN = "Configuration"
    const val DATABASE_NAME = "TeamDatabase"

    const val USER_CANCEL = 100


    val courseDetailPlaceHolder = CourseRecord(
        "No Course","", IntArray(18), IntArray(18), 0
    )
}