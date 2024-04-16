package com.golfpvcc.teamscore_rev4.ui.screens.scorecard

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.golfpvcc.teamscore_rev4.TeamScoreCardApp
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardWithPlayers
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore.DialogAction
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore.getTeamButtonColor
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore.teamScoreTypeNet
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.HDCP_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.HOLE_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.PAR_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.ScoreCardActions
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.TEAM_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.USED_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.getPlayerStrokesForHole
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.highLiteTotalColumn
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.setBoardColorForPlayerTeamScore
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.totalScore
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.updateNetAndGrossScoreCells
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.updateScoreCardState
import com.golfpvcc.teamscore_rev4.utils.BACK_NINE_DISPLAY
import com.golfpvcc.teamscore_rev4.utils.BACK_NINE_IS_DISPLAYED
import com.golfpvcc.teamscore_rev4.utils.BACK_NINE_TOTAL_DISPLAYED

import com.golfpvcc.teamscore_rev4.utils.Constants.SCORE_CARD_REC_ID
import com.golfpvcc.teamscore_rev4.utils.DOUBLE_TEAM_SCORE
import com.golfpvcc.teamscore_rev4.utils.FRONT_NINE_DISPLAY
import com.golfpvcc.teamscore_rev4.utils.FRONT_NINE_IS_DISPLAYED
import com.golfpvcc.teamscore_rev4.utils.FRONT_NINE_TOTAL_DISPLAYED
import com.golfpvcc.teamscore_rev4.utils.JUST_RAW_SCORE

import com.golfpvcc.teamscore_rev4.utils.YELLOW_GETS_1_STROKES
import com.golfpvcc.teamscore_rev4.utils.ORANGE_GETS_2_STROKES
import com.golfpvcc.teamscore_rev4.utils.PURPLE_TWO_UNDER_PAR
import com.golfpvcc.teamscore_rev4.utils.RED_ONE_UNDER_PAR
import com.golfpvcc.teamscore_rev4.utils.TEAM_CLEAR_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_GROSS_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_NET_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_SCORE_MASK
import com.golfpvcc.teamscore_rev4.utils.TOTAL_18_HOLE
import com.golfpvcc.teamscore_rev4.utils.VIN_LIGHT_GRAY
import com.golfpvcc.teamscore_rev4.utils.DISPLAY_HOLE_NUMBER

open class ScoreCardViewModel() : ViewModel() {
    var state by mutableStateOf(ScoreCard())
    private val scoreCardDao = TeamScoreCardApp.getScoreCardDao()

