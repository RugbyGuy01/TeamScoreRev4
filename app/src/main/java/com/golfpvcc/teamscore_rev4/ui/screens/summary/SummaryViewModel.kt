package com.golfpvcc.teamscore_rev4.ui.screens.summary

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
import com.golfpvcc.teamscore_rev4.ui.screens.summary.utils.SummaryActions
import com.golfpvcc.teamscore_rev4.ui.screens.summary.utils.calculateOverUnderScores
import com.golfpvcc.teamscore_rev4.ui.screens.summary.utils.calculatePtQuote
import com.golfpvcc.teamscore_rev4.ui.screens.summary.utils.calculateStableford
import com.golfpvcc.teamscore_rev4.ui.screens.summary.utils.playerScoreSummary
import com.golfpvcc.teamscore_rev4.ui.screens.summary.utils.updateScoreCardState
import com.golfpvcc.teamscore_rev4.utils.MAX_PLAYERS
import com.golfpvcc.teamscore_rev4.utils.PQ_TARGET
import com.golfpvcc.teamscore_rev4.utils.PointTable
import com.golfpvcc.teamscore_rev4.utils.SCORE_CARD_REC_ID
import com.golfpvcc.teamscore_rev4.utils.createPointTableRecords
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
            configurePointTable()
            teamAndPlayerSummary()
        }
    }

    private fun configurePointTable() {
        val pointsTableRecords = pointsRecordDoa.getAllPointRecords()
        val sortPointTable = pointsTableRecords.sortedByDescending { it.mPoints }
        Log.d("Sort", " sortedBy points $sortPointTable")
        for (ptRecord: PointsRecord in sortPointTable) {
            var pointTable: PointTable
            pointTable = PointTable(ptRecord.mId, ptRecord.mPoints.toString(), ptRecord.label)
            state.mGamePointsTable += pointTable    // the record to the point table list
        }
    }

    fun summaryActions(action: SummaryActions) {
        when (action) {
            SummaryActions.DisplayAboutDialog -> displayAboutDialog()
            SummaryActions.DisplayBackupRestoreDialog -> displayBackupRestoreDialog()
            SummaryActions.DisplayEmailDialog -> displayEmailDialog()
            SummaryActions.DisplayJunkDialog -> displayJunkDialog()
            SummaryActions.DisplayPointsDialog -> displayPointsDialog()
            SummaryActions.SavePointsDialog -> savePointsDialogRecords()
            SummaryActions.CancelPointsDialog -> cancelPointsDialogRecords()
        }
    }

    fun displayAboutDialog() {
        Log.d("VIN", "displayAboutDialog  state ${state.mShowAboutDialog}")
        state.mShowAboutDialog = !state.mShowAboutDialog
        repaintScreen()
    }

    fun displayBackupRestoreDialog() {
        Log.d("VIN", "displayBackupRestoreDialog")
    }

    fun displayEmailDialog() {
        Log.d("VIN", "displayEmailDialog")
    }

    fun displayJunkDialog() {
        Log.d("VIN", "displayJunkDialog")
    }

    fun displayPointsDialog() {
        Log.d("VIN", "displayPointsDialog")
        state.mShowPointsDialog = !state.mShowPointsDialog
        if(state.mShowPointsDialog) {
            for (ptRecord: PointTable in state.mGamePointsTable) {
                ptRecord.oldValue = ptRecord.value  // save all of the old value in case the user cancels
            }
        }
        repaintScreen()
    }

    private fun repaintScreen() {
        state = state.copy(mRepaintScreen = !state.mRepaintScreen)
    }

    fun onPointsTableChange(idx: Int, newValue: String) {
        val specialChar: String = if (newValue == ".")
            "-"
        else newValue

        state.mGamePointsTable.find { it.key == idx }?.value = specialChar
        repaintScreen()
        Log.d("VIN", "onPointsTableChange after ${state.mGamePointsTable}")
    }

    fun getPointTableValue(idx: Int): String {
        var ptValue = state.mGamePointsTable.find { it.key == idx }?.value
        if (ptValue == null)
            ptValue = "0"

        return (ptValue)
    }

    private fun savePointsDialogRecords() {
        viewModelScope.launch(Dispatchers.IO) {
            for (ptRecord: PointTable in state.mGamePointsTable) {
                val pointsRecord =
                    PointsRecord(ptRecord.key, ptRecord.value.toInt(), ptRecord.label)
                pointsRecordDoa.addUpdatePointTableRecord(pointsRecord)
            }
            displayPointsDialog()   // exit dialog
        }
    }

    private fun cancelPointsDialogRecords() {
        for (ptRecord: PointTable in state.mGamePointsTable) {
            ptRecord.value = ptRecord.oldValue  // replace all of the old values
        }
        displayPointsDialog()   // exit dialog
    }

    private fun teamAndPlayerSummary() {
        calculatePtQuote()
        calculateStableford()
        calculateOverUnderScores()
        playerScoreSummary()

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
        val ptQuota = state.mGamePointsTable.filter { it.key == PQ_TARGET }
        var TeamBasePointsNeeded = ptQuota.first().value.toInt()
        if (ptQuota.isNotEmpty()) {
            TeamBasePointsNeeded *= state.playerSummary.size //if all players had a 0 handicap, this is how many point they would need
        }

        TeamBasePointsNeeded -= playersHandicap // however, subtract the total handicap of all of the players from the team bas point needed
        return (TeamBasePointsNeeded)
    }

    fun getABCDGameScore(idx: Int): String {
        val gameABCDScore: String = state.mGameABCD[idx].toString()
        return (gameABCDScore)
    }

    fun frontPtQuota(): String {
        val frontNinePtQuote: String = String.format("%.1f", state.mTotalPtQuoteFront)
        val frontUsedPtQuote: String = String.format("%.1f", state.mUsedPtQuoteFront)
        val frontPtQuotaTotalAndUsed: String = "$frontNinePtQuote ($frontUsedPtQuote)"

        return (frontPtQuotaTotalAndUsed)
    }

    fun backPtQuota(): String {
        val backNinePtQuote: String = String.format("%.1f", state.mTotalPtQuoteBack)
        val backUsedPtQuote: String = String.format("%.1f", state.mUsedPtQuoteBack)
        val backPtQuotaTotalAndUsed: String = "$backNinePtQuote ($backUsedPtQuote)"

        return (backPtQuotaTotalAndUsed)
    }

    @SuppressLint("DefaultLocale")
    fun totalPtQuota(): String {
        val totalPointsQuota: String =
            String.format("%.1f", (state.mTotalPtQuoteFront + state.mTotalPtQuoteBack))
        val totalPointsUsedPtQuota: String =
            String.format("%.1f", state.mUsedPtQuoteFront + state.mUsedPtQuoteBack)
        val totalPointsQuotaTotalAndUsed: String = "$totalPointsQuota ($totalPointsUsedPtQuota)"

        return (totalPointsQuotaTotalAndUsed)
    }

    fun frontPointsQuota(): String {
        val frontNinePointsQuota: String =
            state.mTotalPointsFront.toString() + " (" + state.mQuotaPointsFront.toString() + ")"
        return (frontNinePointsQuota)
    }

    fun backPointsQuota(): String {
        val backNinePointsQuota: String =
            state.mTotalPointsBack.toString() + " (" + state.mQuotaPointsBack.toString() + ")"
        return (backNinePointsQuota)
    }

    fun totalPointsQuota(): String {
        val pointsTotal: String
        val totalPoints: Float = state.mTotalPointsFront + state.mTotalPointsBack
        val usedPoints: Float = state.mQuotaPointsFront + state.mQuotaPointsBack
        pointsTotal = "$totalPoints ($usedPoints)"
        return (pointsTotal)
    }

    fun frontScoreOverUnder(): String {
        val frontTotalScore: String =
            state.mTotalScoreFront.toString() + " (" + state.mOverUnderScoreFront.toString() + ")"
        return (frontTotalScore)
    }

    fun backScoreOverUnder(): String {
        val backTotalScore: String =
            state.mTotalScoreBack.toString() + " (" + state.mOverUnderScoreBack.toString() + ")"
        return (backTotalScore)
    }

    fun totalScoreOverUnder(): String {
        val scoreTotal = state.mTotalScoreFront + state.mTotalScoreBack
        val scoreTotalUnder = state.mOverUnderScoreFront + state.mOverUnderScoreBack
        val totalScoreOverUnder = "$scoreTotal ($scoreTotalUnder)"
        return (totalScoreOverUnder)
    }

    fun frontStablefordUsed(): String {
        val frontStablefordUsed: String =
            state.mTotalStablefordFront.toString() + " (" + state.mUsedStablefordFront.toString() + ")"
        return (frontStablefordUsed)
    }

    fun backStablefordUsed(): String {
        val backStablefordUsed: String =
            state.mTotalStablefordBack.toString() + " (" + state.mUsedStablefordBack.toString() + ")"
        return (backStablefordUsed)
    }

    fun totalStablefordUsed(): String {
        val scoreTotalStableford = state.mTotalStablefordFront + state.mTotalStablefordBack
        val scoreStablefordUnder = state.mUsedStablefordFront + state.mUsedStablefordBack
        val totalStableford = "$scoreTotalStableford ($scoreStablefordUnder)"
        return (totalStableford)
    }
}

