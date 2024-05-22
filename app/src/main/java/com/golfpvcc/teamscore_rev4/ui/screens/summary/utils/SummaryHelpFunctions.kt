package com.golfpvcc.teamscore_rev4.ui.screens.summary.utils

import android.util.Log
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardWithPlayers
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.HdcpParHoleHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.PlayerHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.HDCP_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.PAR_HEADER
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.updatePlayersTeamScoreCells
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
    val nineGames = scoreCardWithPlayers.playerRecords.size == 3
    for (idx in scoreCardWithPlayers.playerRecords.indices) { // player name and handicap
        state.playerHeading += PlayerHeading(
            idx,
            mName = scoreCardWithPlayers.playerRecords[idx].mName,
            mHdcp = scoreCardWithPlayers.playerRecords[idx].mHandicap,
            mScore = scoreCardWithPlayers.playerRecords[idx].mScore,
            mTeamHole = scoreCardWithPlayers.playerRecords[idx].mTeamHole,
            mGameNines = nineGames
        ) // add the player's name to the score card
    }
//    if (parCell != null) {
//        for (currentHole in parCell.mHole.indices) {
//            updatePlayersTeamScoreCells(        // fill in the team used and score fields
//                currentHole,
//                state.teamUsedHeading,
//                state.playerHeading
//            )
//        }
//    }


}