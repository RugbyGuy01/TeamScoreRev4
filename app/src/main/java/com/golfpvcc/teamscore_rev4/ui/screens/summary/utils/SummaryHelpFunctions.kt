package com.golfpvcc.teamscore_rev4.ui.screens.summary.utils

import android.content.Context
import android.util.Log
import com.golfpvcc.teamscore_rev4.database.dao.PlayerJunkDao
import com.golfpvcc.teamscore_rev4.database.model.JunkRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardWithPlayers
import com.golfpvcc.teamscore_rev4.ui.screens.getTotalPlayerPointQuota
import com.golfpvcc.teamscore_rev4.ui.screens.getTotalPlayerScore
import com.golfpvcc.teamscore_rev4.ui.screens.getTotalPlayerStableford
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.HdcpParHoleHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.PlayerHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.BACK_NINE_IS_DISPLAYED
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.DISPLAY_MODE_6_X_6
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.DISPLAY_MODE_X_6_6
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.FRONT_NINE_IS_DISPLAYED
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.GameABCD
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.HDCP_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.NineGame
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.PAR_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.setPlayerStrokeHoles
import com.golfpvcc.teamscore_rev4.ui.screens.summary.PlayerSummary
import com.golfpvcc.teamscore_rev4.ui.screens.summary.PlayerJunkPayoutRecord
import com.golfpvcc.teamscore_rev4.ui.screens.summary.SummaryViewModel
import com.golfpvcc.teamscore_rev4.ui.screens.summary.TeamPoints
import com.golfpvcc.teamscore_rev4.utils.BACK_NINE_DISPLAY
import com.golfpvcc.teamscore_rev4.utils.BIRDIES_ON_HOLE
import com.golfpvcc.teamscore_rev4.utils.BOGGY_ON_HOLE
import com.golfpvcc.teamscore_rev4.utils.DOUBLE_ON_HOLE
import com.golfpvcc.teamscore_rev4.utils.EAGLE_ON_HOLE
import com.golfpvcc.teamscore_rev4.utils.FRONT_NINE_DISPLAY
import com.golfpvcc.teamscore_rev4.utils.PAR_ON_HOLE
import com.golfpvcc.teamscore_rev4.utils.PQ_TARGET
import com.golfpvcc.teamscore_rev4.utils.PointTable
import com.golfpvcc.teamscore_rev4.utils.TOTAL_18_HOLE


fun SummaryViewModel.updateScoreCardState(scoreCardWithPlayers: ScoreCardWithPlayers) {
    val scoreCardRecord: ScoreCardRecord = scoreCardWithPlayers.scoreCardRecord

    state = state.copy(mCourseName = scoreCardRecord.mCourseName)
    state = state.copy(mTee = scoreCardRecord.mTee)
    state = state.copy(mCourseId = scoreCardRecord.mCourseId)
    state.mDatePlayed = scoreCardRecord.mDatePlayed

    Log.d("VIN", "updateScoreCardState Id ${scoreCardRecord.mCourseId}")

    val parCell: HdcpParHoleHeading? = state.hdcpParHoleHeading.find { it.vinTag == PAR_HEADER }
    val hdcpCell: HdcpParHoleHeading? = state.hdcpParHoleHeading.find { it.vinTag == HDCP_HEADER }
    if (parCell != null && hdcpCell != null) {
        parCell.mHole = scoreCardRecord.mPar
        hdcpCell.mHole = scoreCardRecord.mHandicap
    }
    state.mGameNines = scoreCardWithPlayers.playerRecords.size == 3
    scoreCardWithPlayers.playerRecords.forEachIndexed { idx, player ->
        val tmpPlayer = PlayerHeading(
            idx,
            mDatePlayed = scoreCardRecord.mDatePlayed,
            mName = scoreCardWithPlayers.playerRecords[idx].mName,
            mHdcp = scoreCardWithPlayers.playerRecords[idx].mHandicap,
            mScore = scoreCardWithPlayers.playerRecords[idx].mScore,
            mTeamHole = scoreCardWithPlayers.playerRecords[idx].mTeamHole,
        ) // add the player's name to the score card
        state.mPlayerSummary += PlayerSummary(mPlayer = tmpPlayer)
        if (hdcpCell != null) {
            setPlayerStrokeHoles(state.mPlayerSummary[idx].mPlayer, hdcpCell.mHole)
        }
    }
}

