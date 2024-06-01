package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardWithPlayers
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.HdcpParHoleHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.PlayerHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.ScoreCardViewModel
import com.golfpvcc.teamscore_rev4.utils.ALBATROSS_ON_HOLE
import com.golfpvcc.teamscore_rev4.utils.BIRDIES_ON_HOLE
import com.golfpvcc.teamscore_rev4.utils.BOGGY_ON_HOLE
import com.golfpvcc.teamscore_rev4.utils.DOUBLE_ON_HOLE
import com.golfpvcc.teamscore_rev4.utils.DOUBLE_TEAM_MASK
import com.golfpvcc.teamscore_rev4.utils.DOUBLE_TEAM_SCORE
import com.golfpvcc.teamscore_rev4.utils.EAGLE_ON_HOLE
import com.golfpvcc.teamscore_rev4.utils.OTHER_ON_HOLE
import com.golfpvcc.teamscore_rev4.utils.PAR_ON_HOLE
import com.golfpvcc.teamscore_rev4.utils.PQ_ALBATROSS
import com.golfpvcc.teamscore_rev4.utils.PQ_BIRDIES
import com.golfpvcc.teamscore_rev4.utils.PQ_BOGGY
import com.golfpvcc.teamscore_rev4.utils.PQ_DOUBLE
import com.golfpvcc.teamscore_rev4.utils.PQ_EAGLE
import com.golfpvcc.teamscore_rev4.utils.PQ_END
import com.golfpvcc.teamscore_rev4.utils.PQ_OTHER
import com.golfpvcc.teamscore_rev4.utils.PQ_PAR
import com.golfpvcc.teamscore_rev4.utils.TEAM_DOUBLE_GROSS_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_DOUBLE_NET_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_GROSS_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_NET_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_SINGLE_GROSS_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_SINGLE_NET_SCORE


