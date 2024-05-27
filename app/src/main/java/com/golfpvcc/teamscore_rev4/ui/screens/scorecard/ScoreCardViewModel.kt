package com.golfpvcc.teamscore_rev4.ui.screens.scorecard
// new commit with luach fix
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.golfpvcc.teamscore_rev4.TeamScoreCardApp
import com.golfpvcc.teamscore_rev4.database.model.PlayerRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardWithPlayers
import com.golfpvcc.teamscore_rev4.ui.screens.playerStokes
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore.DialogAction
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore.getTeamButtonColor
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore.teamScoreTypeNet
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.DISPLAY_MODE_GROSS
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.HDCP_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.HOLE_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.PAR_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.ScoreCardActions
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.TEAM_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.USED_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.changeDisplayScreenMode
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.getTotalScore
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.highLiteTotalColumn
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.refreshScoreCard
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.screenModeText
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.setBoardColorForPlayerTeamScore
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.updatePlayersTeamScoreCells
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.updateScoreCardState
import com.golfpvcc.teamscore_rev4.utils.BACK_NINE_DISPLAY
import com.golfpvcc.teamscore_rev4.utils.BACK_NINE_IS_DISPLAYED
import com.golfpvcc.teamscore_rev4.utils.BACK_NINE_TOTAL_DISPLAYED

import com.golfpvcc.teamscore_rev4.utils.Constants.SCORE_CARD_REC_ID
import com.golfpvcc.teamscore_rev4.utils.DOUBLE_TEAM_SCORE
import com.golfpvcc.teamscore_rev4.utils.FRONT_NINE_DISPLAY
import com.golfpvcc.teamscore_rev4.utils.FRONT_NINE_IS_DISPLAYED
import com.golfpvcc.teamscore_rev4.utils.FRONT_NINE_TOTAL_DISPLAYED
import com.golfpvcc.teamscore_rev4.utils.YELLOW_GETS_1_STROKES
import com.golfpvcc.teamscore_rev4.utils.ORANGE_GETS_2_STROKES
import com.golfpvcc.teamscore_rev4.utils.PURPLE_TWO_UNDER_PAR
import com.golfpvcc.teamscore_rev4.utils.RED_ONE_UNDER_PAR
import com.golfpvcc.teamscore_rev4.utils.TEAM_GROSS_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_NET_SCORE
import com.golfpvcc.teamscore_rev4.utils.TOTAL_18_HOLE
import com.golfpvcc.teamscore_rev4.utils.VIN_LIGHT_GRAY
import com.golfpvcc.teamscore_rev4.utils.DISPLAY_HOLE_NUMBER
import com.golfpvcc.teamscore_rev4.utils.HOLE_ARRAY_SIZE
import com.golfpvcc.teamscore_rev4.utils.TEAM_CLEAR_SCORE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class ScoreCardViewModel() : ViewModel() {
    var state by mutableStateOf(ScoreCard())
    private val scoreCardDao = TeamScoreCardApp.getScoreCardDao()
    private val playerRecordDoa = TeamScoreCardApp.getPlayerDao()

    class ScoreCardViewModelFactor() : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ScoreCardViewModel() as T
        }
    }

    fun getScoreCardAndPlayerRecord() {
        Log.d("VIN1", "getScoreCardAndPlayerRecord read records")
        if (!state.mHasDatabaseBeenRead) {
            state.mHasDatabaseBeenRead = true   // only read the database once
            val scoreCardWithPlayers: ScoreCardWithPlayers =
                scoreCardDao.getScoreRecordWithPlayers(SCORE_CARD_REC_ID)

            if (null != scoreCardWithPlayers) {     // found score record with players
                updateScoreCardState(scoreCardWithPlayers)       // located in helper function file
            } else
                Log.d("VIN1", "getScoreCardAndPlayerRecord is empty")

//
//            state.mGamePointsTable = setUpPointTable(pointRecord)
//            Log.d("VIN1", "getScoreCardAndPlayerRecord pt")
        }
    }