/*
Point quota Used calculation is as follows:
Add all of the players used point quota flagged on the score card.
The points need for the team is target points (36) minus the player handicap is the points need for that player.
Add up all of the player's 'Points need' to equal the teams need points.
Now subtract team's need points from the player flag points to equal the used points for the game..
 */
fun SummaryViewModel.calculatePtQuote() {
    val parCell: HdcpParHoleHeading? = state.hdcpParHoleHeading.find { it.vinTag == PAR_HEADER }
    var pointQuotaTargetValue = PointTable(0, "36")

    state.mTotalPtQuoteFront = 0f
    state.mTotalPtQuoteBack = 0f
    state.mUsedPtQuoteBack = 0f
    state.mUsedPtQuoteFront = 0f

    state.mTotalPointsFront = 0f
    state.mTotalPointsBack = 0f
    state.mQuotaPointsFront = 0f
    state.mQuotaPointsBack = 0f

    val pointQuotaRecord = state.mGamePointsTable.filter { it.key == PQ_TARGET }
    if (pointQuotaRecord.isNotEmpty()) {
        pointQuotaTargetValue = pointQuotaRecord.first()
    }
    if (parCell != null) {
        for (playerSummary in state.mPlayerSummary) {

            var teamPoints: TeamPoints = getTotalPlayerPointQuota(
                whatNine = FRONT_NINE_DISPLAY,
                playerHeading = playerSummary,
                holePar = parCell.mHole,
                gamePointsTable = state.mGamePointsTable
            )
            state.mTotalPtQuoteFront -= (pointQuotaTargetValue.value.toInt() - playerSummary.mPlayer.mHdcp.toInt()) / 2f // target points to make
            state.mTotalPtQuoteFront += teamPoints.teamTotalPoints  // player front nine points

            state.mUsedPtQuoteFront -= (pointQuotaTargetValue.value.toInt() - playerSummary.mPlayer.mHdcp.toInt()) / 2f // target points to make
            state.mUsedPtQuoteFront += teamPoints.teamUsedPoints

            state.mTotalPointsFront += teamPoints.teamTotalPoints  // player front nine points
            state.mQuotaPointsFront += (pointQuotaTargetValue.value.toInt() - playerSummary.mPlayer.mHdcp.toInt()) / 2f // target points to make

            teamPoints = getTotalPlayerPointQuota(
                whatNine = BACK_NINE_DISPLAY,
                playerHeading = playerSummary,
                holePar = parCell.mHole,
                gamePointsTable = state.mGamePointsTable
            )
            state.mTotalPtQuoteBack -= (pointQuotaTargetValue.value.toInt() - playerSummary.mPlayer.mHdcp.toInt()) / 2f // target points to make
            state.mTotalPtQuoteBack += teamPoints.teamTotalPoints     // player back nine points

            state.mUsedPtQuoteBack -= (pointQuotaTargetValue.value.toInt() - playerSummary.mPlayer.mHdcp.toInt()) / 2f // target points to make
            state.mUsedPtQuoteBack += teamPoints.teamUsedPoints

            state.mTotalPointsBack += teamPoints.teamTotalPoints  // player back nine points
            state.mQuotaPointsBack += (pointQuotaTargetValue.value.toInt() - playerSummary.mPlayer.mHdcp.toInt()) / 2f // target points to make

            playerSummary.mQuote -= (pointQuotaTargetValue.value.toInt() - playerSummary.mPlayer.mHdcp.toInt()) // target points to make
            Log.d(
                "VIN",
                "calculatePtQuote Player ${playerSummary.mPlayer.mName} qt pts ${playerSummary.mQuote}"
            )
        }
    }
}

fun SummaryViewModel.calculateStableford() {
    val parCell: HdcpParHoleHeading? = state.hdcpParHoleHeading.find { it.vinTag == PAR_HEADER }

    state.mTotalStablefordFront = 0
    state.mTotalStablefordBack = 0
    state.mUsedStablefordFront = 0
    state.mUsedStablefordBack = 0
    if (parCell != null) {
        for (playerSummary in state.mPlayerSummary) {
            var teamPoints = getTotalPlayerStableford(
                whatNine = FRONT_NINE_DISPLAY,
                playerHeading = playerSummary,
                holePar = parCell.mHole,
                gamePointsTable = state.mGamePointsTable
            )
            state.mTotalStablefordFront += teamPoints.teamTotalPoints
            state.mUsedStablefordFront += teamPoints.teamUsedPoints
            teamPoints = getTotalPlayerStableford(
                whatNine = BACK_NINE_DISPLAY,
                playerHeading = playerSummary,
                holePar = parCell.mHole,
                gamePointsTable = state.mGamePointsTable
            )
            state.mTotalStablefordBack += teamPoints.teamTotalPoints
            state.mUsedStablefordBack += teamPoints.teamUsedPoints
        }
    }
}

