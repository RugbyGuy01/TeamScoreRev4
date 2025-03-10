package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils

import com.golfpvcc.teamscore_rev4.database.dao.JunkDao
import com.golfpvcc.teamscore_rev4.database.dao.PlayerJunkDao
import com.golfpvcc.teamscore_rev4.database.model.PlayerJunkRecord
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.JunkTableList

class JunkTableSelection(junkDao: JunkDao, playerJunkDao: PlayerJunkDao) {
    private val mJunkDao = junkDao    // file descriptor for junk record file
    private val mPlayerJunkDao = playerJunkDao    // file descriptor for junk record file

    var mJunkTableList: List<JunkTableList> =
        emptyList()// hold the list of junk table record

    fun loadJunkTableRecords() {
        val junkRecordList = mJunkDao.getAllJunkRecords()

        for (junkRecord in junkRecordList) {
            val junkTableList = JunkTableList(junkRecord.mJunkName, junkRecord.mId, false)
            mJunkTableList += (junkTableList)
        }
    }

    fun loadPlayerJunkRecords(playerIdx: Int, currentHole: Int) {
        clearJunkTableListSelections()

        val playerJunkRecords = mPlayerJunkDao.getPlayerJunkTableRecords(playerIdx, currentHole)
        for (playerJunkRecord in playerJunkRecords) {
            var junkTableDisplay = mJunkTableList.find { it.mId == playerJunkRecord.mJunkId }
            if (junkTableDisplay != null) {
                junkTableDisplay.mSelected = true
                junkTableDisplay.mPlayerRecId = playerJunkRecord.mId // this rec ID for deleting
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
        junkListItem.mSelected = selection
        val playerJunkRecord = PlayerJunkRecord(
            playerIdx, currentHole, junkListItem.mId, junkListItem.mPlayerRecId)
        return (playerJunkRecord)
    }

    fun deletePlayerJunkRecord(playerJunkRecord: PlayerJunkRecord) {
        mPlayerJunkDao.deleteJunkTableRecord(playerJunkRecord)
    }
    fun getJunkRecordCnt(playerIdx: Int, currentHole: Int) : Int{
        val junkRecordCnt = mPlayerJunkDao.getPlayerJunkRecordsCnt(playerIdx, currentHole)
        return(junkRecordCnt)
    }
    suspend fun addPlayerJunkRecord(playerJunkRecord: PlayerJunkRecord) {
        mPlayerJunkDao.insertJunkTableRecord(playerJunkRecord)
    }

    private fun clearJunkTableListSelections() {
        for (junkTableList in mJunkTableList) {
            junkTableList.mSelected = false
            junkTableList.mPlayerRecId = 0
        }
    }
}