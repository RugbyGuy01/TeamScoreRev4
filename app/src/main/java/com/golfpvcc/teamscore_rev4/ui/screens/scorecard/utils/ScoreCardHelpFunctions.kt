package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardWithPlayers
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.HdcpParHoleHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.PlayerHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.ScoreCardViewModel
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.TeamUsedHeading
import com.golfpvcc.teamscore_rev4.ui.screens.teamScore
import com.golfpvcc.teamscore_rev4.utils.DOUBLE_TEAM_SCORE
import com.golfpvcc.teamscore_rev4.utils.HOLE_ARRAY_SIZE
import com.golfpvcc.teamscore_rev4.utils.JUST_RAW_SCORE
import com.golfpvcc.teamscore_rev4.utils.PLAYER_HDCP
import com.golfpvcc.teamscore_rev4.utils.PLAYER_STROKES_3
import com.golfpvcc.teamscore_rev4.utils.TEAM_DOUBLE_GROSS_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_DOUBLE_NET_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_GROSS_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_NET_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_SCORE_MASK
import com.golfpvcc.teamscore_rev4.utils.TEAM_SINGLE_GROSS_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_SINGLE_NET_SCORE


fun ScoreCardViewModel.highLiteTotalColumn(displayColor: Long) {
    val holeHeading: HdcpParHoleHeading? =
        state.hdcpParHoleHeading.find { it.vinTag == HOLE_HEADER }
    if (holeHeading != null) {
        holeHeading.mColor = Color(displayColor)
    }
}

// user just enter the player scores - need to update the mDisplay score with current display mode
fun updateNetAndGrossScoreCells(
    hdcpParHoleHeading: List<HdcpParHoleHeading>,
    currentHole: Int,
    teamUsedHeading: List<TeamUsedHeading>,
    playerHeading: List<PlayerHeading>
) {
    val teamPlayerScore: TeamUsedHeading? =
        teamUsedHeading.find { it.vinTag == TEAM_HEADER }  // total hole score for selected player
    val holeHdcp = hdcpParHoleHeading.find { it.vinTag == HDCP_HEADER }
    val teamUsed: TeamUsedHeading? =
        teamUsedHeading.find { it.vinTag == USED_HEADER }  // total hole score for selected player

    if (teamPlayerScore != null && holeHdcp != null && teamUsed != null) {
        teamPlayerScore.mHole[currentHole] = 0 //  scores that are used by players
        teamUsed.mHole[currentHole] = 0      // keeps track of the player scores are used

        for (player in playerHeading) {
            val teamScore = teamScore(player.mDisplayScore[currentHole])
            if (teamScore > 0) {
                teamPlayerScore.mHole[currentHole] += teamScore
                teamUsed.mHole[currentHole] += 1
            }
        }
    }
}


fun totalScore(holes: IntArray, mStartingCell: Int, mEndingCell: Int, useMask:Boolean): String {
    var mTotal: Int = 0
    for (idx in mStartingCell until mEndingCell) {
        Log.d("VIN", "totalScore hole value ${holes[idx]} ")
        if(useMask)
            mTotal += (holes[idx] and JUST_RAW_SCORE)   // player's score cells also include strokes
        else
            mTotal += holes[idx]
    }
    Log.d("VIN", "getTotalForNineCell sum $mTotal")
//    return if (mTotal == 0) " " else mTotal.toString()
    return  mTotal.toString()
}