fun SummaryViewModel.calculateOverUnderScores() {
    val parCell: HdcpParHoleHeading? = state.hdcpParHoleHeading.find { it.vinTag == PAR_HEADER }
    state.mTotalScoreFront = 0
    state.mTotalScoreBack = 0
    state.mOverUnderScoreFront = 0
    state.mOverUnderScoreBack = 0

    if (parCell != null) {
        for (playerSummary in state.mPlayerSummary) {
            Log.d(
                "VIN",
                "2 calculateOverUnderScores Player ${playerSummary.mPlayer.mName} qt pts ${playerSummary.mQuote}"
            )

            var teamPoints = getTotalPlayerScore(
                whatNine = FRONT_NINE_DISPLAY,
                playerHeading = playerSummary,
                holePar = parCell.mHole,
            )
            state.mTotalScoreFront += teamPoints.teamTotalPoints    // scores
            state.mOverUnderScoreFront += teamPoints.teamUsedPoints // over/under score

            teamPoints = getTotalPlayerScore(
                whatNine = BACK_NINE_DISPLAY,
                playerHeading = playerSummary,
                holePar = parCell.mHole,
            )
            state.mTotalScoreBack += teamPoints.teamTotalPoints
            state.mOverUnderScoreBack += teamPoints.teamUsedPoints
            Log.d(
                "VIN",
                "2 calculateOverUnderScores Player ${playerSummary.mPlayer.mName} qt pts ${playerSummary.mQuote}"
            )

        }
    }
}

fun SummaryViewModel.playerScoreSummary(playerJunkDao: PlayerJunkDao) {
    val parCell: HdcpParHoleHeading? = state.hdcpParHoleHeading.find { it.vinTag == PAR_HEADER }
    val hdcpCell: HdcpParHoleHeading? = state.hdcpParHoleHeading.find { it.vinTag == HDCP_HEADER }
    if ((parCell != null) && (hdcpCell != null)) {

        state.mPlayerSummary.forEachIndexed { idx, playerSummary ->

            calculatePlayerScoreSummary(playerSummary, parCell.mHole, hdcpCell.mHole)
            calculatePlayerJunkSummary(playerJunkDao, playerSummary, idx, state.mJunkRecordTable)
        }
        if (state.mPlayerSummary.count() == 3) {
            calculatePlayerNineScores()     // and ABCD game
        }
    }
}

fun calculatePlayerJunkSummary(playerJunkDao: PlayerJunkDao, playerSummary: PlayerSummary, playerIdx:Int, junkRecords: MutableList<JunkRecord>) {

    playerSummary.mPlayerJunkRecords = playerJunkDao.getAllPlayerJunkPayoutRecords(playerIdx)

    for (playerJunkRecord in playerSummary.mPlayerJunkRecords) {
        val junkTableRecordFound =
            junkRecords.find { it.mId == playerJunkRecord.mJunkId } //check for a winner

        if (junkTableRecordFound != null) {
            addPayOutRecord(playerSummary.mJunkPayoutList, junkTableRecordFound.mId, junkTableRecordFound.mJunkName)
        } else {    // should never happen
            addPayOutRecord(playerSummary.mJunkPayoutList, 0, "Error Id ${playerJunkRecord.mJunkId}")
        }
    }
}

