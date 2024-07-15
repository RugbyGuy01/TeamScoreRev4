package com.golfpvcc.teamscore_rev4.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PlayerJunkTable")
    data class PlayerJunkRecord(
    val mPlayerIdx:Int=0,
    val mHoleNumber:Int=0,
    val mJunkId:Int=0,
    @PrimaryKey(autoGenerate = true)    // autoGenerate is false
    val mId: Long = 0,
)