    class ScoreCardViewModelFactor() : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ScoreCardViewModel() as T
        }
    }

    fun getScoreCardAndPlayerRecord() {
        val scoreCardWithPlayers: List<ScoreCardWithPlayers> =
            scoreCardDao.getScoreRecordWithPlayers(SCORE_CARD_REC_ID)

        if (scoreCardWithPlayers.isNotEmpty()) {     // found score record with players
            updateScoreCardState(scoreCardWithPlayers[0])       // located in helper function file
        } else
            Log.d("VIN1", "getScoreCardAndPlayerRecord is empty")
    }

    fun scoreCardActions(action: ScoreCardActions) {
        when (action) {
            ScoreCardActions.Next -> advanceToTheNextHole()
            ScoreCardActions.Prev -> advanceToThePreviousHole()
        }

    }

    fun getPlayerHoleScore(playerIdx: Int, idx: Int): String {
        val playerHoleScore: Int = (state.playerHeading[playerIdx].mScore[idx] and JUST_RAW_SCORE)
        var displayPlayerHoleScore: String = "  "

        if (playerHoleScore != 0)
            displayPlayerHoleScore = playerHoleScore.toString()

        return (displayPlayerHoleScore)
    }

    fun getHoleHdcps(): IntArray {
        val hdcpCell: HdcpParHoleHeading? =
            state.hdcpParHoleHeading.find { it.vinTag == HDCP_HEADER }
        if (hdcpCell != null) {
            return (hdcpCell.mHole)
        } else
            return (IntArray(18) { 0 })
    }

    fun getHoleHandicap(hole: Int): Int {
        val hdcpCell: HdcpParHoleHeading? =
            state.hdcpParHoleHeading.find { it.vinTag == HDCP_HEADER }
        if (hdcpCell != null) {
            return (hdcpCell.mHole[hole])
        } else
            return (0)
    }

    fun getStartingHole(): Int {    // true for front nine being displayed
        return if (state.mWhatNineIsBeingDisplayed) 0 else FRONT_NINE_DISPLAY
    }

    fun getEndingHole(): Int {
        return if (state.mWhatNineIsBeingDisplayed) FRONT_NINE_DISPLAY else BACK_NINE_DISPLAY
    }

    fun getStokeOnHolePlayerColor(playerHdcp: String, currentHole: Int): Color {
        var backGroundColor: Color = Color.Transparent
        val holeHdcp = state.hdcpParHoleHeading.find { it.vinTag == HDCP_HEADER }
        if (holeHdcp != null) {
            val strokeForHole = getPlayerStrokesForHole(holeHdcp, playerHdcp, currentHole)

            when (strokeForHole) {
                1 -> {
                    backGroundColor = Color(YELLOW_GETS_1_STROKES)
                }

                2 -> {
                    backGroundColor = Color(ORANGE_GETS_2_STROKES)
                }
            }
        }

        return (backGroundColor)
    }

    fun getPlayerScoreColorForHole(playerScore: Int, holePar: Int): Color {
        val score: Int = (playerScore and JUST_RAW_SCORE) - holePar
        var backGroundColor: Color = Color.Black
        when (score) {
            -1 -> backGroundColor = Color(RED_ONE_UNDER_PAR)
            -2 -> backGroundColor = Color(PURPLE_TWO_UNDER_PAR)
        }
        return backGroundColor
    }

    fun getTeamColorForHole(playerScore: Int): Color {
        return setBoardColorForPlayerTeamScore(playerScore)
    }

    fun getTotalForNineCell(holes: IntArray): String {
        val startingCell: Int = getStartingHole()
        val endingCell: Int = getEndingHole()
        val totalScoreStr = totalScore(holes, startingCell, endingCell)    // helper functions file

        return (totalScoreStr)
    }

    fun getCurrentHole(): Int {
        return (state.mCurrentHole)
    }

    private fun repaintScreen() {
        state = state.copy(mRepaintScreen = !state.mRepaintScreen)
    }

    fun flipFrontNineBackNine() {
        state = state.copy(mWhatNineIsBeingDisplayed = !state.mWhatNineIsBeingDisplayed)
    }

    fun advanceToTheNextHole() { // zero base, user is one the 9 nine hole
        state = if ((state.mCurrentHole + 1) < TOTAL_18_HOLE) {
            state.copy(mCurrentHole = (state.mCurrentHole + 1))
        } else {
            state.copy(mCurrentHole = 0)
        }
        displayFrontOrBackOfScoreCard()
    }


    fun advanceToThePreviousHole() {
        Log.d("Vin", "current hole ${state.mCurrentHole}")
        state = if (0 <= (state.mCurrentHole - 1)) {
            state.copy(mCurrentHole = (state.mCurrentHole - 1))
        } else {
            state.copy(mCurrentHole = BACK_NINE_TOTAL_DISPLAYED)
        }
        displayFrontOrBackOfScoreCard()
    }

    fun setHighLightCurrentHole(holeIdx: Int, displayHoleColor: Color): Color {
        if (displayHoleColor == Color(DISPLAY_HOLE_NUMBER)) { // the only row to high light on the score card
            return if (holeIdx == state.mCurrentHole) Color(DISPLAY_HOLE_NUMBER) else Color(
                VIN_LIGHT_GRAY
            )
        } else {
            return displayHoleColor
        }
    }

    private fun setHoleScore(playerIdx: Int, idx: Int, score: Int) {
        state.playerHeading[playerIdx].mScore[idx] = score
    }

    private fun displayFrontOrBackOfScoreCard() {
        if (state.mCurrentHole < FRONT_NINE_DISPLAY) {
            state = state.copy(mWhatNineIsBeingDisplayed = FRONT_NINE_IS_DISPLAYED)
        } else
            state = state.copy(mWhatNineIsBeingDisplayed = BACK_NINE_IS_DISPLAYED)
    }

    // *******************************************************
