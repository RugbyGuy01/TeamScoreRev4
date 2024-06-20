package com.golfpvcc.teamscore_rev4.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "JunkTable")
data class JunkRecord(
    val mJunkName: String = "",
    @PrimaryKey(autoGenerate = true)    // default is false
    val mId: Int = 0,
)
