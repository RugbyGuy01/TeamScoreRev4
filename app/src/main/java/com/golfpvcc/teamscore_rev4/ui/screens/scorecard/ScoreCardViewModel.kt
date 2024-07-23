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
import com.golfpvcc.teamscore_rev4.database.model.PointsRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardWithPlayers
import com.golfpvcc.teamscore_rev4.ui.screens.playerStokes
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore.DialogAction
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore.getTeamButtonColor
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore.teamScoreTypeNet
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.DISPLAY_MODE_9_GAME
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.DISPLAY_MODE_GROSS
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.DISPLAY_MODE_NET
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.DISPLAY_MODE_POINT_QUOTA
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.DISPLAY_MODE_STABLEFORD
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.HDCP_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.HOLE_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.JunkTableSelection
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.PAR_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.ScoreCardActions
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.TEAM_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.USED_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.getTotalScore
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.highLiteTotalColumn
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.refreshScoreCard
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.setBoardColorForPlayerTeamScore
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.updatePlayersTeamScoreCells
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.updateScoreCardState
import com.golfpvcc.teamscore_rev4.utils.BACK_NINE_DISPLAY
import com.golfpvcc.teamscore_rev4.utils.BACK_NINE_IS_DISPLAYED
import com.golfpvcc.teamscore_rev4.utils.BACK_NINE_TOTAL_DISPLAYED
import com.golfpvcc.teamscore_rev4.utils.BIRDIES_ON_HOLE

