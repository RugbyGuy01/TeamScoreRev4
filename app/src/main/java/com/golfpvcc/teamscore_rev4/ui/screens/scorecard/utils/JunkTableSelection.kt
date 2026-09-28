package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.golfpvcc.teamscore_rev4.database.dao.JunkDao
import com.golfpvcc.teamscore_rev4.database.dao.PlayerJunkDao
import com.golfpvcc.teamscore_rev4.database.model.PlayerJunkRecord
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.JunkTableList

class JunkTableSelection(junkDao: JunkDao, playerJunkDao: PlayerJunkDao) {
    private val mJunkDao = junkDao    // file descriptor for junk record file
    private val mPlayerJunkDao = playerJunkDao    // file descriptor for junk record file

    // Snapshot-backed so replacing an element (see below) is observed by Compose. JunkTableList
    // is a mutable data class, so mutating a field in place (the old approach) never notified
    // the snapshot system — the row's highlight would never update.
    val mJunkTableList: SnapshotStateList<JunkTableList> = mutableStateListOf()

    fun loadJunkTableRecords() {
        val junkRecordList = mJunkDao.getAllJunkRecords()

        mJunkTableList.clear()
        for (junkRecord in junkRecordList) {
            mJunkTableList.add(JunkTableList(junkRecord.mJunkName, junkRecord.mId, false))
        }
    }

    fun loadPlayerJunkRecords(playerIdx: Int, currentHole: Int) {
        clearJunkTableListSelections()

        val playerJunkRecords = mPlayerJunkDao.getPlayerJunkTableRecords(playerIdx, currentHole)
        for (playerJunkRecord in playerJunkRecords) {
            val index = mJunkTableList.indexOfFirst { it.mId == playerJunkRecord.mJunkId }
            if (index != -1) {
                mJunkTableList[index] = mJunkTableList[index].copy(
                    mSelected = true,
                    mPlayerRecId = playerJunkRecord.mId, // this rec ID for deleting
                )
            }
        }
    }

    fun setJunkPlayerRecordToDB(
        listIdx: Int,
        selection: Boolean,
        playerIdx: Int,
        currentHole: Int,
    ): PlayerJunkRecord {
        val junkListItem = mJunkTableList[listIdx]
        val playerJunkRecord = PlayerJunkRecord(
            playerIdx, currentHole, junkListItem.mId, junkListItem.mPlayerRecId)
        mJunkTableList[listIdx] = junkListItem.copy(mSelected = selection)
        return (playerJunkRecord)
    }

    fun deletePlayerJunkRecord(playerJunkRecord: PlayerJunkRecord) {
        mPlayerJunkDao.deleteJunkTableRecord(playerJunkRecord)
    }
    fun getJunkRecordCnt(playerIdx: Int, currentHole: Int) : Int{
        val junkRecordCnt = mPlayerJunkDao.getPlayerJunkRecordsCnt(playerIdx, currentHole)
        return(junkRecordCnt)
    }
    // listIdx lets us store the DB-assigned id back onto the row so a deselect later in the
    // same session deletes the right record instead of a never-matching mId = 0.
    suspend fun addPlayerJunkRecord(listIdx: Int, playerJunkRecord: PlayerJunkRecord) {
        val newId = mPlayerJunkDao.insertJunkTableRecord(playerJunkRecord)
        if (listIdx in mJunkTableList.indices) {
            mJunkTableList[listIdx] = mJunkTableList[listIdx].copy(mPlayerRecId = newId)
        }
    }

    private fun clearJunkTableListSelections() {
        for (index in mJunkTableList.indices) {
            mJunkTableList[index] = mJunkTableList[index].copy(mSelected = false, mPlayerRecId = 0)
        }
    }
}