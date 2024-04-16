package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.PlayerHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.ScoreCardViewModel
import com.golfpvcc.teamscore_rev4.utils.Constants.COLUMN_TOTAL_WIDTH
import com.golfpvcc.teamscore_rev4.utils.Constants.SCORE_CARD_COURSE_NAME_TEXT
import com.golfpvcc.teamscore_rev4.utils.Constants.SCORE_CARD_TEXT
import com.golfpvcc.teamscore_rev4.utils.VIN_LIGHT_GRAY
import com.golfpvcc.teamscore_rev4.utils.DISPLAY_HOLE_NUMBER

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
            Column {    // the DISPLAY_HOLE_NUMBER is flag to high lite the current hole being scored
                for (idx in hdcpParHoleHeading.indices) { // here's how the current hole played is high lighted
                    val holeNumberColor: Long = // Vin hole played is a flag to high light the hole being played
                        if (hdcpParHoleHeading[idx].vinTag == HOLE_HEADER) DISPLAY_HOLE_NUMBER else VIN_LIGHT_GRAY

                    DisplayScoreCardCell(
                        Modifier,
                        hdcpParHoleHeading[idx].mHole,
                        Color(holeNumberColor),
                        scoreCardViewModel,
                    )
                }
            }
            Column {
                modifier = Modifier.width(COLUMN_TOTAL_WIDTH.dp)
                for (idx in hdcpParHoleHeading.indices) {
                    if (hdcpParHoleHeading[idx].vinTag == PAR_HEADER) { // the score card par row - show the total for par
                        hdcpParHoleHeading[idx].mTotal =
                            scoreCardViewModel.getTotalForNineCell(hdcpParHoleHeading[idx].mHole)
                    }
                    DisplayRowHeading(
                        hdcpParHoleHeading[idx].mTotal,
                        modifier,
                        hdcpParHoleHeading[idx].mColor
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
                for (idx in playerHeading.indices) {        // here's the score card player's loop of scores
                    DisplayPlayerScoreCardCell(
                        Modifier,
                        playerHeading[idx],
                        holeHdcps,      // the course handicap for the holes
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
                for (rowTeam in teamUsedHeading) { // display "Team" and "Used"
                    DisplayRowHeading(rowHeading = rowTeam.mName, modifier, Color(VIN_LIGHT_GRAY))
                }
            }
            Column {    // display "Team" and "Used" score in cells
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
                    teamUsedHeading[idx].mTotal =       // Calculate "Team" and "Used" score totals
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
    scoreCardViewModel: ScoreCardViewModel
) {
    Row()
    {
        val startingCell: Int = scoreCardViewModel.getStartingHole()
        val endingCell: Int = scoreCardViewModel.getEndingHole()
        val hdcpParHoleHeading = scoreCardViewModel.state.hdcpParHoleHeading
        var parForTheHoles: IntArray = IntArray(18) { 4 }

        for (idx in hdcpParHoleHeading.indices) {
            if (hdcpParHoleHeading[idx].vinTag == PAR_HEADER) { // the score card par row
                parForTheHoles = hdcpParHoleHeading[idx].mHole
            }
        }

        for (idx in startingCell until endingCell) {    // player score card loop
            val playerStokeHoleColor =
                scoreCardViewModel.getStokeOnHolePlayerColor( //determine the stokes a player gets on hole by the color
                    playerHeading.mHdcp, idx)

            val playerScoreColor =
                scoreCardViewModel.getPlayerScoreColorForHole(   //check for a birdie and turn the score red.
                    playerHeading.mScore[idx],
                    parForTheHoles[idx]
                )
            val teamScoreColor =
                scoreCardViewModel.getTeamColorForHole(playerHeading.mScore[idx]) // did we use this score for the team game
            Surface(
                modifier = modifier.width(45.dp),
                border = BorderStroke(Dp.Hairline, color = Color.Blue),
                color = playerStokeHoleColor,
                contentColor = contentColorFor(Color.Transparent),
            ) {
                val playerStringScore = scoreCardViewModel.getPlayerHoleScore(
                    playerHeading.vinTag,
                    idx
                ) // do not display '0'++ on the score card
                Text(
                    text = playerStringScore,
                    fontSize = SCORE_CARD_TEXT.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(4.dp)
                        .border(3.dp, teamScoreColor, shape = RoundedCornerShape(0.dp)),
                    color = playerScoreColor,
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

@Composable
fun DisplayPrevNextHoleButton(onAction: (ScoreCardActions) -> Unit) {

    PrevNextHoleButton("Prev", onClick = { onAction(ScoreCardActions.Prev) })
    Spacer(modifier = Modifier.size(15.dp))
    PrevNextHoleButton("Next", onClick = { onAction(ScoreCardActions.Next) })
}

@Composable
fun PrevNextHoleButton(buttonText: String, onClick: () -> Unit) {
    Button(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .height(40.dp),  //vpg
    ) {
        Text(text = buttonText)
    }
}

sealed class ScoreCardActions {
    data object Prev : ScoreCardActions()
    data object Next : ScoreCardActions()
}