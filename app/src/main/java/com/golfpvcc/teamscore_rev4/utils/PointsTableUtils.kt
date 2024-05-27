package com.golfpvcc.teamscore_rev4.utils

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.golfpvcc.teamscore_rev4.TeamScoreCardApp
import com.golfpvcc.teamscore_rev4.database.dao.CourseDao
import com.golfpvcc.teamscore_rev4.database.dao.PointsDao
import com.golfpvcc.teamscore_rev4.database.model.PointsRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class PointRecord(
    val key: Int = 0,
    val value: Int = 0,
)

const val PQ_EAGLE = 1
const val PQ_BIRDIES = 2
const val PQ_PAR = 3
const val PQ_BOGGY = 4
const val PQ_DOUBLE = 5
const val PQ_OTHER = 6
const val PQ_ALBATROSS = 7
const val PQ_END = 8 // indexes into the point quota array

val pointsTable: List<PointRecord> = listOf(
    PointRecord(PQ_ALBATROSS, 5),
    PointRecord(PQ_EAGLE, 4),
    PointRecord(PQ_BIRDIES, 3),
    PointRecord(PQ_PAR, 2),
    PointRecord(PQ_BOGGY, 1),
    PointRecord(PQ_DOUBLE, 0),
    PointRecord(PQ_OTHER, -1),
    PointRecord(PQ_END, 20)
)

// indexes into the point quota array
suspend fun createPointTableRecords() {
    val pointsDao: PointsDao = TeamScoreCardApp.getPointsDao()   // are companion object
    val buildDatabase = pointsDao.isEmpty()

    if (buildDatabase) {
        Log.d("VIN", "Build points table")
        coroutineScope {
            launch {
                for (ptRecord: PointRecord in pointsTable) {
                    val pointsRecord = PointsRecord(mId = ptRecord.key, mPoints = ptRecord.value)
                    pointsDao.addUpdatePointTableRecord(pointsRecord)
                }
            }
        }
    } else{
        Log.d("VIN", "Have points table")
    }
}