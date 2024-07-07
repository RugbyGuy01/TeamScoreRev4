package com.golfpvcc.teamscore_rev4.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.golfpvcc.teamscore_rev4.utils.HOLE_ARRAY_SIZE
import com.golfpvcc.teamscore_rev4.utils.SCORE_CARD_REC_ID


@Entity(tableName = "PlayerRecord")
data class PlayerRecord(
    val mName: String = "",   // this is the database key for this course in the CourseListRecord class
    var mHandicap: String = "",
    val mScore: IntArray = IntArray(HOLE_ARRAY_SIZE),
    val mTeamHole: IntArray = IntArray(HOLE_ARRAY_SIZE),
    val mScoreCardRecFk: Int = SCORE_CARD_REC_ID,    // score card record ID
    @PrimaryKey(autoGenerate = false)    // default is false
    val mId: Int = 0
)