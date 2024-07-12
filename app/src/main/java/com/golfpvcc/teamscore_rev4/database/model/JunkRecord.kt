package com.golfpvcc.teamscore_rev4.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey


    @Entity(tableName = "JunkTable")
    data class JunkRecord(
        var mJunkName: String = "",
        @PrimaryKey(autoGenerate = false)    // autoGenerate is false
        var mId: Int = 0,
    )
