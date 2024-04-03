package com.golfpvcc.teamscore_rev4.ui.screens.scorecard

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golfpvcc.teamscore_rev4.utils.Constants.COLUMN_TOTAL_WIDTH
import com.golfpvcc.teamscore_rev4.utils.Constants.SCORE_CARD_COURSE_NAME_TEXT
import com.golfpvcc.teamscore_rev4.utils.Constants.SCORE_CARD_TEXT
import com.golfpvcc.teamscore_rev4.utils.VIN_LIGHT_GRAY
import com.golfpvcc.teamscore_rev4.utils.VIN_HOLE_PLAYED

@Composable
fun FlipNineDisplay(scoreCardViewModel: ScoreCardViewModel) {
    val buttonText: String =
        if (scoreCardViewModel.state.mWhatNineIsBeingDisplayed) "Back Nine" else "Front Nine"
    Button(
        onClick = {
            scoreCardViewModel.flipFrontNineBackNine()
        },
        modifier = Modifier
            .height(40.dp),  //vpg
    ) {
        Text(text = buttonText)
    }
}

@Composable
fun DisplayMainScoreCard(
    scoreCardViewModel: ScoreCardViewModel
) {
    Column {
        DisplayCourseName(scoreCardViewModel)
        DisplayScoreCardHeader(scoreCardViewModel)
        DisplayScoreCardNames(scoreCardViewModel)
        DisplayScoreCardTeams(scoreCardViewModel)
    }
}

@Composable
fun DisplayCourseName(scoreCardViewModel: ScoreCardViewModel) {
    Row {
        Text(
            text = scoreCardViewModel.state.mCourseName,
            fontSize = SCORE_CARD_COURSE_NAME_TEXT.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(4.dp),
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = "Display: Gross Score",
            fontSize = SCORE_CARD_COURSE_NAME_TEXT.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(4.dp),
        )
    }
}

@Composable
fun DisplayScoreCardHeader(
    scoreCardViewModel: ScoreCardViewModel
) {
    val hdcpParHoleHeading = scoreCardViewModel.state.hdcpParHoleHeading
    var modifier = Modifier.width(100.dp)

    Column {
        Row(
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Column {
                for (rowHeading in hdcpParHoleHeading) {
                    DisplayRowHeading(
                        rowHeading = rowHeading.mName,
                        modifier,
                        Color(VIN_LIGHT_GRAY)
                    )
                }
            }
            Column {
                for (idx in hdcpParHoleHeading.indices) { // here's how the current hole played is high lighted
                    val color: Long =
                        // Vin hole played is a flag to high light the hole being played
                        if (hdcpParHoleHeading[idx].vinTag == HOLE_HEADER) VIN_HOLE_PLAYED else VIN_LIGHT_GRAY

                    DisplayScoreCardCell(
                        Modifier,
                        hdcpParHoleHeading[idx].mHole,
                        Color(color),
                        scoreCardViewModel,
                    )
                }
            }
            Column {
                modifier = Modifier.width(COLUMN_TOTAL_WIDTH.dp)
                for (idx in hdcpParHoleHeading.indices) {
                    if (hdcpParHoleHeading[idx].vinTag == PAR_HEADER) { // the score card par row
                        hdcpParHoleHeading[idx].mTotal =
                            scoreCardViewModel.getTotalForNineCell(hdcpParHoleHeading[idx].mHole)
                    }
                    DisplayRowHeading(
                        hdcpParHoleHeading[idx].mTotal,
                        modifier,
                        Color(VIN_LIGHT_GRAY)
                    )
                }
            }
        }
    }
}

@Composable
fun DisplayScoreCardNames(
    scoreCardViewModel: ScoreCardViewModel
) {
    val playerHeading = scoreCardViewModel.state.playerHeading
    var modifier = Modifier.width(100.dp)

    Column {
        Row {
            Column {
                for (rowPlayerName in playerHeading) {      // display the names down the left side
                    DisplayRowHeading(rowHeading = rowPlayerName.mName, modifier, Color.Transparent)
                }
            }
            Column {
                val holeHdcps = scoreCardViewModel.getHoleHdcps()
                for (idx in playerHeading.indices) {
                    DisplayPlayerScoreCardCell(
                        Modifier,
                        playerHeading[idx],
                        holeHdcps,      // the course handicap for the holes
                        Color.Transparent,
                        scoreCardViewModel
                    )
                }
            }
            Column {
                modifier = Modifier.width(COLUMN_TOTAL_WIDTH.dp)
                for (idx in playerHeading.indices) {
                    playerHeading[idx].mTotal =
                        scoreCardViewModel.getTotalForNineCell(playerHeading[idx].mScore)
                    DisplayRowHeading(playerHeading[idx].mTotal, modifier, Color(VIN_LIGHT_GRAY))
                }
            }
        }
    }
}

