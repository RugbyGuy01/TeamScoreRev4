package com.golfpvcc.teamscore_rev4.utils

import android.util.Log
import com.golfpvcc.teamscore_rev4.TeamScoreCardApp
import com.golfpvcc.teamscore_rev4.database.dao.PointsDao
import com.golfpvcc.teamscore_rev4.database.model.PointsRecord
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class PointTable(
    val key: Int = 0,
    var value: String = "",
    val label: String = "",
    var oldValue: String = "",  // kept track off the old value in case the user cancels
)

const val PQ_EAGLE = 1
const val PQ_BIRDIES = 2
const val PQ_PAR = 3
const val PQ_BOGGY = 4
const val PQ_DOUBLE = 5
const val PQ_OTHER = 6
const val PQ_ALBATROSS = 7
const val PQ_TARGET = 8

private val pointsTable: List<PointTable> = listOf(
    PointTable(PQ_TARGET, "36", "PLayer's Quota Target"),
    PointTable(PQ_ALBATROSS, "5", "Albatross"),
    PointTable(PQ_EAGLE, "4", "Eagle"),
    PointTable(PQ_BIRDIES, "3", "Birdies"),
    PointTable(PQ_PAR, "2", "Par"),
    PointTable(PQ_BOGGY, "1", "Bogeys"),
    PointTable(PQ_DOUBLE, "0", "Double"),
    PointTable(PQ_OTHER, "0", "Other"),
)

// indexes into the point quota array
suspend fun createPointTableRecords() {
    val pointsDao: PointsDao = TeamScoreCardApp.getPointsDao()   // are companion object
    val buildDatabase = pointsDao.isEmpty()

    if (buildDatabase) {
        Log.d("VIN", "Build points table")
        coroutineScope {
            launch {
                for (ptRecord: PointTable in pointsTable) {
                    val pointsRecord =
                        PointsRecord(mId = ptRecord.key, mPoints = ptRecord.value.toInt(), ptRecord.label)
                    pointsDao.addUpdatePointTableRecord(pointsRecord)
                }
            }
        }
    } else {
        Log.d("VIN", "Have points table")
    }
}