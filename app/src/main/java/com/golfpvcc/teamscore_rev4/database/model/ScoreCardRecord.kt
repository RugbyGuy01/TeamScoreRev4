package com.golfpvcc.teamscore_rev4.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "ScoreCardRecord")
data class ScoreCardRecord(
    val mCourseName: String,    // current course name from the course list database
    val mTee : String,           // the tee's played or the course yardage
    val mDatePlayed: String,    // the day we play the game
    val mCurrentHole: Int = 0,   // the current hole being played in the game
    val mCourseId:Int = 0,      // the current course we are using for the score card
    val mPar: IntArray,         // the current course Par
    val mHandicap: IntArray,       // current course handicap
    @PrimaryKey
    val mScoreCardRecId: Int?,    // score card record ID
)