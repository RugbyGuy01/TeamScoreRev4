package com.golfpvcc.teamscore_rev4.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.golfpvcc.teamscore_rev4.database.model.PlayerJunkRecord

@Dao
interface PlayerJunkDao {
    @Query("Select * FROM PlayerJunkTable")
    fun getAllPlayerJunkRecords(): List<PlayerJunkRecord>

    @Upsert
    suspend fun addUpdatePlayerJunkTableRecord(playerJunkRecord: PlayerJunkRecord)

    @Query("SELECT * FROM PlayerJunkTable WHERE mPlayerIdx = :playerIdx")
    fun getPlayerJunkTableRecords(playerIdx: Int): List<PlayerJunkRecord>

    @Query("SELECT (SELECT COUNT(*) FROM PlayerJunkTable) == 0")    // check for empty database
    fun isEmpty(): Boolean
}