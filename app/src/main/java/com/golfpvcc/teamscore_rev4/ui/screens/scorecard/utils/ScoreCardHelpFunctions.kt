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
import com.golfpvcc.teamscore_rev4.utils.TEAM_DOUBLE_GROSS_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_DOUBLE_NET_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_GROSS_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_NET_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_SINGLE_GROSS_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_SINGLE_NET_SCORE


fun ScoreCardViewModel.highLiteTotalColumn(displayColor: Long) {
    val holeHeading: HdcpParHoleHeading? =
        state.hdcpParHoleHeading.find { it.vinTag == HOLE_HEADER }
    if (holeHeading != null) {
        holeHeading.mColor = Color(displayColor)
    }
}

fun ScoreCardViewModel.updateScoreCardState(scoreCardWithPlayers: ScoreCardWithPlayers) {
    val scoreCardRecord: ScoreCardRecord = scoreCardWithPlayers.scoreCardRecord

    state = state.copy(mCourseName = scoreCardRecord.mCourseName)
    state = state.copy(mCourseId = scoreCardRecord.mCourseId)
    state = state.copy(mCurrentHole = (scoreCardRecord.mCurrentHole))   // zero based
    state = state.copy(mTee = scoreCardRecord.mTee)
    Log.d("VIN1", "updateScoreCardState read records - mDisplayScore")
    val parCells: HdcpParHoleHeading? = state.hdcpParHoleHeading.find { it.vinTag == PAR_HEADER }
    val hdcpCells: HdcpParHoleHeading? = state.hdcpParHoleHeading.find { it.vinTag == HDCP_HEADER }
    if (parCells != null && hdcpCells != null) {
        parCells.mHole = scoreCardRecord.mPar
        hdcpCells.mHole = scoreCardRecord.mHandicap
    }
    var numberOfPlayers = scoreCardWithPlayers.playerRecords.size
    for (idx in scoreCardWithPlayers.playerRecords.indices) { // player name and handicap
        state.playerHeading += PlayerHeading(
            idx,
            mName = scoreCardWithPlayers.playerRecords[idx].mName,
            mHdcp = scoreCardWithPlayers.playerRecords[idx].mHandicap,
            mScore = scoreCardWithPlayers.playerRecords[idx].mScore,
            mTeamHole = scoreCardWithPlayers.playerRecords[idx].mTeamHole,
            mGameNines = numberOfPlayers == 3
        ) /* add the player's name to the score card */
        if (hdcpCells != null) {
            setDisplayCellsWithHdcp(state.playerHeading[idx], hdcpCells)
        }
    }
    Log.d("VIN1", "updateScoreCardState player record count ${state.playerHeading.size}")
    if (parCells != null) {
        for (currentHole in parCells.mHole.indices) {
            updatePlayersTeamScoreCells(        // read record - fill in the team used and score fields
                state.mDisplayScreenMode,
                currentHole,
                state.playerHeading
            )
        }
    }
}

// user just enter the all of the player scores - need to update the mDisplay score with current display mode
fun ScoreCardViewModel.updatePlayersTeamScoreCells(
    displayScreenMode: Int,
    currentHole: Int,
    playerHeading: List<PlayerHeading>,
) {
    val teamPlayerScore: TeamUsedHeading? =
        state.teamUsedHeading.find { it.vinTag == TEAM_HEADER }  // total hole score for selected player
    val teamUsed: TeamUsedHeading? =
        state.teamUsedHeading.find { it.vinTag == USED_HEADER }  // total hole score for selected player

    if (teamPlayerScore != null && teamUsed != null) {
        teamPlayerScore.mHole[currentHole] = 0 //  scores that are used by players
        teamUsed.mHole[currentHole] = 0      // keeps track of the player scores are used

        for (player in playerHeading) {
            updateDisplayScore(player, displayScreenMode, currentHole)
            if (0 < player.mTeamHole[currentHole]) {
                when (displayScreenMode) {
                    DISPLAY_MODE_NET, DISPLAY_MODE_GROSS -> {
                        teamPlayerScore.mHole[currentHole] += player.mDisplayScore[currentHole]
                        if (player.mTeamHole[currentHole] > TEAM_NET_SCORE) {
                            teamUsed.mHole[currentHole] += 2
                        } else {
                            teamUsed.mHole[currentHole] += 1
                        }
                    }

                    DISPLAY_MODE_POINT_QUOTA -> {
                    }

                    DISPLAY_MODE_9_GAME -> {
                    }

                    DISPLAY_MODE_STABLEFORD -> {
                    }
                }
            }
        }
    }
}

fun ScoreCardViewModel.updateDisplayScore(
    playerHeading: PlayerHeading,
    displayScreenMode: Int,
    currentHole: Int,
) {
    when (displayScreenMode) {
        DISPLAY_MODE_GROSS -> {
            playerHeading.mDisplayScore[currentHole] = playerHeading.mScore[currentHole]
        }

        DISPLAY_MODE_NET -> {
            playerHeading.mDisplayScore[currentHole] =
                playerHeading.mScore[currentHole] - playerHeading.mStokeHole[currentHole]
        }

        DISPLAY_MODE_POINT_QUOTA -> {
        }

        DISPLAY_MODE_9_GAME -> {
        }

        DISPLAY_MODE_STABLEFORD -> {
        }
    }
}


fun getTotalScore(holes: IntArray, mStartingCell: Int, mEndingCell: Int): String {
    var mTotal: Int = 0
    for (idx in mStartingCell until mEndingCell) {
        Log.d("VIN", "totalScore hole value ${holes[idx]} ")
        mTotal += holes[idx]
    }
    Log.d("VIN", "getTotalForNineCell sum $mTotal")
    return mTotal.toString()
}

fun setDisplayCellsWithHdcp(
    playerHeading: PlayerHeading,
    hdcpHoles: HdcpParHoleHeading,
) {
    var strokeForHole: Int
    // set player's strokes in the player's mDisplay score
    for (idx in playerHeading.mStokeHole.indices) {
        strokeForHole = getPlayerStrokesForHole(hdcpHoles, playerHeading.mHdcp, idx)
        playerHeading.mStokeHole[idx] = strokeForHole
    }
}

fun getPlayerStrokesForHole(
    holeHdcp: HdcpParHoleHeading,
    playerHdcp: String,
    currentHole: Int,
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
    Log.d("VIN", "getPlayerStrokesForHole $strokesForHole")
    return (strokesForHole)
}

fun setBoardColorForPlayerTeamScore(teamScore: Int): Color {

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
