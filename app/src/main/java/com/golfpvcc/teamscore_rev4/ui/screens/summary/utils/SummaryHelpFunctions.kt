package com.golfpvcc.teamscore_rev4.ui.screens.summary.utils

import android.util.Log
import com.golfpvcc.teamscore_rev4.database.model.PointsRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardWithPlayers
import com.golfpvcc.teamscore_rev4.ui.screens.getTotalPlayerPointQuota
import com.golfpvcc.teamscore_rev4.ui.screens.getTotalPlayerScore
import com.golfpvcc.teamscore_rev4.ui.screens.getTotalPlayerStableford
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.HdcpParHoleHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.PlayerHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.HDCP_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.NineGame
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.PAR_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.setPlayerStrokeHoles
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.updatePlayerDisplayScore
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.updatePlayersTeamScoreCells
import com.golfpvcc.teamscore_rev4.ui.screens.summary.PlayerSummary
import com.golfpvcc.teamscore_rev4.ui.screens.summary.SummaryViewModel
import com.golfpvcc.teamscore_rev4.ui.screens.summary.TeamPoints
import com.golfpvcc.teamscore_rev4.utils.BACK_NINE_DISPLAY
import com.golfpvcc.teamscore_rev4.utils.BACK_NINE_TOTAL_DISPLAYED
import com.golfpvcc.teamscore_rev4.utils.FRONT_NINE_DISPLAY
import com.golfpvcc.teamscore_rev4.utils.PQ_TARGET
import com.golfpvcc.teamscore_rev4.utils.TOTAL_18_HOLE


