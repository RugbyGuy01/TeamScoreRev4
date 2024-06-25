package com.golfpvcc.teamscore_rev4.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "EmailTable")
data class EmailRecord(
    val mEmailName: String = "",
    val mEmailAddress: String = "",
    @PrimaryKey(autoGenerate = true)    // autoGenerate is false
    val mId: Int = 0,
)
