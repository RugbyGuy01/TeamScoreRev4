package com.golfpvcc.teamscore_rev4.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golfpvcc.teamscore_rev4.database.model.PointsRecord
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.PlayerHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.displayTeamGrossScore
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.displayTeamNetScore
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.getPointQuoteKey
import com.golfpvcc.teamscore_rev4.ui.screens.summary.PlayerSummary
import com.golfpvcc.teamscore_rev4.ui.screens.summary.TeamPoints
import com.golfpvcc.teamscore_rev4.utils.BACK_NINE_TOTAL_DISPLAYED
import com.golfpvcc.teamscore_rev4.utils.DOUBLE_TEAM_SCORE
import com.golfpvcc.teamscore_rev4.utils.FRONT_NINE_DISPLAY
import com.golfpvcc.teamscore_rev4.utils.FRONT_NINE_TOTAL_DISPLAYED
import com.golfpvcc.teamscore_rev4.utils.PLAYER_STROKES_1
import com.golfpvcc.teamscore_rev4.utils.PLAYER_STROKES_2
import com.golfpvcc.teamscore_rev4.utils.PLAYER_STROKES_3
import com.golfpvcc.teamscore_rev4.utils.SUMMARY_BUTTON_TEXT
import com.golfpvcc.teamscore_rev4.utils.TEAM_GROSS_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_NET_SCORE


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardButton(buttonText: String, onClick: () -> Unit) {

    Card(modifier = Modifier
        .wrapContentSize()
        .clip(RoundedCornerShape(4.dp))
        .padding(4.dp),
        border = BorderStroke(2.dp, Color.LightGray),
        onClick = { onClick() }
    ) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = buttonText,
            fontSize = SUMMARY_BUTTON_TEXT.sp,
        )
    }
}
/*
                team Score              Used Score
gross Score     gross - Par             Count
Net Score       gross - strokes         Count
Stableford      net score               add flagged pts
                table Lookup
Pt Quote        gross score             add flagged pts
                table Lookup

 */

fun getTeamScore(grossScore: Int, strokes: Int, teamScoreMask: Int): Int {
    val teamScoreResults: Int

    when (teamScoreMask) {
        TEAM_GROSS_SCORE -> {
            teamScoreResults = grossScore
        }

        TEAM_NET_SCORE -> {
            teamScoreResults = grossScore - strokes
        }

        TEAM_GROSS_SCORE + DOUBLE_TEAM_SCORE -> {
            teamScoreResults = grossScore * 2
        }

        TEAM_NET_SCORE + DOUBLE_TEAM_SCORE -> {
            teamScoreResults = (grossScore - strokes) * 2
        }

        else -> {
            teamScoreResults = 0
        }
    }
    return (teamScoreResults)
}

fun playerStokes(playerStrokes: Int): Int {
    val strokeResult: Int

    when (playerStrokes) {
        PLAYER_STROKES_1 -> {
            strokeResult = 1
        }

        PLAYER_STROKES_2 -> {
            strokeResult = 2
        }

        PLAYER_STROKES_3 -> {
            strokeResult = 3
        }

        else -> {
            strokeResult = 0
        }
    }
    return (strokeResult)
}

/*
    This function will get the front/back point quota total for this player used by the Summary screen of the app.
     */
fun getTotalPlayerPointQuota(
    whatNine: Int,
    playerHeading: PlayerSummary,
    holePar: IntArray,
    gamePointsTable: List<PointsRecord>,
): TeamPoints {
    val playerPtQuote = TeamPoints(0, 0)
    var start: Int = 0
    var stop: Int = 8

    if (whatNine == FRONT_NINE_DISPLAY) {
        playerHeading.mQuote = 0
    } else {
        start = 9
        stop = 17
    }
    for (idx in start..stop) {
        if (0 < playerHeading.mPlayer.mScore[idx]) {
            val playerScore = playerHeading.mPlayer.mScore[idx]
            val ptKey = getPointQuoteKey(playerScore, holePar[idx])
            val ptQuota = gamePointsTable.filter { it.mId == ptKey }
            if (ptQuota.isNotEmpty()) {
                val ptQuoteRec = ptQuota.first()
                playerHeading.mQuote += ptQuoteRec.mPoints
                playerPtQuote.teamTotalPoints += ptQuoteRec.mPoints
                playerPtQuote.teamUsedPoints += getTeamUsedScore(
                    playerHeading.mPlayer.mTeamHole[idx], ptQuoteRec.mPoints
                )
            }
        }
    }
    return (playerPtQuote)
}

