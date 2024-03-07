package com.golfpvcc.teamscore_rev4.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "CourseTable")
data class CourseRecord(
    val mCoursename: String,   // this is the database key for this course in the CourseListRecord class
    val mUsstate: String? = "NC",
    val mPar: IntArray,
    val mHandicap: IntArray,
    @PrimaryKey(autoGenerate = true)    // default is false
    val mId: Int = 0
)
