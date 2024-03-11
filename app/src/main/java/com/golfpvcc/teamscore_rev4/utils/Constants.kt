package com.golfpvcc.teamscore_rev4.utils

import com.golfpvcc.teamscore_rev4.database.model.CourseRecord
import com.golfpvcc.teamscore_rev4.utils.Constants.courseDetailPlaceHolder

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
    const val USER_SAVE = 101
    const val USER_TEXT_SAVE = 102
    const val USER_TEXT_UPDATE = 103

    fun List<CourseRecord>?.orCourseRecHolderList(): List<CourseRecord> {
        fun courseRecHolderList(): List<CourseRecord> {
            return listOf(CourseRecord("No Course", "", IntArray(18){4}, IntArray(18)))
        }
        return if (this != null && this.isNotEmpty()){
            this
        } else courseRecHolderList()
    }

    val courseDetailPlaceHolder = CourseRecord(
        "No Course", "", IntArray(18){4}, IntArray(18)
    )
}