fun SummaryViewModel.calculatePlayerNineScores() {
    var nineGameScores = NineGame()
    var gameABCD = GameABCD()
    var currentHole: Int = 0

    while (currentHole < TOTAL_18_HOLE) { // holes 1 to 18
        nineGameScores.clearTotals()
        for (playerSummary in state.mPlayerSummary) {        // add each player score to 9' Class
            if (0 < playerSummary.mPlayer.mScore[currentHole]) {
                val playerNetScore =
                    playerSummary.mPlayer.mScore[currentHole] - playerSummary.mPlayer.mStokeHole[currentHole]
                nineGameScores.addPlayerGrossScore(playerSummary.mPlayer.vinTag, playerNetScore)
                gameABCD.addPlayer(playerNetScore)          // add each player score to  ABCD Class
            }
        }
        nineGameScores.sort9Scores()    // calculate player's scores
        gameABCD.sortScores()           // sort player scores
        var idx: Int = 0

        for (playerSummary in state.mPlayerSummary) {
            playerSummary.mNineTotal += nineGameScores.get9GameScore(playerSummary.mPlayer.vinTag)

            state.mGameABCD[idx] += gameABCD.getPlayerScore(idx)    // now get each player score
            idx++
        }
        currentHole++       // do the next hole
    }
}
fun toggle_6_ScoreCard(currentDisplayMode:Int) : Int{
    val newScreenMode:Int
    newScreenMode = if( currentDisplayMode == FRONT_NINE_IS_DISPLAYED || currentDisplayMode == BACK_NINE_IS_DISPLAYED)
        DISPLAY_MODE_X_6_6
    else
        FRONT_NINE_IS_DISPLAYED

    return (newScreenMode)
}



// This function will calculate the summary of the player's round used by the summary page functions
fun SummaryViewModel.calculatePlayerScoreSummary(
    playerHeading: PlayerSummary,
    holePar: IntArray,
    holeHdcp: IntArray,
) {
    var OverUnderScore: Int
    var playerHoleScore: Int
    var currentHole = 0

    clearSummaryScores(playerHeading)
    while (currentHole < TOTAL_18_HOLE) {
        playerHoleScore = playerHeading.mPlayer.mScore[currentHole]
        if (currentHole < FRONT_NINE_DISPLAY) {
            playerHeading.mFront += playerHoleScore
        } else {
            playerHeading.mBack += playerHoleScore
        }
        OverUnderScore = playerHoleScore - holePar[currentHole]
        when (OverUnderScore) {
            EAGLE_ON_HOLE -> {
                playerHeading.mEagles++
                addPayOutRecord(playerHeading.mJunkPayoutList, EAGLE_ON_HOLE.toLong(), "Eagle")
            }

            BIRDIES_ON_HOLE -> {
                playerHeading.mBirdies++
                addPayOutRecord(playerHeading.mJunkPayoutList, BIRDIES_ON_HOLE.toLong(), "Birdie")
            }

            PAR_ON_HOLE -> playerHeading.mPars++
            BOGGY_ON_HOLE -> playerHeading.mBogeys++
            DOUBLE_ON_HOLE -> playerHeading.mDouble++
            else -> playerHeading.mOthers++
        }
        currentHole++
    }
}

fun addPayOutRecord(
    playerJunkPayoutRecords: MutableList<PlayerJunkPayoutRecord>,
    mJunkId: Long,
    junkName: String,
) {

    val payoutRecord = playerJunkPayoutRecords.find { it.mJunkId == mJunkId }
    if (payoutRecord == null) playerJunkPayoutRecords += PlayerJunkPayoutRecord(
        junkName,
        mJunkId,
        1,
    ) else {
        payoutRecord.mCount++
    }
}

fun SummaryViewModel.sendPlayerEmail(playerIdx: Int, mContext: Context) {
    var subject = "Player's Score: "
    val myEmailApp = EmailScores(mContext)
    var emailAddress = "vgamble@golfpvcc.com"       // default address

    if (state.mEmailRecords.isNotEmpty())
        emailAddress = state.mEmailRecords[0].mEmailAddress

    subject += state.mPlayerSummary[playerIdx].mPlayer.mName  // subject line
    subject += "  - " + state.mCourseName // get the current score for today's game

    val parCell: HdcpParHoleHeading? = state.hdcpParHoleHeading.find { it.vinTag == PAR_HEADER }
    if (parCell != null) {
        var body: String =
            getSpreadSheetScore(state.mPlayerSummary[playerIdx].mPlayer, parCell.mHole)
        body += state.mCourseName + "," // add course name, user will add the tee box or yardage
        body += state.mTee

        myEmailApp.setEmailAddress(emailAddress)
        myEmailApp.setEmailSubject(subject)
        myEmailApp.setEmailBody(body)
        myEmailApp.toPostOffice()
    }
}

fun clearSummaryScores(playerHeading: PlayerSummary) {
    playerHeading.mFront = 0
    playerHeading.mBack = 0
    playerHeading.mEagles = 0
    playerHeading.mBirdies = 0
    playerHeading.mPars = 0
    playerHeading.mBogeys = 0
    playerHeading.mDouble = 0
    playerHeading.mOthers = 0
    playerHeading.mNineTotal = 0
    playerHeading.mJunkPayoutList.clear()
}