fun ScoreCardViewModel.highLiteTotalColumn(displayColor: Long) {
    val holeHeading: HdcpParHoleHeading? =
        state.mHdcpParHoleHeading.find { it.vinTag == HOLE_HEADER }
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

    val parCells: HdcpParHoleHeading? = state.mHdcpParHoleHeading.find { it.vinTag == PAR_HEADER }
    val hdcpCells: HdcpParHoleHeading? = state.mHdcpParHoleHeading.find { it.vinTag == HDCP_HEADER }
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
    state.mGameNines = (numberOfPlayers == 3)
    for (idx in scoreCardWithPlayers.playerRecords.indices) { // player name and handicap
        state.mPlayerHeading += PlayerHeading(
            idx,
            mName = scoreCardWithPlayers.playerRecords[idx].mName,
            mHdcp = scoreCardWithPlayers.playerRecords[idx].mHandicap,
            mScore = scoreCardWithPlayers.playerRecords[idx].mScore,
            mTeamHole = scoreCardWithPlayers.playerRecords[idx].mTeamHole,
        ) /* add the player's name to the score card */

        if (hdcpCells != null) {
            setPlayerStrokeHoles(state.mPlayerHeading[idx], hdcpCells.mHole)
        }
    }
    Log.d("VIN1", "updateScoreCardState player record count ${state.mPlayerHeading.size}")
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
            state.mPlayerHeading
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
    var nineGameScores = NineGame()

    teamPlayerScoreCells[currentHole] = 0 //  scores that are used by players
    teamUsedCells[currentHole] = 0      // keeps track of the player scores are used
    nineGameScores.ClearTotals()

    for (player in playerHeading) {
        if (0 < player.mScore[currentHole]) {
            updatePlayerDisplayScore(
                player,
                displayScreenMode,
                currentHole,
                holePar,
                nineGameScores
            )
        }

        updateTeamDisplayScore(
            displayScreenMode,
            teamPlayerScoreCells,
            teamUsedCells,
            currentHole,
            holePar,
            player
        )
    }
    if (displayScreenMode == DISPLAY_MODE_9_GAME) {
        nineGameScores.sort9Scores()    // calculate player's scores
        for (player in playerHeading) {
            player.mDisplayScore[currentHole] = nineGameScores.get9GameScore(player.vinTag)
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
    val teamUsedMask = player.mTeamHole[currentHole]

    when (displayScreenMode) {
        DISPLAY_MODE_GROSS -> {
            if (0 < teamUsedMask)
                displayTeamGrossScore(
                    teamPlayerScoreCells,
                    teamUsedCells,
                    currentHole,
                    holePar,
                    player
                )
        }

        DISPLAY_MODE_NET -> {
            if (0 < teamUsedMask)
                displayTeamNetScore(teamPlayerScoreCells, teamUsedCells, currentHole, player)
        }

        DISPLAY_MODE_STABLEFORD,
        DISPLAY_MODE_POINT_QUOTA,
        -> {   // used field are score flags value, Team are all player score added
            teamPlayerScoreCells[currentHole] += player.mDisplayScore[currentHole]

            if (0 < teamUsedMask) {
                teamUsedCells[currentHole] += player.mDisplayScore[currentHole]

                if (teamUsedMask == (TEAM_GROSS_SCORE + DOUBLE_TEAM_SCORE)
                    || teamUsedMask == (TEAM_NET_SCORE + DOUBLE_TEAM_SCORE)
                ) {
                    teamUsedCells[currentHole] += player.mDisplayScore[currentHole]
                }
            }
        }

        DISPLAY_MODE_9_GAME -> {
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
        TEAM_GROSS_SCORE -> {
        }

        TEAM_NET_SCORE -> {
            teamScore = player.mDisplayScore[currentHole] - holePar - player.mStokeHole[currentHole]
        }

        TEAM_NET_SCORE + DOUBLE_TEAM_SCORE -> {
            teamScore = player.mDisplayScore[currentHole] - holePar - player.mStokeHole[currentHole]
            teamScore *= 2
            teamUsedCells[currentHole] += 1
        }

        TEAM_GROSS_SCORE + DOUBLE_TEAM_SCORE -> {
            teamScore *= 2
            teamUsedCells[currentHole] += 1
        }

        else -> {}
    }
    teamPlayerScoreCells[currentHole] += teamScore
}

fun displayTeamNetScore(
    teamPlayerScoreCells: IntArray,
    teamUsedCells: IntArray,
    currentHole: Int,
    player: PlayerHeading,
) {
    var teamScore = player.mScore[currentHole]
    teamUsedCells[currentHole] += 1

    when (player.mTeamHole[currentHole]) {
        TEAM_GROSS_SCORE -> {
        }

        TEAM_GROSS_SCORE + DOUBLE_TEAM_SCORE -> {
            teamScore *= 2
            teamUsedCells[currentHole] += 1
        }

        TEAM_NET_SCORE -> {
            teamScore -= player.mStokeHole[currentHole]
        }

        TEAM_NET_SCORE + DOUBLE_TEAM_SCORE -> {
            teamScore -= player.mStokeHole[currentHole]
            teamScore *= 2
            teamUsedCells[currentHole] += 1
        }

        else -> {}
    }
    teamPlayerScoreCells[currentHole] += teamScore
}

fun ScoreCardViewModel.updatePlayerDisplayScore(
    playerHeading: PlayerHeading,
    displayScreenMode: Int,
    currentHole: Int,
    holePar: Int,
    nineGameScores: NineGame,
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
            if (0 < playerHeading.mScore[currentHole]) {
                val ptKey = getPointQuoteKey(playerHeading.mScore[currentHole], holePar)
                val ptQuota = state.mGamePointsTable.filter { it.mId == ptKey }
                if (ptQuota.isNotEmpty()) {
                    val ptQuoteRec = ptQuota.first()
                    playerHeading.mDisplayScore[currentHole] = ptQuoteRec.mPoints
                }
            }
        }

        DISPLAY_MODE_STABLEFORD -> {
            if (0 < playerHeading.mScore[currentHole]) {
                val playerNetScore =
                    playerHeading.mScore[currentHole] - playerHeading.mStokeHole[currentHole]
                val ptKey = getPointQuoteKey(playerNetScore, holePar)
                val ptQuota = state.mGamePointsTable.filter { it.mId == ptKey }
                if (ptQuota.isNotEmpty()) {
                    val ptQuoteRec = ptQuota.first()
                    playerHeading.mDisplayScore[currentHole] = ptQuoteRec.mPoints
                }
            }
        }

        DISPLAY_MODE_9_GAME -> {
            val playerNetScore =
                playerHeading.mScore[currentHole] - playerHeading.mStokeHole[currentHole]
            nineGameScores.addPlayerGrossScore(playerHeading.vinTag, playerNetScore)
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

fun getPointQuoteKey(score: Int, parForHole: Int): Int {
    var pointQuoteKey: Int = PQ_ALBATROSS

    when (score - parForHole) {
        ALBATROSS_ON_HOLE -> pointQuoteKey = PQ_ALBATROSS

        EAGLE_ON_HOLE -> pointQuoteKey = PQ_EAGLE

        BIRDIES_ON_HOLE -> pointQuoteKey = PQ_BIRDIES

        PAR_ON_HOLE -> pointQuoteKey = PQ_PAR

        BOGGY_ON_HOLE -> pointQuoteKey = PQ_BOGGY

        DOUBLE_ON_HOLE -> pointQuoteKey = PQ_DOUBLE

        OTHER_ON_HOLE -> pointQuoteKey = PQ_OTHER
    }

    return (pointQuoteKey)
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
