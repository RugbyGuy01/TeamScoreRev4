package com.golfpvcc.teamscore_rev4.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.golfpvcc.teamscore_rev4.utils.HOLE_ARRAY_SIZE

@Entity(tableName = "CourseTable")
data class CourseRecord(
    val mCoursename: String = "",   // this is the database key for this course in the CourseListRecord class
    val mUsstate: String? = "NC",
    val mPar: IntArray = IntArray(HOLE_ARRAY_SIZE),
    val mHandicap: IntArray = IntArray(HOLE_ARRAY_SIZE),   // gross scores,
    val mNotes:Array<String> = Array(HOLE_ARRAY_SIZE){ "" },   // gross scores,
    @PrimaryKey(autoGenerate = true)    // default is false
    val mId: Int = 0
)
