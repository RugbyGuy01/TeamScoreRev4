package com.golfpvcc.teamscore_rev4.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.golfpvcc.teamscore_rev4.database.model.JunkRecord

@Dao
interface JunkDao {
    @Query("Select * FROM JunkTable")
    fun getAllJunkRecords(): List<JunkRecord>

    @Insert
    suspend fun insertJunkTableRecord(junkRecord: JunkRecord):Long   // return the rec Id

    @Update
    suspend fun updateJunkTableRecord(junkRecord: JunkRecord)   // return the rec Id

    @Query("DELETE FROM JunkTable WHERE mId = :junkId")
    fun deleteRecordById(junkId: Long)

    @Query("SELECT * FROM JunkTable WHERE mId = :ptRecId")
    fun getJunkTableRecord(ptRecId: Int): JunkRecord

    @Query("SELECT (SELECT COUNT(*) FROM JunkTable) == 0")    // check for empty database
    fun isEmpty(): Boolean
}