package com.golfpvcc.teamscore_rev4.ui.screens.summary

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.golfpvcc.teamscore_rev4.TeamScoreCardApp
import com.golfpvcc.teamscore_rev4.database.model.PointsRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardWithPlayers
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.HdcpParHoleHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.PlayerHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.TeamUsedHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.HDCP_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.HOLE_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.PAR_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.TEAM_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.USED_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.summary.utils.updateScoreCardState
import com.golfpvcc.teamscore_rev4.utils.PQ_TARGET
import com.golfpvcc.teamscore_rev4.utils.SCORE_CARD_REC_ID
import com.golfpvcc.teamscore_rev4.utils.createPointTableRecords

open class SummaryViewModel() : ViewModel() {
    var state by mutableStateOf(State())
    private val scoreCardDao = TeamScoreCardApp.getScoreCardDao()
    private val pointsRecordDoa = TeamScoreCardApp.getPointsDao()

    class SummaryViewModelFactor() : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SummaryViewModel() as T
        }
    }

    fun getScoreCardAndPlayerRecord() {
        if (!state.mHasDatabaseBeenRead) {
            state.mHasDatabaseBeenRead = true   // only read the database once
            val scoreCardWithPlayers: ScoreCardWithPlayers =
                scoreCardDao.getScoreRecordWithPlayers(SCORE_CARD_REC_ID)

            if (scoreCardWithPlayers != null) {     // found score record with players
                updateScoreCardState(scoreCardWithPlayers)       // located in helper function file
            } else
                Log.d("VIN1", "getScoreCardAndPlayerRecord is empty")
            state.mGamePointsTable = pointsRecordDoa.getAllPointRecords()
        }
    }

    suspend fun checkPointRecords() {
        createPointTableRecords()
    }

    fun getNumberOfPlayer(): Int {
        val numberOfPlayers = state.playerSummary.size
        return (numberOfPlayers)
    }

    // This function will calculate the points needed by the team from the point Quote.
    fun calculatedTeamPointsNeeded(): Int {
        val numberOfPlayers = state.playerSummary.size
        var playersHandicap = 0

        for (idx in 0 until numberOfPlayers) {
            playersHandicap += state.playerSummary[idx].mPlayer.mHdcp.toInt()
        }
        val ptQuota = state.mGamePointsTable.filter { it.mId == PQ_TARGET }
        var TeamBasePointsNeeded = ptQuota.first().mPoints
        if (ptQuota.isNotEmpty()) {
            TeamBasePointsNeeded *= state.playerSummary.size //if all players had a 0 handicap, this is how many point they would need
        }

        TeamBasePointsNeeded -= playersHandicap // however, subtract the total handicap of all of the players from the team bas point needed
        return (TeamBasePointsNeeded)
    }

    fun frontPtQuotaUsed(): String {
        val frontNinePtQuoteUsed: String
        frontNinePtQuoteUsed = "1.0 (-20)"

        return (frontNinePtQuoteUsed)
    }

    fun backPtQuotaUsed(): String {
        val backNinePtQuoteUsed: String
        backNinePtQuoteUsed = "9.0 (-16.0)"

        return (backNinePtQuoteUsed)
    }

    fun totalPtQuotaUsed(): String {
        val totalNinePtQuoteUsed: String
        totalNinePtQuoteUsed = "10.0 (-36.0)"

        return (totalNinePtQuoteUsed)
    }

    fun frontPointsQuota(): String {
        val frontNinePointsQuota: String
        frontNinePointsQuota = "52.0(51.0)"

        return (frontNinePointsQuota)
    }

    fun backPointsQuota(): String {
        val backNinePointsQuota: String
        backNinePointsQuota = "60.0 (51.0)"

        return (backNinePointsQuota)
    }

    fun totalPointsQuota(): String {
        val totalNinePointsQuota: String
        totalNinePointsQuota = "112.0 (102.0)"

        return (totalNinePointsQuota)
    }

    fun frontScoreOverUnder(): String {
        val frontNineScoreOverUnder: String
        frontNineScoreOverUnder = "56 (-10)"

        return (frontNineScoreOverUnder)
    }

    fun backScoreOverUnder(): String {
        val backNineScoreOverUnder: String
        backNineScoreOverUnder = "54 (-12)"

        return (backNineScoreOverUnder)
    }

    fun totalScoreOverUnder(): String {
        val totalNineScoreOverUnder: String
        totalNineScoreOverUnder = "110 (-22)"

        return (totalNineScoreOverUnder)
    }

    fun frontStablefordUsed(): String {
        val frontStablefordUsed: String
        frontStablefordUsed = "110 (-22)"
        return (frontStablefordUsed)
    }

    fun backStablefordUsed(): String {
        val backStablefordUsed: String
        backStablefordUsed = "54 (-12)"

        return (backStablefordUsed)
    }

    fun totalStablefordUsed(): String {
        val totalStablefordUsed: String
        totalStablefordUsed = "152 (86)"

        return (totalStablefordUsed)
    }
}

data class State(
    val mCourseName: String = "",    // current course name from the course list database
    val mTee: String = "",                   // the tee's played or the course yardage
    val mCourseId: Int = 0,      // the current course we are using for the score card
    var mGameNines: Boolean = false,    // true if we only have 3 players
    var mDisplayMenu:Boolean = false,
    var mHasDatabaseBeenRead: Boolean = false,
    var mGamePointsTable: List<PointsRecord> = emptyList(),
    val hdcpParHoleHeading: List<HdcpParHoleHeading> = listOf(
        HdcpParHoleHeading(HDCP_HEADER, "HdCp"),
        HdcpParHoleHeading(PAR_HEADER, "Par"),
        HdcpParHoleHeading(HOLE_HEADER, mName = "Hole", mTotal = "Total"),
    ),
    var playerSummary: List<PlayerSummary> = emptyList(),
    val teamUsedHeading: List<TeamUsedHeading> = listOf(
        TeamUsedHeading(TEAM_HEADER, "Team"),   // total hole score for selected player
        TeamUsedHeading(USED_HEADER, "Used"),   // total players selected for this hole
    ),
)

data class PlayerSummary(
    var mPlayer: PlayerHeading,
    var mFront: Int = 0,
    var mBack: Int = 0,
    var mEagles: Int = 0,
    var mBirdies: Int = 0,
    var mPars: Int = 0,
    var mBogeys: Int = 0,
    var mDouble: Int = 0,
    var mOthers: Int = 0,
    var mQuote: Int = 0,
    var mStableford:Int = 0,
    var mNineTotal: Int = 0,
    var mSandy: Int = 0,
    var mCTP: Int = 0,
    var mOtherJunk: Int = 0,
)