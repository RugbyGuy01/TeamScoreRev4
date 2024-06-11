package com.golfpvcc.teamscore_rev4.ui.screens.summary.utils

import android.util.Log
import com.golfpvcc.teamscore_rev4.database.model.PointsRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardWithPlayers
import com.golfpvcc.teamscore_rev4.ui.screens.getTotalPlayerPointQuota
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.HdcpParHoleHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.PlayerHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.HDCP_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.PAR_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.summary.PlayerSummary
import com.golfpvcc.teamscore_rev4.ui.screens.summary.SummaryViewModel
import com.golfpvcc.teamscore_rev4.ui.screens.summary.TeamPtQuotePoint
import com.golfpvcc.teamscore_rev4.utils.BACK_NINE_DISPLAY
import com.golfpvcc.teamscore_rev4.utils.FRONT_NINE_DISPLAY
import com.golfpvcc.teamscore_rev4.utils.PQ_TARGET


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
    }
}

fun SummaryViewModel.calculatePtQuote() {
    val parCell: HdcpParHoleHeading? = state.hdcpParHoleHeading.find { it.vinTag == PAR_HEADER }
    var pointQuotaTargetValue: PointsRecord = PointsRecord(0, 36)

    state.mTotalPtQuoteFront = 0f
    state.mTotalPtQuoteBack = 0f
    state.mUsedPtQuoteBack = 0f
    state.mUsedPtQuoteFront = 0f

    state.mTotalPointsFront = 0f
    state.mTotalPointsBack  = 0f
    state.mQuotaPointsFront = 0f
    state.mQuotaPointsBack  = 0f

    val pointQuotaRecord = state.mGamePointsTable.filter { it.mId == PQ_TARGET }
    if (pointQuotaRecord.isNotEmpty()) {
        pointQuotaTargetValue = pointQuotaRecord.first()
    }
    if (parCell != null) {
        for (playerSummary in state.playerSummary) {

            var teamPtQuotePoint: TeamPtQuotePoint = getTotalPlayerPointQuota(
                whatNine = FRONT_NINE_DISPLAY,
                playerHeading = playerSummary,
                holePar = parCell.mHole,
                gamePointsTable = state.mGamePointsTable
            )
            state.mTotalPtQuoteFront -= (pointQuotaTargetValue.mPoints - playerSummary.mPlayer.mHdcp.toInt()) / 2f // target points to make
            state.mTotalPtQuoteFront += teamPtQuotePoint.teamTotalPoints  // player front nine points

            state.mUsedPtQuoteFront -= (pointQuotaTargetValue.mPoints - playerSummary.mPlayer.mHdcp.toInt()) / 2f // target points to make
            state.mUsedPtQuoteFront += teamPtQuotePoint.teamUsedPoints

            state.mTotalPointsFront += teamPtQuotePoint.teamTotalPoints  // player front nine points
            state.mQuotaPointsFront += (pointQuotaTargetValue.mPoints - playerSummary.mPlayer.mHdcp.toInt()) / 2f // target points to make

            Log.d("VIN", "Front Points Total ${state.mTotalPointsFront} Need points ${state.mQuotaPointsFront}")
            teamPtQuotePoint = getTotalPlayerPointQuota(
                whatNine = BACK_NINE_DISPLAY,
                playerHeading = playerSummary,
                holePar = parCell.mHole,
                gamePointsTable = state.mGamePointsTable
            )
            state.mTotalPtQuoteBack -= (pointQuotaTargetValue.mPoints - playerSummary.mPlayer.mHdcp.toInt())/2f // target points to make
            state.mTotalPtQuoteBack += teamPtQuotePoint.teamTotalPoints     // player back nine points

            state.mUsedPtQuoteBack -= (pointQuotaTargetValue.mPoints - playerSummary.mPlayer.mHdcp.toInt())/2f // target points to make
            state.mUsedPtQuoteBack += teamPtQuotePoint.teamUsedPoints

            state.mTotalPointsBack += teamPtQuotePoint.teamTotalPoints  // player back nine points
            state.mQuotaPointsBack +=  (pointQuotaTargetValue.mPoints - playerSummary.mPlayer.mHdcp.toInt()) / 2f // target points to make


            Log.d("VIN", "Back Points Total ${state.mTotalPointsBack} Need points ${state.mQuotaPointsBack}")

            playerSummary.mQuote -= (pointQuotaTargetValue.mPoints - playerSummary.mPlayer.mHdcp.toInt()) // target points to make
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