fun getTeamUsedScore(teamUsedMask: Int, playerPoints: Int): Int {
    var teamUsedScore: Int = 0

    if (0 < teamUsedMask) {
        teamUsedScore = playerPoints

        if (teamUsedMask == (TEAM_GROSS_SCORE + DOUBLE_TEAM_SCORE)
            || teamUsedMask == (TEAM_NET_SCORE + DOUBLE_TEAM_SCORE)
        ) {
            teamUsedScore += playerPoints
        }
    }
    return (teamUsedScore)
}

fun getTotalPlayerStableford(
    whatNine: Int,
    playerHeading: PlayerSummary,
    holePar: IntArray,
    gamePointsTable: List<PointsRecord>,
): TeamPoints {
    val playerStableford = TeamPoints(0, 0)
    var start: Int = 0
    var stop: Int = 8

    if (whatNine == FRONT_NINE_DISPLAY) {
        playerHeading.mStableford = 0
    } else {
        start = 9
        stop = 17
    }
    for (idx in start..stop) {
        if (0 < playerHeading.mPlayer.mScore[idx]) {    // only if have a score
            val playerNetScore =
                playerHeading.mPlayer.mScore[idx] - playerHeading.mPlayer.mStokeHole[idx]

            val ptKey = getPointQuoteKey(playerNetScore, holePar[idx])
            val stableford = gamePointsTable.filter { it.mId == ptKey }
            if (stableford.isNotEmpty()) {
                val stablefordRec = stableford.first()
                playerHeading.mStableford += stablefordRec.mPoints
                Log.d(
                    "VIN",
                    "${playerHeading.mPlayer.mName}  Par ${holePar[idx]} Net score $playerNetScore points ${stablefordRec.mPoints}"
                )

                playerStableford.teamTotalPoints += stablefordRec.mPoints
                playerStableford.teamUsedPoints += getTeamUsedScore(
                    teamUsedMask = playerHeading.mPlayer.mTeamHole[idx],
                    playerPoints = stablefordRec.mPoints
                )
            }
        }
    }
    return (playerStableford)
}

fun getTotalPlayerScore(
    whatNine: Int,
    playerHeading: PlayerSummary,
    holePar: IntArray,
): TeamPoints {
    val teamOverUnderScore = TeamPoints(0, 0)
    var start: Int = 0
    var stop: Int = FRONT_NINE_TOTAL_DISPLAYED

    if (whatNine == FRONT_NINE_DISPLAY) {
        stop = FRONT_NINE_TOTAL_DISPLAYED
    } else {
        start = FRONT_NINE_DISPLAY
        stop = BACK_NINE_TOTAL_DISPLAYED
    }
    for (currentHole in start..stop) {
        if (0 < playerHeading.mPlayer.mScore[currentHole]) {    // only if have a score
            if (0 < playerHeading.mPlayer.mTeamHole[currentHole]) {
                teamOverUnderScore.teamTotalPoints += displayTeamNetScore(
                    teamUsedCells = playerHeading.mPlayer.mTeamHole,
                    currentHole = currentHole,
                    player = playerHeading.mPlayer
                )

                teamOverUnderScore.teamUsedPoints += getUnderOverScore(
                    player = playerHeading.mPlayer,
                    currentHole = currentHole,
                    holePar = holePar[currentHole]
                )
            }
        }
    }

    return (teamOverUnderScore)
}
fun getUnderOverScore(player: PlayerHeading, currentHole: Int, holePar: Int): Int {
    var teamScore: Int = player.mScore[currentHole] - holePar

    when (player.mTeamHole[currentHole]) {
        TEAM_GROSS_SCORE -> {
        }

        TEAM_NET_SCORE -> {
            teamScore -= player.mStokeHole[currentHole]
        }

        TEAM_NET_SCORE + DOUBLE_TEAM_SCORE -> {
            teamScore -= player.mStokeHole[currentHole]
            teamScore *= 2
        }

        TEAM_GROSS_SCORE + DOUBLE_TEAM_SCORE -> {
            teamScore *= 2
        }

        else -> {}
    }
    return (teamScore)
}