//  Dialog Enter player scores function are below
    fun dialogAction(action: DialogAction) {
        when (action) {
            DialogAction.Done -> doneScoringDialog()
            DialogAction.Clear -> clearOneScore()
            DialogAction.ButtonEnterScore -> buttonEnterScore()
            is DialogAction.Gross -> setGrossScore(action.playerIdx)
            is DialogAction.GrossLongClick -> setGrossLongClickScore(action.playerIdx)
            is DialogAction.Net -> setNetScore(action.playerIdx)
            is DialogAction.NetLongClick -> setNetLongClickScore(action.playerIdx)
            is DialogAction.Number -> scoreEnter(action.score)
            is DialogAction.SetDialogCurrentPlayer -> setDialogCurrentPlayer(action.currentPlayerIdx)
        }
    }

    fun buttonEnterScore() {
        Log.d("VIN", "ButtonEnterScore")
        state = state.copy(mDialogDisplayed = true)
        if (state.mShowTotals) {
            advanceToTheNextHole()  //user looking at the total scores
            state = state.copy(mShowTotals = false)
            highLiteTotalColumn(VIN_LIGHT_GRAY)
        }
        displayFrontOrBackOfScoreCard()
        configureDialogGrossAndNetButtonColors()
    }

    private fun doneScoringDialog() {
        Log.d("HOLE", "doneScoringDialog current hole ${state.mCurrentHole}")
        state = state.copy(mDialogDisplayed = false)
        updateNetAndGrossScoreCells(
            state.hdcpParHoleHeading,
            state.mCurrentHole,
            state.teamUsedHeading,
            state.playerHeading
        )
        clearGrossAndNetButtons()   // clear the color button array
        if (FRONT_NINE_TOTAL_DISPLAYED == state.mCurrentHole || BACK_NINE_TOTAL_DISPLAYED == state.mCurrentHole) {     // let the user see the totals scores
            state = state.copy(mShowTotals = true)
            highLiteTotalColumn(DISPLAY_HOLE_NUMBER)
        } else {
            advanceToTheNextHole()
        }
    }

    private fun configureDialogGrossAndNetButtonColors() {
        val holeIdx = getCurrentHole()
        var playerScore: Int

        for (playerHeadRecord in state.playerHeading) {
            playerScore = playerHeadRecord.mScore[holeIdx]
            getGrossButtonColor(playerHeadRecord.vinTag, playerScore)
        }
    }

    private fun setGrossScore(playerIdx: Int) {
        setTeamScoreButtonColor(playerIdx, TEAM_GROSS_SCORE)
    }

    private fun setGrossLongClickScore(playerIdx: Int) {
        setTeamScoreButtonColor(playerIdx, (TEAM_GROSS_SCORE + DOUBLE_TEAM_SCORE))
    }

    private fun setNetScore(playerIdx: Int) {
        setTeamScoreButtonColor(playerIdx, TEAM_NET_SCORE)
    }

    private fun setNetLongClickScore(playerIdx: Int) {
        setTeamScoreButtonColor(playerIdx, TEAM_NET_SCORE + DOUBLE_TEAM_SCORE)
    }

    private fun setTeamScoreButtonColor(playerIdx: Int, teamScore: Int) {
        val holeIdx = getCurrentHole()
        val teamHoleMask: Int
        val playerHeadRecord = state.playerHeading[playerIdx]

        var playerScore = playerHeadRecord.mScore[holeIdx]
        teamHoleMask = playerScore and TEAM_SCORE_MASK
        playerScore = playerScore and TEAM_CLEAR_SCORE

        if ((teamHoleMask and teamScore) != teamScore) {  // if the team score already set, then clear the score
            playerScore = playerScore or teamScore
        }
        playerHeadRecord.mScore[holeIdx] = playerScore
        Log.d("VIN", "setGrossScore playerScore $playerScore")
        getGrossButtonColor(playerHeadRecord.vinTag, playerScore)
    }

    private fun getGrossButtonColor(playerIdx: Int, playerScore: Int) {

        if (teamScoreTypeNet(playerScore)) {
            state.grossButtonColor[playerIdx] = Color.LightGray
            state.netButtonColor[playerIdx] = getTeamButtonColor(playerScore)
        } else {
            state.grossButtonColor[playerIdx] = getTeamButtonColor(playerScore)
            state.netButtonColor[playerIdx] = Color.LightGray
        }
        repaintScreen()
    }

    private fun clearGrossAndNetButtons() {
        for (idx in state.grossButtonColor.indices) {
            state.grossButtonColor[idx] = Color.LightGray
        }
        for (idx in state.netButtonColor.indices) {
            state.netButtonColor[idx] = Color.LightGray
        }
    }


    private fun clearOneScore() {
        scoreEnter(0)
        Log.d("VIN", "clearOneScore")
    }

    fun scoreEnter(score: Int) {
        var dialogCurrentPlayer: Int = getDialogCurrentPlayer()

        setHoleScore(dialogCurrentPlayer, getCurrentHole(), score)
        dialogCurrentPlayer += 1
        if (dialogCurrentPlayer < state.playerHeading.size)
            setDialogCurrentPlayer(dialogCurrentPlayer) // on to the next player
        else
            repaintScreen()
    }

    fun setDialogCurrentPlayer(idx: Int) {
        state.mDialogCurrentPlayer = idx
        Log.d("VIN", "setDialogCurrentPlayer is $idx")
        repaintScreen()
    }

    private fun getDialogCurrentPlayer(): Int {
        return (state.mDialogCurrentPlayer)
    }

    fun getHighLiteActivePlayerColor(idx: Int): Color {
        return if (idx == state.mDialogCurrentPlayer) {
            Color.Yellow
        } else {
            Color.Transparent
        }
    }
}

