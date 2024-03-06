package com.golfpvcc.teamscore_rev4.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "PlayerRecord")
data class PlayerRecord(
    val mName: String,   // this is the database key for this course in the CourseListRecord class
    val mHandicap: Int = 0,
    val mScore: IntArray,
    val scoreCardRec_Fk: Int,    // score card record ID
    @PrimaryKey(autoGenerate = false)    // default is false
    val mId: Int = 0
)