import com.golfpvcc.teamscore_rev4.utils.SCORE_CARD_REC_ID
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
import com.golfpvcc.teamscore_rev4.utils.EAGLE_ON_HOLE
import com.golfpvcc.teamscore_rev4.utils.HOLE_ARRAY_SIZE
import com.golfpvcc.teamscore_rev4.utils.PQ_TARGET
import com.golfpvcc.teamscore_rev4.utils.TEAM_CLEAR_SCORE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class ScoreCardViewModel() : ViewModel() {
    var state by mutableStateOf(ScoreCard())
    private val scoreCardDao = TeamScoreCardApp.getScoreCardDao()
    private val playerRecordDoa = TeamScoreCardApp.getPlayerDao()
    private val pointsRecordDoa = TeamScoreCardApp.getPointsDao()


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

            state.mGamePointsTable = pointsRecordDoa.getAllPointRecords()

            state.mJunkTableSelection.loadJunkTableRecords()
        }
    }

    private fun savePlayersScoresRecord() {
        viewModelScope.launch(Dispatchers.IO) {
            saveScoreCardRecord()
            for (playerHeadRecord in state.mPlayerHeading) {
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
            ScoreCardActions.ScreenModeGross -> changeScreenGross()
            ScoreCardActions.ScreenModeNet -> changeScreenNet()
            ScoreCardActions.ScreenModeStableford -> changeScreenStableford()
            ScoreCardActions.ScreenModePtQuote -> changeScreenPtQuote()
            ScoreCardActions.ScreenModeNineGame -> changeScreenNineGame()
            ScoreCardActions.ButtonEnterScore -> buttonEnterScore()
            ScoreCardActions.SetDialogCurrentPlayer -> setDialogCurrentPlayer(0)
            ScoreCardActions.FlipFrontBackNine -> flipFrontNineBackNine()
        }
    }

    fun changeScreenGross() {
        state = state.copy(mDisplayScreenModeText = "Gross")

        state.mDisplayScreenMode = DISPLAY_MODE_GROSS
        val parCells = getHoleParCells()
        refreshScoreCard(parCells)
    }

    fun changeScreenNet() {
        state = state.copy(mDisplayScreenModeText = "Net")

        state.mDisplayScreenMode = DISPLAY_MODE_NET
        val parCells = getHoleParCells()
        refreshScoreCard(parCells)
    }

    fun changeScreenStableford() {
        state = state.copy(mDisplayScreenModeText = "Stableford")

        state.mDisplayScreenMode = DISPLAY_MODE_STABLEFORD
        val parCells = getHoleParCells()
        refreshScoreCard(parCells)
    }

    fun changeScreenPtQuote() {
        state = state.copy(mDisplayScreenModeText = "Point Quote")

        state.mDisplayScreenMode = DISPLAY_MODE_POINT_QUOTA
        val parCells = getHoleParCells()
        refreshScoreCard(parCells)
    }

    fun changeScreenNineGame() {
        state = state.copy(mDisplayScreenModeText = "Nines Game")
        if (state.mGameNines) {
            state.mDisplayScreenMode = DISPLAY_MODE_9_GAME
            val parCells = getHoleParCells()
            refreshScoreCard(parCells)
        }
    }

    fun getPlayerHoleScore(
        playerIdx: Int,
        idx: Int,
    ): String { //display the player's score on the score card, howerver display 0 for point quota
        val playerHoleScore: Int =
            (state.mPlayerHeading[playerIdx].mDisplayScore[idx])
        var displayPlayerHoleScore: String = "  "

        if (state.mPlayerHeading[playerIdx].mScore[idx] != 0) {
            displayPlayerHoleScore = playerHoleScore.toString()
        }

        Log.d("VIN", "getPlayerHoleScore Player's score $displayPlayerHoleScore")
        return (displayPlayerHoleScore)
    }

    fun getTeamUsedCells(): IntArray {
        val teamUsedCells =
            state.mTeamUsedHeading.find { it.vinTag == USED_HEADER }
        return if (teamUsedCells != null) {
            (teamUsedCells.mHole)
        } else
            (IntArray(HOLE_ARRAY_SIZE) { 0 })
    }

    fun getTeamPlayerScoreCells(): IntArray {
        val teamUsedCells =
            state.mTeamUsedHeading.find { it.vinTag == TEAM_HEADER }
        return if (teamUsedCells != null) {
            (teamUsedCells.mHole)
        } else
            (IntArray(HOLE_ARRAY_SIZE) { 0 })
    }

    fun getHoleHdcpCells(): IntArray {
        val hdcpCell: HdcpParHoleHeading? =
            state.mHdcpParHoleHeading.find { it.vinTag == HDCP_HEADER }
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
            state.mHdcpParHoleHeading.find { it.vinTag == PAR_HEADER }
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
        if (state.mDisplayScreenMode == DISPLAY_MODE_GROSS || state.mDisplayScreenMode == DISPLAY_MODE_NET) {
            when (score) {
                BIRDIES_ON_HOLE -> backGroundColor = Color(RED_ONE_UNDER_PAR)
                EAGLE_ON_HOLE -> backGroundColor = Color(PURPLE_TWO_UNDER_PAR)
            }
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

    fun getPlayerScoreAdjustedForPtQuote(playerTotalScore: String, playerHdcp: Int): String {
        var playerAdjustScore: String = playerTotalScore

        if (state.mDisplayScreenMode == DISPLAY_MODE_POINT_QUOTA)
            playerAdjustScore = adjustPlayerPtQuoteScore(playerTotalScore, playerHdcp)
        return (playerAdjustScore)
    }

    private fun adjustPlayerPtQuoteScore(playerScore: String, playerHdcp: Int): String {
        var adjustedScore: Float = 0f
        val ptQuota = state.mGamePointsTable.filter { it.mId == PQ_TARGET }
        var playerAdjustedScore: String = playerScore

        if (ptQuota.isNotEmpty()) {
            val targetPoint: Int = ptQuota.first().mPoints

            adjustedScore = (targetPoint - playerHdcp).toFloat()
            adjustedScore /= 2
            adjustedScore = (playerScore.toInt() - adjustedScore)

            Log.d("VIN", "Target $targetPoint hdcp $playerHdcp, player score $playerScore, ")
            playerAdjustedScore = adjustedScore.toString()
        }

        return (playerAdjustedScore)
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
        state.mPlayerHeading[playerIdx].mScore[idx] = score
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
            is DialogAction.DisplayJunkDialog -> displayJunkDialog(action.playerIdx)
            DialogAction.DisplayHoleNote -> displayHoleNote()
            is DialogAction.ToggleJunkListItem -> toggleJunkListItem(action.listIdx)
            DialogAction.CloseJunkTableList -> closeJunkTableList()
        }
    }


    fun displayJunkDialog(playerIdx: Int) {   //  Dialog Enter player scores function are below
        Log.d("VIN", "displayJunkDialog player Idx $playerIdx ")
        viewModelScope.launch(Dispatchers.IO) {
                        state.mJunkTableSelection.loadPlayerJunkRecords(playerIdx, state.mCurrentHole)
            state.mDialogDisplayJunkSelection = true
            state.mCurrentJunkPlayerIdx = playerIdx
            repaintScreen()
        }
    }

    private fun toggleJunkListItem(listIdx: Int) {
        Log.d("VIN", "toggleJunkListItem listIdx  $listIdx")

        var selection = !state.mJunkTableSelection.mJunkTableList[listIdx].mSelected
        val playerJunkRecord = state.mJunkTableSelection.setJunkPlayerRecordToDB(
            listIdx,
            selection,
            state.mCurrentJunkPlayerIdx,
            state.mCurrentHole
        )
        Log.d("JUNKREC", "toggleJunkListItem selection  $selection rec $playerJunkRecord")
        viewModelScope.launch(Dispatchers.IO) {
            if (selection) {
                state.mJunkTableSelection.addPlayerJunkRecord(playerJunkRecord)
            } else {
                state.mJunkTableSelection.deletePlayerJunkRecord(playerJunkRecord)
            }
        }
        repaintScreen()
    }

    private fun closeJunkTableList() {
        state.mDialogDisplayJunkSelection = false
        repaintScreen()
    }

    fun displayHoleNote() { //  Dialog Enter player scores function are below
        Log.d("VIN", "displayHoleNotee")
    }

    fun getDialogPlayerHoleScore(
        playerIdx: Int,
        idx: Int,
    ): String { //display the player's score on the dialog window
        val playerHoleScore: Int =
            (state.mPlayerHeading[playerIdx].mScore[idx])
        var displayPlayerHoleScore: String = "  "

        if (playerHoleScore != 0)
            displayPlayerHoleScore = playerHoleScore.toString()
        Log.d("VIN", "getDialogPlayerHoleScore Player's score $displayPlayerHoleScore")
        return (displayPlayerHoleScore)
    }

    fun buttonEnterScore() { //  Dialog Enter player scores function are below
        Log.d("VIN", "ButtonEnterScore")
        state = state.copy(mDialogEnterScores = true)
        if (state.mShowTotals) {
            advanceToTheNextHole()  //user looking at the total scores
            highLiteTotalColumn(VIN_LIGHT_GRAY)
        }
        displayFrontOrBackOfScoreCard()
        configureDialogGrossAndNetButtonColors()
    }

    private fun doneScoringDialog() { //  Dialog Enter player scores function are below
        Log.d("HOLE", "doneScoringDialog current hole ${state.mCurrentHole}")
        state = state.copy(mDialogEnterScores = false)
        updatePlayersTeamScoreCells( // doneScoringDialog
            state.mDisplayScreenMode,
            state.mCurrentHole,
            getParForHole(state.mCurrentHole),
            state.mPlayerHeading
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

        for (playerHeadRecord in state.mPlayerHeading) {
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
        var currentTeamMask = state.mPlayerHeading[playerIdx].mTeamHole[currentHole]

        state.mGrossButtonColor[playerIdx] = Color.LightGray
        state.mNetButtonColor[playerIdx] = Color.LightGray
        state.mPlayerHeading[playerIdx].mTeamHole[currentHole] = TEAM_CLEAR_SCORE
        if (currentTeamMask != teamScoreMask) {     // disable the selection if equal
            getGrossButtonColor(playerIdx, teamScoreMask)
            state.mPlayerHeading[playerIdx].mTeamHole[currentHole] = teamScoreMask
        }
        repaintScreen()
    }

    private fun getGrossButtonColor(
        playerIdx: Int,
        teamHoleMask: Int,
    ) { //  Dialog Enter player scores function are below

        if (teamScoreTypeNet(teamHoleMask)) {
            state.mGrossButtonColor[playerIdx] = getTeamButtonColor(teamHoleMask)
            state.mNetButtonColor[playerIdx] = Color.LightGray
        } else {
            state.mGrossButtonColor[playerIdx] = Color.LightGray
            state.mNetButtonColor[playerIdx] = getTeamButtonColor(teamHoleMask)
        }
    }

    private fun clearGrossAndNetButtons() { //  Dialog Enter player scores function are below
        for (idx in state.mGrossButtonColor.indices) {
            state.mGrossButtonColor[idx] = Color.LightGray
        }
        for (idx in state.mNetButtonColor.indices) {
            state.mNetButtonColor[idx] = Color.LightGray
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
        if (dialogCurrentPlayer < state.mPlayerHeading.size)
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
    var mGamePointsTable: List<PointsRecord> = emptyList(),
    var mHasDatabaseBeenRead: Boolean = false,
    val mGrossButtonColor: Array<Color> = Array(4) { Color.LightGray },
    val mNetButtonColor: Array<Color> = Array(4) { Color.LightGray },
    val mJunkButtonColor: Array<Color> = Array(4) { Color.LightGray },
    var mDisplayScreenMode: Int = DISPLAY_MODE_GROSS,
    val mDisplayScreenModeText: String = "Gross",        // what is being display now
    val mButtonScreenNextText: String = "Net",             // what will be display next
    val mRepaintScreen: Boolean = false,
    val mShowTotals: Boolean = false,
    var mGameNines: Boolean = false,    // true if we only have 3 players
    val mCourseName: String = "",    // current course name from the course list database
    val mCourseId: Int = 0,
    val mTee: String = "",                   // the tee's played or the course yardage
    val mDialogEnterScores: Boolean = false,
    var mDialogDisplayJunkSelection: Boolean = false,
    var mDialogCurrentPlayer: Int = 0,
    val mCurrentHole: Int = 0,      // the current hole being played in the game
    var mCurrentJunkPlayerIdx: Int = 0, // set when adding player junk records
    val mWhatNineIsBeingDisplayed: Boolean = FRONT_NINE_IS_DISPLAYED,
    val mHdcpParHoleHeading: List<HdcpParHoleHeading> = listOf(
        HdcpParHoleHeading(HDCP_HEADER, "HdCp", mTotal = "Notes"),
        HdcpParHoleHeading(PAR_HEADER, "Par"),
        HdcpParHoleHeading(HOLE_HEADER, mName = "Hole", mTotal = "Total"),
    ),

    var mPlayerHeading: List<PlayerHeading> = emptyList(),

    val mTeamUsedHeading: List<TeamUsedHeading> = listOf(
        TeamUsedHeading(TEAM_HEADER, "Team"),   // total hole score for selected player
        TeamUsedHeading(USED_HEADER, "Used"),   // total players selected for this hole
    ),
    val mJunkTableSelection: JunkTableSelection = JunkTableSelection(
        TeamScoreCardApp.getJunkDao(),
        TeamScoreCardApp.getPlayerJunkDao()
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
    var mScore: IntArray = IntArray(HOLE_ARRAY_SIZE),   // gross scores
    var mDisplayScore: IntArray = IntArray(HOLE_ARRAY_SIZE) { 0 },
    var mStokeHole: IntArray = IntArray(HOLE_ARRAY_SIZE) { 0 },
    var mTeamHole: IntArray = IntArray(HOLE_ARRAY_SIZE),  // used for team scoring
    val mJunk: IntArray = IntArray(HOLE_ARRAY_SIZE),
    var mTotal: String = "",
)

data class TeamUsedHeading(
    val vinTag: Int = 0,
    var mName: String = "",
    var mHole: IntArray = IntArray(HOLE_ARRAY_SIZE) { 0 },
    var mTotal: String = "",
)

data class JunkTableList(
    var mJunkName: String = "",
    var mId: Long = 1,
    var mSelected: Boolean = false,
)

