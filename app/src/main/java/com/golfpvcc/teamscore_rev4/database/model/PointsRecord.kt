package com.golfpvcc.teamscore_rev4.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PointsTable")
data class PointsRecord(
    @PrimaryKey(autoGenerate = true)    // default is false
        val mId: Int = 0,
    var mPoints: Int = 0,
    val label:String = "",
)