@Composable
fun DisplayScoreCardTeams(
    scoreCardViewModel: ScoreCardViewModel
) {
    val teamUsedHeading = scoreCardViewModel.state.teamUsedHeading
    var modifier = Modifier.width(100.dp)

    Column {
        Row {

            Column {
                for (rowTeam in teamUsedHeading) {
                    DisplayRowHeading(rowHeading = rowTeam.mName, modifier, Color(VIN_LIGHT_GRAY))
                }
            }
            Column {
                for (rowTeamCell in teamUsedHeading) {
                    DisplayScoreCardCell(
                        Modifier,
                        rowTeamCell.mHole,
                        Color(VIN_LIGHT_GRAY),
                        scoreCardViewModel,
                    )
                }
            }
            Column {
                modifier = Modifier.width(COLUMN_TOTAL_WIDTH.dp)
                for (idx in teamUsedHeading.indices) {
                    teamUsedHeading[idx].mTotal =
                        scoreCardViewModel.getTotalForNineCell(teamUsedHeading[idx].mHole)
                    DisplayRowHeading(teamUsedHeading[idx].mTotal, modifier, Color(VIN_LIGHT_GRAY))
                }
            }
        }
    }
}

@Composable
fun DisplayScoreCardCell(
    modifier: Modifier,
    cellData: IntArray,
    color: Color,
    scoreCardViewModel: ScoreCardViewModel,
) {
    Row()
    {
        val startingCell: Int = scoreCardViewModel.getStartingHole()
        val endingCell: Int = scoreCardViewModel.getEndingHole()
        for (idx in startingCell until endingCell) {
            Surface(
                modifier = modifier.width(45.dp),
                border = BorderStroke(Dp.Hairline, color = Color.Blue),
                color = scoreCardViewModel.setHighLightCurrentHole(idx, color),
                contentColor = contentColorFor(Color.Transparent),

                ) {
                Log.d("VIN", "DisplayScoreCardCell ${cellData[idx]}")

                Text(
                    text = if (cellData[idx] == 0) "" else cellData[idx].toString(), // do not display '0'++ on the score card
                    fontSize = SCORE_CARD_TEXT.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(4.dp),
                    maxLines = 1,
                )  // start at zero - get the first column of the data
            }
        }
    }
}

@Composable
fun DisplayPlayerScoreCardCell(
    modifier: Modifier,
    playerHeading: PlayerHeading,
    holeHdcps: IntArray,
    color: Color,
    scoreCardViewModel: ScoreCardViewModel
) {
    Row()
    {
        val startingCell: Int = scoreCardViewModel.getStartingHole()
        val endingCell: Int = scoreCardViewModel.getEndingHole()
        for (idx in startingCell until endingCell) {
            val playerStokeHoleColor = scoreCardViewModel.getStokeOnHolePlayerColor(
                playerHeading.mHdcp,
                holeHdcps[idx]
            )
            Surface(
                modifier = modifier.width(45.dp),
                border = BorderStroke(Dp.Hairline, color = Color.Blue),
                color = playerStokeHoleColor,
                contentColor = contentColorFor(Color.Transparent),
            ) {
                Log.d(
                    "VIN",
                    "DisplayPlayerScoreCardCell playerHeading mHdc ${playerHeading.mHdcp} hdcp ${holeHdcps[idx]}"
                )

                Text(
                    text = if (playerHeading.mScore[idx] == 0) "" else playerHeading.mScore[idx].toString(), // do not display '0'++ on the score card
                    fontSize = SCORE_CARD_TEXT.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(4.dp),
                    maxLines = 1,
                )  // start at zero - get the first column of the data
            }
        }
    }
}

@Composable
fun DisplayRowHeading(
    rowHeading: String,
    modifier: Modifier,
    color: Color,
) {

    Surface(
        modifier = modifier, // modifier.width(100.dp),
        border = BorderStroke(Dp.Hairline, color = Color.Blue),
        color = color,
        contentColor = contentColorFor(Color.Transparent),
    ) {
        Text(
            text = rowHeading,
            fontSize = SCORE_CARD_TEXT.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )  // start at zero - get the first column of the data
    }
}
