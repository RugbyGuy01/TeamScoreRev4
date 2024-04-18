package com.golfpvcc.teamscore_rev4.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.golfpvcc.teamscore_rev4.database.model.PlayerRecord
import kotlinx.coroutines.flow.Flow
@Dao
interface PlayerDao {
    // Player's record
    @Query("Select * FROM PlayerRecord")
    fun getAllPlayerRecords(): List<PlayerRecord>

    @Query("DELETE  FROM PlayerRecord")
    fun  deleteAllPlayersRecord()

    @Upsert
    suspend fun addUpdatePlayerRecord(playerRecord: PlayerRecord)

    @Query("SELECT * FROM PlayerRecord WHERE mScoreCardRecFk = :scoreCardRec_Id AND mId = :playerID")
    fun getPlayerRecord(playerID: Int, scoreCardRec_Id: Int):PlayerRecord

    @Query("UPDATE PlayerRecord SET mName = :name, mHandicap = :handicap WHERE mId =:index")
    fun updatePlayer(index: Int, name: String, handicap: Int):Unit
}