package com.golfpvcc.teamscore_rev4.ui.screens.summary.utils

import android.util.Log
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardWithPlayers
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.HdcpParHoleHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.PlayerHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.HDCP_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.PAR_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.summary.PlayerSummary
import com.golfpvcc.teamscore_rev4.ui.screens.summary.SummaryViewModel


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
        state.playerSummary += PlayerSummary(mPlayer = tmpPlayer )
    }
}

fun TeamPointQuotaTotals() {
//    val teamBasePointsNeeded: Float = summaryViewModel.calculatedTeamPointsNeeded().toFloat()
//    val numberOfPlayers = summaryViewModel.getNumberOfPlayer()
//
//    var teamTotalFrontNine: Float
//    var teamTotalBackNine: Float
//    var teamTotal: Float
//    var teamUsedTotalFrontNine: Float
//    var teamUsedTotalBackNine: Float
//    var teamUsedTotal: Float
//    var teamPointsFrontNine: Float
//    var teamPointsBackNine: Float
//    var teamPointsTotal: Float
//    var strTeamTotalFront: String
//    var strTeamTotalBack: String
//    var strTeamToal: String
//    var strTeamActualPointsFront: String
//    var strTeamActualPointsBack: String
//    var strTeamActualPointsTotal: String
//
//    for (idx in 0 until numberOfPlayers) {
//        TeamTotalFrontNine += m_PlayerScreenData.get(Inx).GetTotalPlayerPointQuotaTotal(
//            FIRST_HOLE,
//            NINETH_HOLE
//        ) // get the front nine quota, will round down
//        TeamTotalBackNine += m_PlayerScreenData.get(Inx).GetTotalPlayerPointQuotaTotal(
//            NINETH_HOLE,
//            HOLES_18
//        ) // get the front nine quota, will round down
//
//        TeamUsedTotalFrontNine += m_PlayerScreenData.get(Inx)
//            .GetTotalUsedPlayerPointQuotaTotal(FIRST_HOLE, NINETH_HOLE)
//        TeamUsedTotalBackNine += m_PlayerScreenData.get(Inx)
//            .GetTotalUsedPlayerPointQuotaTotal(NINETH_HOLE, HOLES_18)
//    }

}
