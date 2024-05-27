package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardWithPlayers
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.HdcpParHoleHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.PlayerHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.ScoreCardViewModel
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.TeamUsedHeading
import com.golfpvcc.teamscore_rev4.utils.DOUBLE_TEAM_MASK
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


    val parCellsTest = getHoleParCells()
    val hdcpCellsTest = getHoleHdcpCells()

    if (parCells != null) {
        Log.d("VIN", "parCellsTest $parCellsTest and parCells.mHole ${parCells.mHole}")
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
            setPlayerStrokeHoles(state.playerHeading[idx], hdcpCells.mHole)
        }
    }
    Log.d("VIN1", "updateScoreCardState player record count ${state.playerHeading.size}")
    if (parCells != null) {
        refreshScoreCard(parCells.mHole)
    }
}

fun ScoreCardViewModel.refreshScoreCard(parCells: IntArray) {
    for (currentHole in parCells.indices) {
        updatePlayersTeamScoreCells(        // read record - fill in the team used and score fields
            state.mDisplayScreenMode,
            currentHole,        // getParForHole
            parCells[currentHole],
            state.playerHeading
        )
    }
}

// user just enter the all of the player scores - need to update the mDisplay score with current display mode
// Also called by just read score card record
fun ScoreCardViewModel.updatePlayersTeamScoreCells(
    displayScreenMode: Int,
    currentHole: Int,
    holePar: Int,
    playerHeading: List<PlayerHeading>,
) {
    val teamPlayerScoreCells = getTeamPlayerScoreCells() // total hole score for selected player
    val teamUsedCells = getTeamUsedCells() // total hole score for selected player

    teamPlayerScoreCells[currentHole] = 0 //  scores that are used by players
    teamUsedCells[currentHole] = 0      // keeps track of the player scores are used

    for (player in playerHeading) {
        if (0 < player.mScore[currentHole]) {
            updatePlayerDisplayScore(player, displayScreenMode, currentHole, holePar)
        }
        if (0 < player.mTeamHole[currentHole]) {
            updateTeamDisplayScore(
                displayScreenMode,
                teamPlayerScoreCells,
                teamUsedCells,
                currentHole,
                holePar,
                player
            )
        }
    }
}

fun updateTeamDisplayScore(
    displayScreenMode: Int,
    teamPlayerScoreCells: IntArray,
    teamUsedCells: IntArray,
    currentHole: Int,
    holePar: Int,
    player: PlayerHeading,
) {
    var teamScore = player.mDisplayScore[currentHole]

    when (displayScreenMode) {
        DISPLAY_MODE_GROSS -> {
            displayTeamGrossScore(teamPlayerScoreCells, teamUsedCells, currentHole, holePar, player)
        }

        DISPLAY_MODE_NET -> {
            displayTeamNetScore(teamPlayerScoreCells, teamUsedCells, currentHole, player)
        }

        DISPLAY_MODE_POINT_QUOTA -> {
        }

        DISPLAY_MODE_9_GAME -> {
        }

        DISPLAY_MODE_STABLEFORD -> {
        }
    }
}

fun displayTeamGrossScore(
    teamPlayerScoreCells: IntArray,
    teamUsedCells: IntArray,
    currentHole: Int,
    holePar: Int,
    player: PlayerHeading,
) {
    var teamScore = player.mDisplayScore[currentHole] - holePar
    teamUsedCells[currentHole] += 1
    when (player.mTeamHole[currentHole]) {
        TEAM_NET_SCORE -> {
            teamScore += (-player.mStokeHole[currentHole])
        }// adjust for strokes
        TEAM_NET_SCORE + DOUBLE_TEAM_SCORE -> {
            teamScore += (-player.mStokeHole[currentHole]) * 2
            teamUsedCells[currentHole] += 1
        }
        TEAM_GROSS_SCORE + DOUBLE_TEAM_SCORE -> {
            teamScore += teamScore
            teamUsedCells[currentHole] += 1
        }
        else -> { }
    }
    teamPlayerScoreCells[currentHole] += teamScore
}

fun displayTeamNetScore(
    teamPlayerScoreCells: IntArray,
    teamUsedCells: IntArray,
    currentHole: Int,
    player: PlayerHeading,
) {
    var teamScore = player.mDisplayScore[currentHole]

    teamPlayerScoreCells[currentHole] += teamScore
    teamUsedCells[currentHole] += 1

    if (player.mTeamHole[currentHole] > DOUBLE_TEAM_MASK) { // double the score
        teamUsedCells[currentHole] += 1
        teamPlayerScoreCells[currentHole] += teamScore
    }
}

fun ScoreCardViewModel.updatePlayerDisplayScore(
    playerHeading: PlayerHeading,
    displayScreenMode: Int,
    currentHole: Int,
    holePar: Int,
) {
    when (displayScreenMode) {
        DISPLAY_MODE_GROSS -> {
            if (0 < playerHeading.mScore[currentHole]) {
                playerHeading.mDisplayScore[currentHole] =
                    playerHeading.mScore[currentHole]
            }
        }

        DISPLAY_MODE_NET -> {
            playerHeading.mDisplayScore[currentHole] =
                playerHeading.mScore[currentHole] - playerHeading.mStokeHole[currentHole]
        }

        DISPLAY_MODE_POINT_QUOTA -> {
//            if (0 < playerHeading.mScore[currentHole]) {
//
//            }
        }

        DISPLAY_MODE_9_GAME -> {
        }

        DISPLAY_MODE_STABLEFORD -> {
        }
    }
}

fun getGrossDisplayScore(currentHole: Int, holePar: Int, player: PlayerHeading): Int {
    val displayScore: Int = player.mScore[currentHole] - holePar
    return (displayScore)
}

fun getNetScore(currentHole: Int, player: PlayerHeading): Int {
    val displayScore: Int = player.mScore[currentHole] - player.mStokeHole[currentHole]

    return (displayScore)
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

fun setPlayerStrokeHoles(
    playerHeading: PlayerHeading,
    hdcpHoles: IntArray,
) {
    var strokeForHole: Int
    // set player's strokes in the player's mDisplay score
    for (idx in playerHeading.mStokeHole.indices) {
        strokeForHole = getPlayerStrokesForHole(hdcpHoles, playerHeading.mHdcp, idx)
        playerHeading.mStokeHole[idx] = strokeForHole
    }
}

fun getPlayerStrokesForHole(
    holeHdcp: IntArray,
    playerHdcp: String,
    currentHole: Int,
): Int {
    var playerIntHdcp = playerHdcp.toInt()
    var strokesForHole: Int = 0
    val currentHoleHandicap = holeHdcp[currentHole]
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
