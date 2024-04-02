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
import com.golfpvcc.teamscore_rev4.utils.GETS_1_STROKES
import com.golfpvcc.teamscore_rev4.utils.GETS_2_STROKES
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

        if (0 < scoreCardWithPlayers.size)      // found score record with players
            updateScoreCardRecord(scoreCardWithPlayers[0])
    }

    fun updateScoreCardRecord(scoreCardWithPlayers: ScoreCardWithPlayers) {
        val scoreCardRecord: ScoreCardRecord = scoreCardWithPlayers.scoreCardRecord

        state = state.copy(mCourseName = scoreCardRecord.mCourseName)
        state = state.copy(mCurrentHole = (scoreCardRecord.mCurrentHole - 1))   // zero based

        val parCell: HdcpParHoleHeading? = state.hdcpParHoleHeading.find { it.vinTag == PAR_HEADER }
        if (parCell != null) {
            parCell.mHole = scoreCardRecord.mPar
        }
        val hdcpCell: HdcpParHoleHeading? =
            state.hdcpParHoleHeading.find { it.vinTag == HDCP_HEADER }
        if (hdcpCell != null) {
            hdcpCell.mHole = scoreCardRecord.mHandicap
        }
        for (idx in scoreCardWithPlayers.playerRecords.indices) { // player name and handicap
            state.playerHeading += PlayerHeading(
                PLAYER_1_NAME + idx,
                mName = scoreCardWithPlayers.playerRecords[idx].mName,
                mHdcp = scoreCardWithPlayers.playerRecords[idx].mHandicap,
                mScore = scoreCardWithPlayers.playerRecords[idx].mScore
            ) // add the player's name to the score card
        }
    }

    fun getHoleHdcps(): IntArray {
        val hdcpCell: HdcpParHoleHeading? =
            state.hdcpParHoleHeading.find { it.vinTag == HDCP_HEADER }
        if (hdcpCell != null) {
            return (hdcpCell.mHole)
        } else
            return (IntArray(18) { 0 })
    }

    fun getStartingHole(): Int {
        return if (state.mWhatNineIsBeingDisplayed) 0 else FRONT_NINE_DISPLAY
    }

    fun getEndingHole(): Int {
        return if (state.mWhatNineIsBeingDisplayed) FRONT_NINE_DISPLAY else BACK_NINE_DISPLAY
    }

    fun getStokeOnHolePlayerColor(playerHdcp: String, holeHdcp: Int): Color {
        var backGroundColor: Color = Color.Transparent
        var playerIntHdcp = playerHdcp.toInt()

        if (holeHdcp <= playerIntHdcp) {
            backGroundColor = Color(GETS_1_STROKES)
            playerIntHdcp -= 18
            if (holeHdcp <= playerIntHdcp)
                backGroundColor = Color(GETS_2_STROKES)
        }
        return (backGroundColor)
    }

    fun getTotalForNineCell(Holes: IntArray): String {
        var total: Int = 0
        val startingCell: Int = getStartingHole()
        val endingCell: Int = getEndingHole()

        for (idx in startingCell until endingCell) {
            total += Holes[idx]
        }
        Log.d("VIN", "getTotalForNineCell sum $total")
        return if (total == 0) " " else total.toString()
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
        state.playerHeading[playerIdx].mScore[idx] = score
        Log.d("VIN", "SetHoleScore ${state.playerHeading[playerIdx].mScore[idx]}")
        state = state.copy(mRepaintScreen = true)
    }

}

data class ScoreCard(
    val mRepaintScreen: Boolean = false,
    val mCourseName: String = "",    // current course name from the course list database
    val mCurrentHole: Int = 0,      // the current hole being played in the game
    val mWhatNineIsBeingDisplayed: Boolean = FRONT_NINE_IS_DISPLAYED,
    val hdcpParHoleHeading: List<HdcpParHoleHeading> = listOf(
        HdcpParHoleHeading(HDCP_HEADER, "HdCp"),
        HdcpParHoleHeading(PAR_HEADER, "Par"),
        HdcpParHoleHeading(HOLE_HEADER, mName="Hole", mTotal="Total"),
    ),

    var playerHeading: List<PlayerHeading> = emptyList(),

    val teamUsedHeading: List<TeamUsedHeading> = listOf(
        TeamUsedHeading(TEAM_HEADER, "Team"),
        TeamUsedHeading(USED_HEADER, "Used"),
    ),
)

data class HdcpParHoleHeading(
    val vinTag: Int = 0,
    var mName: String = "",
    var mHole: IntArray = IntArray(18) { i -> i + 1 },
    var mTotal: String = "",
)

data class PlayerHeading(
    val vinTag: Int = 0,
    var mHdcp: String = "",     // not used on the screen
    var mName: String = "",
    var mScore: IntArray = IntArray(18),
    var mTotal: String = "",
)

data class TeamUsedHeading(
    val vinTag: Int = 0,
    var mName: String = "",
    var mHole: IntArray = IntArray(18),
    var mTotal: String = "",
)
