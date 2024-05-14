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
import com.golfpvcc.teamscore_rev4.utils.Constants
import com.golfpvcc.teamscore_rev4.utils.DOUBLE_TEAM_SCORE
import com.golfpvcc.teamscore_rev4.utils.JUST_RAW_SCORE
import com.golfpvcc.teamscore_rev4.utils.PLAYER_HDCP
import com.golfpvcc.teamscore_rev4.utils.PLAYER_STROKES_1
import com.golfpvcc.teamscore_rev4.utils.PLAYER_STROKES_2
import com.golfpvcc.teamscore_rev4.utils.PLAYER_STROKES_3
import com.golfpvcc.teamscore_rev4.utils.TEAM_GROSS_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_NET_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_SCORE_MASK


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
            fontSize = Constants.SUMMARY_BUTTON_TEXT.sp,
        )
    }
}

//test.mDisplayScore[2] = test.mDisplayScore[2] or (PLAYER_HDCP and PLAYER_STROKES_0)
//test.mDisplayScore[2] = test.mDisplayScore[2] or (TEAM_SCORE_MASK and TEAM_NET_SCORE)
fun teamScore(playerScore:Int): Int {
    val teamScoreResults:Int
    val score = playerScore and JUST_RAW_SCORE
    val teamScoreMask = playerScore and TEAM_SCORE_MASK
    val strokes = playerStokes(playerScore)

    when (teamScoreMask) {
        TEAM_GROSS_SCORE -> {
            teamScoreResults = score
        }

        TEAM_NET_SCORE -> {
            teamScoreResults = score - strokes
        }

        TEAM_GROSS_SCORE + DOUBLE_TEAM_SCORE -> {
            teamScoreResults = score * 2
        }
        TEAM_NET_SCORE + DOUBLE_TEAM_SCORE -> {
            teamScoreResults = (score - strokes) * 2
        }

        else -> {
            teamScoreResults = 0
        }
    }
    return (teamScoreResults)
}
fun playerStokes(playerStrokes: Int): Int {
    val strokeResult:Int
    val strokes = playerStrokes and PLAYER_HDCP

    when (strokes) {
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