data class State(
    val mCourseName: String = "",    // current course name from the course list database
    val mTee: String = "",                   // the tee's played or the course yardage
    val mCourseId: Int = 0,      // the current course we are using for the score card
    var mGameNines: Boolean = false,    // true if we only have 3 players
    var mGameABCD: IntArray = IntArray(MAX_PLAYERS) { 0 },   // A player index 0
    var mDisplayMenu: Boolean = false,
    var mHasDatabaseBeenRead: Boolean = false,
    var mShowJunkDialog: Boolean = false,
    var mShowPointsDialog: Boolean = false,
    var mShowEmailDialog: Boolean = false,
    var mShowBackupRestoreDialog: Boolean = false,
    var mShowAboutDialog: Boolean = false,

    val mRepaintScreen: Boolean = false,
    var mGamePointsTable: List<PointTable> = emptyList(),

    var mTotalPtQuoteFront: Float = 0f,
    var mTotalPtQuoteBack: Float = 0f,
    var mUsedPtQuoteFront: Float = 0f,
    var mUsedPtQuoteBack: Float = 0f,

    var mTotalPointsFront: Float = 0f,
    var mTotalPointsBack: Float = 0f,
    var mQuotaPointsFront: Float = 0f,
    var mQuotaPointsBack: Float = 0f,

    var mTotalScoreFront: Int = 0,
    var mTotalScoreBack: Int = 0,
    var mOverUnderScoreFront: Int = 0,
    var mOverUnderScoreBack: Int = 0,

    var mTotalStablefordFront: Int = 0,
    var mTotalStablefordBack: Int = 0,
    var mUsedStablefordFront: Int = 0,
    var mUsedStablefordBack: Int = 0,

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
    var mStableford: Int = 0,
    var mNineTotal: Int = 0,
    var mSandy: Int = 0,
    var mCTP: Int = 0,
    var mOtherJunk: Int = 0,
)

data class TeamPoints(
    var teamTotalPoints: Int = 0,
    var teamUsedPoints: Int = 0,
)