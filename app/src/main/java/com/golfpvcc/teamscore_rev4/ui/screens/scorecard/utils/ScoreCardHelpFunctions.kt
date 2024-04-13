package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils

import android.util.Log
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardWithPlayers
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.HdcpParHoleHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.PlayerHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.ScoreCardViewModel
import com.golfpvcc.teamscore_rev4.utils.JUST_RAW_SCORE


fun totalScore(holes: IntArray, mStartingCell: Int, mEndingCell: Int): String {
    var mTotal :Int = 0
    for (idx in mStartingCell until mEndingCell) {
        mTotal += (holes[idx] and JUST_RAW_SCORE)
    }
    Log.d("VIN", "getTotalForNineCell sum $mTotal")
    return if (mTotal == 0) " " else mTotal.toString()
}

fun ScoreCardViewModel.updateScoreCardState(scoreCardWithPlayers: ScoreCardWithPlayers) {
    val scoreCardRecord: ScoreCardRecord = scoreCardWithPlayers.scoreCardRecord

    state = state.copy(mCourseName = scoreCardRecord.mCourseName)
    state = state.copy(mCurrentHole = (scoreCardRecord.mCurrentHole))   // zero based

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
            idx,
            mName = scoreCardWithPlayers.playerRecords[idx].mName,
            mHdcp = scoreCardWithPlayers.playerRecords[idx].mHandicap,
            mScore = scoreCardWithPlayers.playerRecords[idx].mScore
        ) // add the player's name to the score card
    }
}