//    fun setUpPointTable(pointRec: String): List<PtTable> {
//        val GamePointsTable:List<PtTable>  = emptyList<PtTable>()
//
//        return (GamePointsTable)
//    }

    private fun savePlayersScoresRecord() {
        viewModelScope.launch(Dispatchers.IO) {
            saveScoreCardRecord()
            for (playerHeadRecord in state.playerHeading) {
                val playerRecord = PlayerRecord(
                    mName = playerHeadRecord.mName,
                    mHandicap = playerHeadRecord.mHdcp,
                    mScore = playerHeadRecord.mScore,
                    mTeamHole = playerHeadRecord.mTeamHole,
                    mScoreCardRecFk = SCORE_CARD_REC_ID,
                    mId = playerHeadRecord.vinTag
                )
                playerRecordDoa.addUpdatePlayerRecord(playerRecord)
            }
        }
    }

    private suspend fun saveScoreCardRecord() {
        val hdcpCells = getHoleHdcpCells()
        val parCells = getHoleParCells()

        val scoreCardRecord: ScoreCardRecord = ScoreCardRecord(
            mCourseName = state.mCourseName,
            mTee = state.mTee,
            mCurrentHole = state.mCurrentHole,
            mCourseId = state.mCourseId,
            mPar = parCells,
            mHandicap = hdcpCells,
            mScoreCardRecId = SCORE_CARD_REC_ID
        )
        scoreCardDao.addUpdateScoreCardRecord(scoreCardRecord)
    }

    fun scoreCardActions(action: ScoreCardActions) {
        when (action) {
            ScoreCardActions.Next -> advanceToTheNextHole()
            ScoreCardActions.Prev -> advanceToThePreviousHole()
            ScoreCardActions.ScreenMode -> changeScreenMode()
            ScoreCardActions.ButtonEnterScore -> buttonEnterScore()
            ScoreCardActions.SetDialogCurrentPlayer -> setDialogCurrentPlayer(0)
            ScoreCardActions.FlipFrontBackNine -> flipFrontNineBackNine()
        }
    }

    fun changeScreenMode() {
        state = state.copy(mDisplayScreenModeText = screenModeText(state.mDisplayScreenMode))
        Log.d("VIN", "before mDisplayScreenModeText  ${state.mDisplayScreenModeText}")

        state.mDisplayScreenMode = changeDisplayScreenMode(state.mDisplayScreenMode)
        val parCells = getHoleParCells()
        refreshScoreCard(parCells)
        state =
            state.copy(mButtonScreenNextText = screenModeText(state.mDisplayScreenMode))  // change the button text now
    }

    fun getPlayerHoleScore(
        playerIdx: Int,
        idx: Int,
    ): String { //display the player's score on the score card
        val playerHoleScore: Int =
            (state.playerHeading[playerIdx].mDisplayScore[idx])
        var displayPlayerHoleScore: String = "  "

        if (playerHoleScore != 0)
            displayPlayerHoleScore = playerHoleScore.toString()
        Log.d("VIN", "getPlayerHoleScore Player's score $displayPlayerHoleScore")
        return (displayPlayerHoleScore)
    }

    fun getTeamUsedCells(): IntArray {
        val teamUsedCells =
            state.teamUsedHeading.find { it.vinTag == USED_HEADER }
        return if (teamUsedCells != null) {
            (teamUsedCells.mHole)
        } else
            (IntArray(HOLE_ARRAY_SIZE) { 0 })
    }

    fun getTeamPlayerScoreCells(): IntArray {
        val teamUsedCells =
            state.teamUsedHeading.find { it.vinTag == TEAM_HEADER }
        return if (teamUsedCells != null) {
            (teamUsedCells.mHole)
        } else
            (IntArray(HOLE_ARRAY_SIZE) { 0 })
    }

    fun getHoleHdcpCells(): IntArray {
        val hdcpCell: HdcpParHoleHeading? =
            state.hdcpParHoleHeading.find { it.vinTag == HDCP_HEADER }
        return if (hdcpCell != null) {
            (hdcpCell.mHole)
        } else
            (IntArray(HOLE_ARRAY_SIZE) { 0 })
    }

    fun getHoleHandicap(hole: Int): Int {
        val hdcpCells = getHoleHdcpCells()
        return (hdcpCells[hole])
    }

    fun getHoleParCells(): IntArray {
        val parCell: HdcpParHoleHeading? =
            state.hdcpParHoleHeading.find { it.vinTag == PAR_HEADER }
        return if (parCell != null) {
            (parCell.mHole)
        } else
            (IntArray(HOLE_ARRAY_SIZE) { 0 })
    }

    fun getParForHole(hole: Int): Int {
        val parCells = getHoleParCells()
        return (parCells[hole])
    }

    fun getStartingHole(): Int {    // true for front nine being displayed
        return if (state.mWhatNineIsBeingDisplayed) 0 else FRONT_NINE_DISPLAY
    }

    fun getEndingHole(): Int {
        return if (state.mWhatNineIsBeingDisplayed) FRONT_NINE_DISPLAY else BACK_NINE_DISPLAY
    }

    fun getStokeOnHolePlayerColor(playerDisplayHole: Int): Color {
        var backGroundColor: Color = Color.Transparent
        val strokesForHole = playerStokes(playerDisplayHole)

        when (strokesForHole) {
            1 -> {
                backGroundColor = Color(YELLOW_GETS_1_STROKES)
            }

            2 -> {
                backGroundColor = Color(ORANGE_GETS_2_STROKES)
            }
        }
        return (backGroundColor)
    }

    fun getPlayerScoreColorForHole(grossScore: Int, holePar: Int): Color {
        val score: Int = grossScore - holePar
        var backGroundColor: Color = Color.Black
        when (score) {
            -1 -> backGroundColor = Color(RED_ONE_UNDER_PAR)
            -2 -> backGroundColor = Color(PURPLE_TWO_UNDER_PAR)
        }
        Log.d("SCORE", "Score $score")
        return backGroundColor
    }

    fun getTeamColorForHole(teamScore: Int): Color {
        return setBoardColorForPlayerTeamScore(teamScore)
    }

    fun getTotalForNineCell(holes: IntArray): String {
        val startingCell: Int = getStartingHole()
        val endingCell: Int = getEndingHole()     // helper functions file
        val totalScoreStr = getTotalScore(holes, startingCell, endingCell)

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
        if (state.mShowTotals) {
            state = state.copy(mShowTotals = false)
            highLiteTotalColumn(VIN_LIGHT_GRAY)
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
        if (state.mShowTotals) {
            state = state.copy(mShowTotals = false)
            highLiteTotalColumn(VIN_LIGHT_GRAY)
        }
        displayFrontOrBackOfScoreCard()
    }

    fun setHighLightCurrentHole(holeIdx: Int, displayHoleColor: Color): Color {
        if (displayHoleColor == Color(DISPLAY_HOLE_NUMBER)) { // the only row to high light on the score card
            return if (holeIdx == state.mCurrentHole && !state.mShowTotals) Color(
                DISPLAY_HOLE_NUMBER
            ) else Color(
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
            is DialogAction.JunkClick -> displayJunkDialog()
            DialogAction.DisplayHoleNote -> displayHoleNote()
        }
    }

    fun displayJunkDialog() {   //  Dialog Enter player scores function are below
        Log.d("VIN", "displayJunkDialog")
    }

    fun displayHoleNote() { //  Dialog Enter player scores function are below
        Log.d("VIN", "displayHoleNotee")
    }

    fun getDialogPlayerHoleScore(
        playerIdx: Int,
        idx: Int,
    ): String { //display the player's score on the dialog window
        val playerHoleScore: Int =
            (state.playerHeading[playerIdx].mScore[idx])
        var displayPlayerHoleScore: String = "  "

        if (playerHoleScore != 0)
            displayPlayerHoleScore = playerHoleScore.toString()
        Log.d("VIN", "getDialogPlayerHoleScore Player's score $displayPlayerHoleScore")
        return (displayPlayerHoleScore)
    }

    fun buttonEnterScore() { //  Dialog Enter player scores function are below
        Log.d("VIN", "ButtonEnterScore")
        state = state.copy(mDialogDisplayed = true)
        if (state.mShowTotals) {
            advanceToTheNextHole()  //user looking at the total scores
            highLiteTotalColumn(VIN_LIGHT_GRAY)
        }
        displayFrontOrBackOfScoreCard()
        configureDialogGrossAndNetButtonColors()
    }

    private fun doneScoringDialog() { //  Dialog Enter player scores function are below
        Log.d("HOLE", "doneScoringDialog current hole ${state.mCurrentHole}")
        state = state.copy(mDialogDisplayed = false)
        updatePlayersTeamScoreCells( // doneScoringDialog
            state.mDisplayScreenMode,
            state.mCurrentHole,
            getParForHole(state.mCurrentHole),
            state.playerHeading
        )
        clearGrossAndNetButtons()   // clear the color button array
        savePlayersScoresRecord()
        if (FRONT_NINE_TOTAL_DISPLAYED == state.mCurrentHole || BACK_NINE_TOTAL_DISPLAYED == state.mCurrentHole) {     // let the user see the totals scores
            state = state.copy(mShowTotals = true)
            highLiteTotalColumn(DISPLAY_HOLE_NUMBER)
        } else {
            state = state.copy(mShowTotals = false)
            advanceToTheNextHole()
        }
    }

    private fun configureDialogGrossAndNetButtonColors() { //  Dialog Enter player scores function are below
        val holeIdx = getCurrentHole()
        var teamHoleMask: Int

        for (playerHeadRecord in state.playerHeading) {
            teamHoleMask = playerHeadRecord.mTeamHole[holeIdx]
            if (0 < teamHoleMask)
                getGrossButtonColor(playerHeadRecord.vinTag, teamHoleMask)
        }
    }

    private fun setGrossScore(playerIdx: Int) { //  Dialog Enter player scores function are below
        setTeamScoreButtonColor(playerIdx, TEAM_GROSS_SCORE)
    }

    private fun setGrossLongClickScore(playerIdx: Int) { //  Dialog Enter player scores function are below
        setTeamScoreButtonColor(playerIdx, (TEAM_GROSS_SCORE + DOUBLE_TEAM_SCORE))
    }

    private fun setNetScore(playerIdx: Int) { //  Dialog Enter player scores function are below
        setTeamScoreButtonColor(playerIdx, TEAM_NET_SCORE)
    }

    private fun setNetLongClickScore(playerIdx: Int) { //  Dialog Enter player scores function are below
        setTeamScoreButtonColor(playerIdx, TEAM_NET_SCORE + DOUBLE_TEAM_SCORE)
    }

    private fun setTeamScoreButtonColor(
        playerIdx: Int,
        teamScoreMask: Int,
    ) { //  Dialog Enter player scores function are below
        val currentHole = getCurrentHole()
        var currentTeamMask = state.playerHeading[playerIdx].mTeamHole[currentHole]

        state.grossButtonColor[playerIdx] = Color.LightGray
        state.netButtonColor[playerIdx] = Color.LightGray
        state.playerHeading[playerIdx].mTeamHole[currentHole] = TEAM_CLEAR_SCORE
        if (currentTeamMask != teamScoreMask) {     // disable the selection if equal
            getGrossButtonColor(playerIdx, teamScoreMask)
            state.playerHeading[playerIdx].mTeamHole[currentHole] = teamScoreMask
        }
        repaintScreen()
    }

    private fun getGrossButtonColor(
        playerIdx: Int,
        teamHoleMask: Int,
    ) { //  Dialog Enter player scores function are below

        if (teamScoreTypeNet(teamHoleMask)) {
            state.grossButtonColor[playerIdx] = getTeamButtonColor(teamHoleMask)
            state.netButtonColor[playerIdx] = Color.LightGray
        } else {
            state.grossButtonColor[playerIdx] = Color.LightGray
            state.netButtonColor[playerIdx] = getTeamButtonColor(teamHoleMask)
        }
    }

    private fun clearGrossAndNetButtons() { //  Dialog Enter player scores function are below
        for (idx in state.grossButtonColor.indices) {
            state.grossButtonColor[idx] = Color.LightGray
        }
        for (idx in state.netButtonColor.indices) {
            state.netButtonColor[idx] = Color.LightGray
        }
    }

    private fun clearOneScore() { //  Dialog Enter player scores function are below
        scoreEnter(0)
        Log.d("VIN", "clearOneScore")
    }

    fun scoreEnter(score: Int) {    //  Dialog Enter player scores function are below
        var dialogCurrentPlayer: Int = getDialogCurrentPlayer()

        setHoleScore(dialogCurrentPlayer, getCurrentHole(), score)
        dialogCurrentPlayer += 1
        if (dialogCurrentPlayer < state.playerHeading.size)
            setDialogCurrentPlayer(dialogCurrentPlayer) // on to the next player
        else
            repaintScreen()
    }

    fun setDialogCurrentPlayer(idx: Int) {  //  Dialog Enter player scores function are below
        state.mDialogCurrentPlayer = idx
        Log.d("VIN", "setDialogCurrentPlayer is $idx")
        repaintScreen()
    }

    private fun getDialogCurrentPlayer(): Int { //  Dialog Enter player scores function are below
        return (state.mDialogCurrentPlayer)
    }

    fun getHighLiteActivePlayerColor(idx: Int): Color { //  Dialog Enter player scores function are below
        return if (idx == state.mDialogCurrentPlayer) {
            Color.Yellow
        } else {
            Color.Transparent
        }
    }
}

data class ScoreCard(
//    var mGamePointsTable: List<PtTable> = emptyList(),
    var mHasDatabaseBeenRead: Boolean = false,
    val grossButtonColor: Array<Color> = Array(4) { Color.LightGray },
    val netButtonColor: Array<Color> = Array(4) { Color.LightGray },
    val junkButtonColor: Array<Color> = Array(4) { Color.LightGray },
    var mDisplayScreenMode: Int = DISPLAY_MODE_GROSS,
    val mDisplayScreenModeText: String = "Gross",        // what is being display now
    val mButtonScreenNextText: String = "Net",             // what will be display next
    val mRepaintScreen: Boolean = false,
    val mShowTotals: Boolean = false,
    val mCourseName: String = "",    // current course name from the course list database
    val mCourseId: Int = 0,
    val mTee: String = "",                   // the tee's played or the course yardage
    val mDialogDisplayed: Boolean = false,
    var mDialogCurrentPlayer: Int = 0,
    val mCurrentHole: Int = 0,      // the current hole being played in the game
    val mWhatNineIsBeingDisplayed: Boolean = FRONT_NINE_IS_DISPLAYED,
    val hdcpParHoleHeading: List<HdcpParHoleHeading> = listOf(
        HdcpParHoleHeading(HDCP_HEADER, "HdCp", mTotal = "Notes"),
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
    var mHole: IntArray = IntArray(HOLE_ARRAY_SIZE) { i -> i + 1 },
    var mTotal: String = "",
    var mColor: Color = Color(VIN_LIGHT_GRAY),
)

data class PlayerHeading(
    val vinTag: Int = 0,
    var mHdcp: String = "",     // not used on the screen
    var mName: String = "",
    var mGameNines: Boolean,    // true if we only have 3 players
    var mScore: IntArray = IntArray(HOLE_ARRAY_SIZE),   // gross scores
    var mDisplayScore: IntArray = IntArray(HOLE_ARRAY_SIZE) { 0 },
    var mStokeHole: IntArray = IntArray(HOLE_ARRAY_SIZE) { 0 },
    var mTeamHole: IntArray = IntArray(HOLE_ARRAY_SIZE),  // used for team scoring
    var mTotal: String = "",
)

data class TeamUsedHeading(
    val vinTag: Int = 0,
    var mName: String = "",
    var mHole: IntArray = IntArray(HOLE_ARRAY_SIZE) { 0 },
    var mTotal: String = "",
)

