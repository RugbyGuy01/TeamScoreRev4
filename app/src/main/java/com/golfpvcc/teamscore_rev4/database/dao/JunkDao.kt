package com.golfpvcc.teamscore_rev4.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.golfpvcc.teamscore_rev4.database.model.JunkRecord

@Dao
interface JunkDao {
    @Query("Select * FROM JunkTable")
    fun getAllJunkRecords(): List<JunkRecord>

    @Upsert
    suspend fun addUpdateJunkTableRecord(junkRecord: JunkRecord)

    @Query("SELECT * FROM JunkTable WHERE mId = :ptRecId")
    fun getJunkTableRecord(ptRecId: Int): JunkRecord

    @Query("SELECT (SELECT COUNT(*) FROM JunkTable) == 0")    // check for empty database
    fun isEmpty(): Boolean
}