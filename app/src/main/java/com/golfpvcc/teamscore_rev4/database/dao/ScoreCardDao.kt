package com.golfpvcc.teamscore_rev4.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardWithPlayers
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreCardDao {
    /* Score Card DAO interfaces */
    @Upsert
    suspend fun addUpdateScoreCardRecord(scoreCardRecord: ScoreCardRecord)

    @Query("Select * FROM ScoreCardRecord WHERE mScoreCardRecId = :scoreCardId ")
    fun getScoreCardRecord(scoreCardId: Int): ScoreCardRecord

    @Transaction
    @Query("Select * FROM ScoreCardRecord WHERE mScoreCardRecId = :scoreCardId ")
    fun getScoreRecordWithPlayers(scoreCardId: Int?): ScoreCardWithPlayers

    @Query("SELECT EXISTS(SELECT * FROM ScoreCardRecord WHERE mScoreCardRecId = :scoreCardId)")
    fun isRowIsExist(scoreCardId : Int) : Boolean
}