data class ScoreCard(
    val grossButtonColor: Array<Color> = Array(4) { Color.LightGray },
    val netButtonColor: Array<Color> = Array(4) { Color.LightGray },
    val mRepaintScreen: Boolean = false,
    val mShowTotals: Boolean = false,
    val mCourseName: String = "",    // current course name from the course list database
    val mDialogDisplayed: Boolean = false,
    var mDialogCurrentPlayer: Int = 0,
    val mCurrentHole: Int = 0,      // the current hole being played in the game
    val mWhatNineIsBeingDisplayed: Boolean = FRONT_NINE_IS_DISPLAYED,
    val hdcpParHoleHeading: List<HdcpParHoleHeading> = listOf(
        HdcpParHoleHeading(HDCP_HEADER, "HdCp"),
        HdcpParHoleHeading(PAR_HEADER, "Par"),
        HdcpParHoleHeading(HOLE_HEADER, mName = "Hole", mTotal = "Total"),
    ),

    var playerHeading: List<PlayerHeading> = emptyList(),

    val teamUsedHeading: List<TeamUsedHeading> = listOf(
        TeamUsedHeading(TEAM_HEADER, "Team"),   // total hole score for selected player
        TeamUsedHeading(USED_HEADER, "Used"),   // total players selected for this hole
    ),
)

data class HdcpParHoleHeading(
    val vinTag: Int = 0,
    var mName: String = "",
    var mHole: IntArray = IntArray(18) { i -> i + 1 },
    var mTotal: String = "",
    var mColor: Color = Color(VIN_LIGHT_GRAY)
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
