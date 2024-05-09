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

fun changeDisplayScreenMode(currentScreenMode: Int): Int {
    var newScreenMode: Int = DISPLAY_MODE_GROSS

    when (currentScreenMode) {
        DISPLAY_MODE_GROSS -> {newScreenMode = DISPLAY_MODE_NET}
        DISPLAY_MODE_NET -> {newScreenMode = DISPLAY_MODE_POINT_QUOTA}
        DISPLAY_MODE_POINT_QUOTA -> {newScreenMode = DISPLAY_MODE_STABLEFORD}
        DISPLAY_MODE_9_GAME -> {newScreenMode = DISPLAY_MODE_GROSS}
        DISPLAY_MODE_STABLEFORD -> {newScreenMode = DISPLAY_MODE_GROSS}
    }

    return (newScreenMode)
}
fun screenModeText(currentScreenMode: Int):String{
    var newScreenMode: String = "Gross"
    when (currentScreenMode) {
        DISPLAY_MODE_GROSS -> {newScreenMode = "Net"}
        DISPLAY_MODE_NET -> {newScreenMode = "Pt Quote"}
        DISPLAY_MODE_POINT_QUOTA -> {newScreenMode = "Stableford"}
        DISPLAY_MODE_9_GAME -> {newScreenMode = "Gross"}
        DISPLAY_MODE_STABLEFORD -> {newScreenMode = "Gross"}
    }
    return (newScreenMode)
}

fun ScoreCardViewModel.highLiteTotalColumn(displayColor: Long) {
    val holeHeading: HdcpParHoleHeading? =
        state.hdcpParHoleHeading.find { it.vinTag == HOLE_HEADER }
    if (holeHeading != null) {
        holeHeading.mColor = Color(displayColor)
    }
}


fun updateNetAndGrossScoreCells(
    hdcpParHoleHeading: List<HdcpParHoleHeading>,
    currentHole: Int,
    teamUsedHeading: List<TeamUsedHeading>,
    playerHeading: List<PlayerHeading>
) {
    var teamPlayerScore: TeamUsedHeading? =
        teamUsedHeading.find { it.vinTag == TEAM_HEADER }  // total hole score for selected player
    val holeHdcp = hdcpParHoleHeading.find { it.vinTag == HDCP_HEADER }
    var teamUsedHeading: TeamUsedHeading? =
        teamUsedHeading.find { it.vinTag == USED_HEADER }  // total hole score for selected player

    if (teamPlayerScore != null && holeHdcp != null && teamUsedHeading != null) {
        teamPlayerScore.mHole[currentHole] =
            0      // keeps track of the scores that are used by players
        teamUsedHeading.mHole[currentHole] = 0      // keeps track of the player scores are used

        for (player in playerHeading) {
            val teamScore = player.mScore[currentHole] and TEAM_SCORE_MASK
            val strokeForHole = getPlayerStrokesForHole(holeHdcp, player.mHdcp, currentHole)
            when (teamScore) {
                TEAM_GROSS_SCORE -> {
                    teamPlayerScore.mHole[currentHole] += player.mScore[currentHole] and JUST_RAW_SCORE
                    teamUsedHeading.mHole[currentHole] += 1
                }

                TEAM_NET_SCORE -> { // using the player's net score, need to subtract stroke for this hole
                    teamPlayerScore.mHole[currentHole] += (player.mScore[currentHole] and JUST_RAW_SCORE) - strokeForHole
                    teamUsedHeading.mHole[currentHole] += 1
                }

                TEAM_GROSS_SCORE + DOUBLE_TEAM_SCORE -> {
                    teamPlayerScore.mHole[currentHole] += (player.mScore[currentHole] and JUST_RAW_SCORE) * 2
                    teamUsedHeading.mHole[currentHole] += 1
                }

                TEAM_NET_SCORE + DOUBLE_TEAM_SCORE -> { // using the player's net score, need to subtract stroke for this hole X 2
                    teamPlayerScore.mHole[currentHole] += ((player.mScore[currentHole] and JUST_RAW_SCORE) - strokeForHole) * 2
                    teamUsedHeading.mHole[currentHole] += 1
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
    state = state.copy(mTee = scoreCardRecord.mTee)

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
            mScore = scoreCardWithPlayers.playerRecords[idx].mScore
        ) // add the player's name to the score card
    }
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

