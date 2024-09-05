package com.golfpvcc.teamscore_rev4.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.golfpvcc.teamscore_rev4.database.model.PlayerRecord

@Dao
interface PlayerDao {
    // Player's record
    @Query("Select * FROM PlayerRecord")
    fun getAllPlayerRecords(): List<PlayerRecord>

    @Query("DELETE  FROM PlayerRecord")
    fun  deleteAllPlayersRecord()

    @Upsert
    suspend fun addUpdatePlayerRecord(playerRecord: PlayerRecord)

    @Query("SELECT * FROM PlayerRecord WHERE mScoreCardRecFk = :scoreCardRecId AND mId = :playerID")
    fun getPlayerRecord(playerID: Int, scoreCardRecId: Int):PlayerRecord

    @Query("UPDATE PlayerRecord SET mName = :name, mHandicap = :handicap WHERE mId =:index")
    fun updatePlayer(index: Int, name: String, handicap: Int)

    @Delete
    suspend fun deleteDeletePlayerRecord(playerRecord: PlayerRecord)
}