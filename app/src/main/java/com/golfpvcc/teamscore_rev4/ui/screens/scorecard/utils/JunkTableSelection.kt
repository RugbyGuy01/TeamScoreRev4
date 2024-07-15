package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.golfpvcc.teamscore_rev4.database.dao.JunkDao
import com.golfpvcc.teamscore_rev4.database.dao.PlayerJunkDao
import com.golfpvcc.teamscore_rev4.database.model.JunkRecord
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.JunkTableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JunkTableSelection(junkDao:JunkDao, playerJunkDao: PlayerJunkDao) {
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
        Log.d("VIN", "Junk Table Load Rec Cnt ${mJunkTableList.size}")
    }

    fun loadPlayerJunkRecords(playerIdx:Int, currentHole:Int){
        clearJunkTableListSelections()

            val playerJunkRecords = mPlayerJunkDao.getPlayerJunkTableRecords(playerIdx, currentHole)
            for (playerJunkRecord in playerJunkRecords) {
                var junkTableDisplay = mJunkTableList.find { it.mId == playerJunkRecord.mId }
                if (junkTableDisplay != null) {
                    junkTableDisplay.mSelected = true
                }
            }

    }

    private fun clearJunkTableListSelections(){
        for (junkTableList in mJunkTableList) {
            junkTableList.mSelected = false
        }
    }
}