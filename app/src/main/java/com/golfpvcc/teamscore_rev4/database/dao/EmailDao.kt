package com.golfpvcc.teamscore_rev4.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.golfpvcc.teamscore_rev4.database.model.EmailRecord

@Dao
interface EmailDao {
    @Query("Select * FROM EmailTable")
    fun getAllEmailRecords(): List<EmailRecord>

    @Upsert
    suspend fun addUpdateEmailTableRecord(emailRecord: EmailRecord)

    @Query("SELECT * FROM EmailTable WHERE mId = :ptRecId")
    fun getEmailTableRecord(ptRecId: Int): EmailRecord

    @Query("SELECT (SELECT COUNT(*) FROM EmailTable) == 0")    // check for empty database
    fun isEmpty(): Boolean
}