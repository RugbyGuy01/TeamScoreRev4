package com.golfpvcc.teamscore_rev4.ui.screens

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
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.PlayerHeading
import com.golfpvcc.teamscore_rev4.utils.Constants
import com.golfpvcc.teamscore_rev4.utils.DOUBLE_TEAM_SCORE
import com.golfpvcc.teamscore_rev4.utils.PLAYER_STROKES_1
import com.golfpvcc.teamscore_rev4.utils.PLAYER_STROKES_2
import com.golfpvcc.teamscore_rev4.utils.PLAYER_STROKES_3
import com.golfpvcc.teamscore_rev4.utils.SUMMARY_BUTTON_TEXT
import com.golfpvcc.teamscore_rev4.utils.TEAM_GROSS_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_NET_SCORE


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardButton(buttonText: String,  onClick: () -> Unit) {

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

fun getTeamScore(grossScore:Int, strokes: Int, teamScoreMask:Int): Int {
    val teamScoreResults:Int

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
    val strokeResult:Int

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


fun getGamePointsScore(playerGrossNetScore: Int, par: Int, golfGamePoints: Map<Int, Int>): Int {
    var stablefordScore: Int
    var playerIndex: Int = playerGrossNetScore - par
    val defaultValue: Map.Entry<Int, Int> = golfGamePoints.entries.last()

    stablefordScore = golfGamePoints.getOrDefault(playerIndex, defaultValue.value)


    return (stablefordScore)
}