package com.golfpvcc.teamscore_rev4.ui.screens.scorecard

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.golfpvcc.teamscore_rev4.TeamScoreCardApp
import com.golfpvcc.teamscore_rev4.database.model.CourseRecord
import com.golfpvcc.teamscore_rev4.database.model.PlayerRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardWithPlayers
import com.golfpvcc.teamscore_rev4.utils.BACK_NINE_DISPLAY
import com.golfpvcc.teamscore_rev4.utils.Constants
import com.golfpvcc.teamscore_rev4.utils.Constants.SCORE_CARD_REC_ID
import com.golfpvcc.teamscore_rev4.utils.FRONT_NINE_DISPLAY
import com.golfpvcc.teamscore_rev4.utils.FRONT_NINE_IS_DISPLAYED
import com.golfpvcc.teamscore_rev4.utils.VIN_LIGHT_GRAY
import com.golfpvcc.teamscore_rev4.utils.VIN_HOLE_PLAYED

class ScoreCardViewModel(

) : ViewModel() {
    var state by mutableStateOf(ScoreCard())
        private set
    private val scoreCardDao = TeamScoreCardApp.getScoreCardDao()

    class ScoreCardViewModelFactor() : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ScoreCardViewModel() as T
        }
    }

    fun getScoreCardAndPlayerRecord() {
        val scoreCardWithPlayers: List<ScoreCardWithPlayers> =
            scoreCardDao.getScoreRecordWithPlayers(SCORE_CARD_REC_ID)

        Log.d("VIN", "scoreCardWithPlayers size ${scoreCardWithPlayers.size}  ")
        Log.d("VIN", "playerRecords size ${scoreCardWithPlayers[0].playerRecords.size}  ")

        if (0 < scoreCardWithPlayers.size)
            updateScoreCardRecord(scoreCardWithPlayers[0])
    }

    fun updateScoreCardRecord(scoreCardWithPlayers: ScoreCardWithPlayers) {
        val scoreCardRecord: ScoreCardRecord = scoreCardWithPlayers.scoreCardRecord

        state = state.copy(mCourseName = scoreCardRecord.mCourseName)
        state = state.copy(mCurrentHole = (scoreCardRecord.mCurrentHole - 1))   // zero based

        val parCell: ScoreCardCell? = state.rowHeaderCells.find { it.vinTag == PAR_CELL }
        if (parCell != null) {
            parCell.mHole = scoreCardRecord.mPar
        }
        val hdcpCell: ScoreCardCell? = state.rowHeaderCells.find { it.vinTag == HDCP_CELL }
        if (hdcpCell != null) {
            hdcpCell.mHole = scoreCardRecord.mHandicap
        }
        for(idx in scoreCardWithPlayers.playerRecords.indices){
            state.rowPlayerNames += RowHeading(PLAYER_1_NAME + idx, scoreCardWithPlayers.playerRecords[idx].mName)
        }

    }

    fun getStartingHole(): Int {
        return if (state.mWhatNineIsBeingDisplayed) 0 else FRONT_NINE_DISPLAY
    }

    fun getEndingHole(): Int {
        return if (state.mWhatNineIsBeingDisplayed) FRONT_NINE_DISPLAY else BACK_NINE_DISPLAY
    }

    fun getTotalForNineCell(Holes: IntArray): String {
        var total: Int = 0
        val startingCell: Int = getStartingHole()
        val endingCell: Int = getEndingHole()

        for (idx in startingCell until endingCell) {
            total += Holes[idx]
        }
        Log.d("VIN", "getTotalForNineCell sum $total")
        return total.toString()
    }


    fun flipFrontNineBackNine() {
        state = state.copy(mWhatNineIsBeingDisplayed = !state.mWhatNineIsBeingDisplayed)
    }

    fun setHighLightCurrentHole(holeIdx: Int, displayHoleColor: Color): Color {

        if (displayHoleColor == Color(VIN_HOLE_PLAYED)) { // the only row to high light on the score card
            return if (holeIdx == state.mCurrentHole) Color(VIN_HOLE_PLAYED) else Color(
                VIN_LIGHT_GRAY
            )
        } else {
            return displayHoleColor
        }
    }

    fun setHoleScore(playerIdx: Int, idx: Int, score: Int) {
        Log.d("VIN", "SetHoleScore $idx  Score $score")
        state.rowPlayerCells[playerIdx].mHole[idx] = score
        Log.d("VIN", "SetHoleScore ${state.rowPlayerCells[playerIdx].mHole[idx]}")
        state = state.copy(mRepaintScreen = true)
    }

}

data class ScoreCard(
    val mRepaintScreen: Boolean = false,
    val mCourseName: String = "",    // current course name from the course list database
    val mCurrentHole: Int = 0,      // the current hole being played in the game
    val mWhatNineIsBeingDisplayed: Boolean = FRONT_NINE_IS_DISPLAYED,
    val rowHeadings: List<RowHeading> = listOf(
        RowHeading(HDCP_HEADER, "HdCp"),
        RowHeading(PAR_HEADER, "Par"),
        RowHeading(HOLE_HEADER, "Hole"),
    ),
    val rowHeaderCells: List<ScoreCardCell> = listOf(
        ScoreCardCell(HDCP_CELL, IntArray(18) { i -> i + 1 }), // Handicap
        ScoreCardCell(PAR_CELL, IntArray(18) { 4 }),          // Par
        ScoreCardCell(HOLE_CELL, IntArray(18) { i -> i + 1 }), // hole
    ),
    val rowHeaderTotals: List<RowHeading> = listOf(
        RowHeading(HDCP_TOTAL, ""),         // Handicap - blank
        RowHeading(PAR_TOTAL, ""),       // determine by the score card record
        RowHeading(TOTAL_TOTAL, "Total"),
    ),

    var rowPlayerNames: List<RowHeading> = emptyList(),

    val rowPlayerCells: List<ScoreCardCell> = listOf(
        ScoreCardCell(PLAYER_1_CELL, IntArray(18) { 5 }),
        ScoreCardCell(PLAYER_2_CELL, IntArray(18) { 3 }),
        ScoreCardCell(PLAYER_3_CELL, IntArray(18) { 5 }),
        ScoreCardCell(PLAYER_4_CELL, IntArray(18) { 3 })
    ),
    val rowPlayerTotals: List<RowHeading> = listOf(
        // determine by the score card record
        RowHeading(PLAYER_1_TOTAL, "40"),
        RowHeading(PLAYER_2_TOTAL, "45"),
        RowHeading(PLAYER_3_TOTAL, "36"),
        RowHeading(PLAYER_4_TOTAL, "43"),
    ),

    val rowTeams: List<RowHeading> = listOf(
        RowHeading(TEAM_HEADER, "Team"),
        RowHeading(USED_HEADER, "Used"),
    ),

    val rowTeamCells: List<ScoreCardCell> = listOf(
        ScoreCardCell(TEAM_CELL, IntArray(18) { 5 }),
        ScoreCardCell(USED_CELL, IntArray(18) { 3 }),
    ),
    val rowTeamTotals: List<RowHeading> = listOf(
        // determine by the score card record
        RowHeading(TEAM_TOTAL, "40"),
        RowHeading(USED_TOTAL, "45"),
    )
)


data class RowHeading(
    val vinTag: Int,
    var mName: String = ""
)

data class ScoreCardCell(
    val vinTag: Int,
    var mHole: IntArray = IntArray(18),
)