fun SummaryViewModel.updateScoreCardState(scoreCardWithPlayers: ScoreCardWithPlayers) {
    val scoreCardRecord: ScoreCardRecord = scoreCardWithPlayers.scoreCardRecord

    state = state.copy(mCourseName = scoreCardRecord.mCourseName)
    state = state.copy(mTee = scoreCardRecord.mTee)
    state = state.copy(mCourseId = scoreCardRecord.mCourseId)

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
            mName = scoreCardWithPlayers.playerRecords[idx].mName,
            mHdcp = scoreCardWithPlayers.playerRecords[idx].mHandicap,
            mScore = scoreCardWithPlayers.playerRecords[idx].mScore,
            mTeamHole = scoreCardWithPlayers.playerRecords[idx].mTeamHole,
        ) // add the player's name to the score card
        state.playerSummary += PlayerSummary(mPlayer = tmpPlayer)
        if (hdcpCell != null) {
            setPlayerStrokeHoles(state.playerSummary[idx].mPlayer, hdcpCell.mHole)
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
    var pointQuotaTargetValue: PointsRecord = PointsRecord(0, 36)

    state.mTotalPtQuoteFront = 0f
    state.mTotalPtQuoteBack = 0f
    state.mUsedPtQuoteBack = 0f
    state.mUsedPtQuoteFront = 0f

    state.mTotalPointsFront = 0f
    state.mTotalPointsBack = 0f
    state.mQuotaPointsFront = 0f
    state.mQuotaPointsBack = 0f

    val pointQuotaRecord = state.mGamePointsTable.filter { it.mId == PQ_TARGET }
    if (pointQuotaRecord.isNotEmpty()) {
        pointQuotaTargetValue = pointQuotaRecord.first()
    }
    if (parCell != null) {
        for (playerSummary in state.playerSummary) {

            var teamPoints: TeamPoints = getTotalPlayerPointQuota(
                whatNine = FRONT_NINE_DISPLAY,
                playerHeading = playerSummary,
                holePar = parCell.mHole,
                gamePointsTable = state.mGamePointsTable
            )
            state.mTotalPtQuoteFront -= (pointQuotaTargetValue.mPoints - playerSummary.mPlayer.mHdcp.toInt()) / 2f // target points to make
            state.mTotalPtQuoteFront += teamPoints.teamTotalPoints  // player front nine points

            state.mUsedPtQuoteFront -= (pointQuotaTargetValue.mPoints - playerSummary.mPlayer.mHdcp.toInt()) / 2f // target points to make
            state.mUsedPtQuoteFront += teamPoints.teamUsedPoints

            state.mTotalPointsFront += teamPoints.teamTotalPoints  // player front nine points
            state.mQuotaPointsFront += (pointQuotaTargetValue.mPoints - playerSummary.mPlayer.mHdcp.toInt()) / 2f // target points to make

            teamPoints = getTotalPlayerPointQuota(
                whatNine = BACK_NINE_DISPLAY,
                playerHeading = playerSummary,
                holePar = parCell.mHole,
                gamePointsTable = state.mGamePointsTable
            )
            state.mTotalPtQuoteBack -= (pointQuotaTargetValue.mPoints - playerSummary.mPlayer.mHdcp.toInt()) / 2f // target points to make
            state.mTotalPtQuoteBack += teamPoints.teamTotalPoints     // player back nine points

            state.mUsedPtQuoteBack -= (pointQuotaTargetValue.mPoints - playerSummary.mPlayer.mHdcp.toInt()) / 2f // target points to make
            state.mUsedPtQuoteBack += teamPoints.teamUsedPoints

            state.mTotalPointsBack += teamPoints.teamTotalPoints  // player back nine points
            state.mQuotaPointsBack += (pointQuotaTargetValue.mPoints - playerSummary.mPlayer.mHdcp.toInt()) / 2f // target points to make

            playerSummary.mQuote -= (pointQuotaTargetValue.mPoints - playerSummary.mPlayer.mHdcp.toInt()) // target points to make
            Log.d("VIN","calculatePtQuote Player ${playerSummary.mPlayer.mName} qt pts ${playerSummary.mQuote}" )
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
        for (playerSummary in state.playerSummary) {
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
        for (playerSummary in state.playerSummary) {
            Log.d("VIN","2 calculateOverUnderScores Player ${playerSummary.mPlayer.mName} qt pts ${playerSummary.mQuote}" )

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
            Log.d("VIN","2 calculateOverUnderScores Player ${playerSummary.mPlayer.mName} qt pts ${playerSummary.mQuote}" )

        }
    }
}

fun SummaryViewModel.playerScoreSummary() {
    val parCell: HdcpParHoleHeading? = state.hdcpParHoleHeading.find { it.vinTag == PAR_HEADER }
    val hdcpCell: HdcpParHoleHeading? = state.hdcpParHoleHeading.find { it.vinTag == HDCP_HEADER }
    if ((parCell != null) && (hdcpCell != null)) {
        for (playerSummary in state.playerSummary) {
            Log.d("VIN","2 playerScoreSummary Player ${playerSummary.mPlayer.mName} qt pts ${playerSummary.mQuote}" )
            calculatePlayerScoreSummary(playerSummary, parCell.mHole, hdcpCell.mHole)
        }
        if(state.playerSummary.count() == 3){
            calculatePlayerNineScores()
        }
    }
}
fun SummaryViewModel.calculatePlayerNineScores(){
    var nineGameScores = NineGame()
    var currentHole : Int = 0

    while (currentHole < TOTAL_18_HOLE) { // holes 1 to 18
        nineGameScores.clearTotals()
        for (playerSummary in state.playerSummary) {        // add each player score to 9' Class
            if (0 < playerSummary.mPlayer.mScore[currentHole]) {
                val playerNetScore =
                    playerSummary.mPlayer.mScore[currentHole] - playerSummary.mPlayer.mStokeHole[currentHole]
                nineGameScores.addPlayerGrossScore(playerSummary.mPlayer.vinTag, playerNetScore)
            }
        }
        nineGameScores.sort9Scores()    // calculate player's scores
        for (playerSummary in state.playerSummary) {
            playerSummary.mNineTotal += nineGameScores.get9GameScore(playerSummary.mPlayer.vinTag)
        }
        currentHole++
    }
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
            -2 -> playerHeading.mEagles++
            -1 -> playerHeading.mBirdies++
            0 -> playerHeading.mPars++
            1 -> playerHeading.mBogeys++
            2 -> playerHeading.mDouble++
            else -> playerHeading.mOthers++
        }
        currentHole++
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
    playerHeading.mSandy = 0
    playerHeading.mCTP = 0
    playerHeading.mOtherJunk = 0
}

