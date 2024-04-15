package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardWithPlayers
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.HdcpParHoleHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.PlayerHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.ScoreCardViewModel
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.TeamUsedHeading
import com.golfpvcc.teamscore_rev4.utils.DOUBLE_TEAM_SCORE
import com.golfpvcc.teamscore_rev4.utils.JUST_RAW_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_DOUBLE_GROSS_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_DOUBLE_NET_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_GROSS_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_NET_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_SCORE_MASK
import com.golfpvcc.teamscore_rev4.utils.TEAM_SINGLE_GROSS_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_SINGLE_NET_SCORE

fun updateNetAndGrossScoreCells(
    hdcpParHoleHeading: List<HdcpParHoleHeading>,
    currentHole: Int,
    teamUsedHeading: List<TeamUsedHeading>,
    playerHeading: List<PlayerHeading>
) {
    var teamPlayerScore: TeamUsedHeading? =
        teamUsedHeading.find { it.vinTag == TEAM_HEADER }  // total hole score for selected player
    val holeHdcp = hdcpParHoleHeading.find { it.vinTag == HDCP_HEADER }

    if (teamPlayerScore != null && holeHdcp != null) {
        teamPlayerScore.mHole[currentHole] = 0

        for (player in playerHeading) {
            val teamScore = player.mScore[currentHole] and TEAM_SCORE_MASK
            val strokeForHole = getPlayerStrokesForHole(holeHdcp, player.mHdcp, currentHole)
            when (teamScore) {
                TEAM_GROSS_SCORE -> {
                    teamPlayerScore.mHole[currentHole] += player.mScore[currentHole] and JUST_RAW_SCORE
                }

                TEAM_NET_SCORE -> { // using the player's net score, need to subtract stroke for this hole
                    teamPlayerScore.mHole[currentHole] += (player.mScore[currentHole] and JUST_RAW_SCORE) - strokeForHole
                }

                TEAM_GROSS_SCORE + DOUBLE_TEAM_SCORE -> {
                    teamPlayerScore.mHole[currentHole] += (player.mScore[currentHole] and JUST_RAW_SCORE) * 2
                }

                TEAM_NET_SCORE + DOUBLE_TEAM_SCORE -> { // using the player's net score, need to subtract stroke for this hole X 2
                    teamPlayerScore.mHole[currentHole] += ((player.mScore[currentHole] and JUST_RAW_SCORE) - strokeForHole) * 2
                }
            }
        }
    }
}


fun totalScore(holes: IntArray, mStartingCell: Int, mEndingCell: Int): String {
    var mTotal: Int = 0
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