fun ScoreCardViewModel.updateScoreCardState(scoreCardWithPlayers: ScoreCardWithPlayers) {
    val scoreCardRecord: ScoreCardRecord = scoreCardWithPlayers.scoreCardRecord

    state = state.copy(mCourseName = scoreCardRecord.mCourseName)
    state = state.copy(mCourseId = scoreCardRecord.mCourseId)
    state = state.copy(mCurrentHole = (scoreCardRecord.mCurrentHole))   // zero based
    state = state.copy(mTee = scoreCardRecord.mTee)
    Log.d("VIN1", "updateScoreCardState read records - mDisplayScore")
    val parCell: HdcpParHoleHeading? = state.hdcpParHoleHeading.find { it.vinTag == PAR_HEADER }
    val hdcpCell: HdcpParHoleHeading? = state.hdcpParHoleHeading.find { it.vinTag == HDCP_HEADER }
    if (parCell != null && hdcpCell != null) {
        parCell.mHole = scoreCardRecord.mPar
        hdcpCell.mHole = scoreCardRecord.mHandicap
    }

    for (idx in scoreCardWithPlayers.playerRecords.indices) { // player name and handicap
        state.playerHeading += PlayerHeading(
            idx,
            mName = scoreCardWithPlayers.playerRecords[idx].mName,
            mHdcp = scoreCardWithPlayers.playerRecords[idx].mHandicap,
            mScore = scoreCardWithPlayers.playerRecords[idx].mScore,
            mDisplayScore = scoreCardWithPlayers.playerRecords[idx].mScore.copyOf(HOLE_ARRAY_SIZE)  // test.mScore = junk.copyOf(junk.size)
        ) // add the player's name to the score card
        if (hdcpCell != null) {
            setDisplayCellsWithHdcp(
                state.playerHeading[idx],
                hdcpCell
            ) // ADD THE STROKE MASK TO THE DISPLAY CELL ARRAY
        }
    }
    Log.d("VIN1", "updateScoreCardState player record count ${state.playerHeading.size}")
    if (parCell != null) {
        for (currentHole in parCell.mHole.indices) {
            updateNetAndGrossScoreCells(        // fill in the team used and score fields
                state.hdcpParHoleHeading,
                currentHole,
                state.teamUsedHeading,
                state.playerHeading
            )
        }
    }
}

fun setDisplayCellsWithHdcp(
    playerHeading: PlayerHeading,
    hdcpCell: HdcpParHoleHeading
) {
    var strokeForHole: Int
    var holeStrokeMask: Int
                                            // set player's strokes in the player's mDisplay score
    for (idx in playerHeading.mDisplayScore.indices) {
        strokeForHole = getPlayerStrokesForHole(hdcpCell, playerHeading.mHdcp, idx)
        holeStrokeMask = getHoleStrokeMask(strokeForHole)
        playerHeading.mDisplayScore[idx] = playerHeading.mDisplayScore[idx] or (PLAYER_HDCP and holeStrokeMask)
    }

}

fun getPlayerStrokesForHole(
    holeHdcp: HdcpParHoleHeading,
    playerHdcp: String,
    currentHole: Int
): Int {
    var playerIntHdcp = playerHdcp.toInt()
    var strokesForHole: Int = 0
    val currentHoleHandicap = holeHdcp.mHole[currentHole]
    if (currentHoleHandicap <= playerIntHdcp) {
        strokesForHole = 1
        playerIntHdcp -= 18
        if (currentHoleHandicap <= playerIntHdcp) {
            strokesForHole = 2
        }
    }

    return (strokesForHole)
}

fun setBoardColorForPlayerTeamScore(playerScore: Int): Color {
    val teamScore = playerScore and TEAM_SCORE_MASK
    var bordColor: Color = Color.Transparent
    when (teamScore) {
        TEAM_GROSS_SCORE -> {
            bordColor = Color(TEAM_SINGLE_GROSS_SCORE)
        }

        TEAM_NET_SCORE -> {
            bordColor = Color(TEAM_SINGLE_NET_SCORE)
        }

        TEAM_GROSS_SCORE + DOUBLE_TEAM_SCORE -> {
            bordColor = Color(TEAM_DOUBLE_GROSS_SCORE)
        }

        TEAM_NET_SCORE + DOUBLE_TEAM_SCORE -> {
            bordColor = Color(TEAM_DOUBLE_NET_SCORE)
        }
    }
    return (bordColor)
}
