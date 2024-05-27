package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.golfpvcc.teamscore_rev4.ui.navigation.ROOT_GRAPH_ROUTE
import com.golfpvcc.teamscore_rev4.ui.navigation.TeamScoreScreen
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.PlayerHeading
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.ScoreCardViewModel
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore.DialogAction
import com.golfpvcc.teamscore_rev4.utils.COLOR_NEXT_HOLE
import com.golfpvcc.teamscore_rev4.utils.COLOR_PREV_HOLE
import com.golfpvcc.teamscore_rev4.utils.COLOR_SCREEN_MODE
import com.golfpvcc.teamscore_rev4.utils.Constants.COLUMN_TOTAL_WIDTH
import com.golfpvcc.teamscore_rev4.utils.Constants.SCORE_CARD_COURSE_NAME_TEXT
import com.golfpvcc.teamscore_rev4.utils.Constants.SCORE_CARD_TEXT
import com.golfpvcc.teamscore_rev4.utils.VIN_LIGHT_GRAY
import com.golfpvcc.teamscore_rev4.utils.DISPLAY_HOLE_NUMBER

sealed class ScoreCardActions {
    data object ButtonEnterScore : ScoreCardActions()
    data object Prev : ScoreCardActions()
    data object Next : ScoreCardActions()
    data object ScreenMode : ScoreCardActions()
    data object SetDialogCurrentPlayer : ScoreCardActions()
    data object FlipFrontBackNine : ScoreCardActions()
}
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
            text = "Display: ${scoreCardViewModel.state.mDisplayScreenModeText}",
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
            Column {    // display the handicap row
                for (rowHeading in hdcpParHoleHeading) {
                    DisplayRowHeading(
                        rowHeading = rowHeading.mName,  // display hdcp, par, and hole in first column
                        modifier,
                        Color(VIN_LIGHT_GRAY)
                    )
                }
            }
            Column {    // the DISPLAY_HOLE_NUMBER is flag to high lite the current hole being scored
                for (idx in hdcpParHoleHeading.indices) { // here's how the current hole played is high lighted
                    val holeNumberColor: Long =
                        // Vin hole played is a flag to high light the hole being played
                        if (hdcpParHoleHeading[idx].vinTag == HOLE_HEADER) DISPLAY_HOLE_NUMBER else VIN_LIGHT_GRAY

                    DisplayScoreCardCell(   // the cell function will display the 9 cell data - ie hdcp, par, and hole number
                        Modifier,
                        hdcpParHoleHeading[idx].mHole,
                        Color(holeNumberColor),
                        scoreCardViewModel,
                    )
                }
            }
            Column {    // display the total column notes par total and "Total"
                modifier = Modifier.width(COLUMN_TOTAL_WIDTH.dp)
                for (idx in hdcpParHoleHeading.indices) {       // indices = 3
                    if (hdcpParHoleHeading[idx].vinTag == PAR_HEADER) { // the score card par row - show the total for par
                        hdcpParHoleHeading[idx].mTotal =
                            scoreCardViewModel.getTotalForNineCell(hdcpParHoleHeading[idx].mHole)
                    }

                    DisplayRowHeadingOnClick(
                        hdcpParHoleHeading[idx].mTotal,
                        modifier,
                        hdcpParHoleHeading[idx].mColor,
                        scoreCardViewModel::dialogAction
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
                val holeHdcpCells = scoreCardViewModel.getHoleHdcpCells()
                for (idx in playerHeading.indices) {        // here's the score card player's loop of scores
                    DisplayPlayerScoreCardCell(
                        Modifier,
                        playerHeading[idx],
                        holeHdcpCells,      // the course handicap for the holes
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
                    Log.d("VIN", "Calculate Team and Used score totals Idx $idx - ${teamUsedHeading[idx].mTotal}")
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
                    text = cellData[idx].toString(), //
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
                    playerHeading.mStokeHole[idx]
                )

            val playerScoreColor =
                scoreCardViewModel.getPlayerScoreColorForHole(   //check for a birdie and turn the score red.
                    playerHeading.mDisplayScore[idx],
                    parForTheHoles[idx]
                )
            val teamScoreColor =
                scoreCardViewModel.getTeamColorForHole(playerHeading.mTeamHole[idx]) // did we use this score for the team game
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
fun DisplayRowHeadingOnClick(
    rowHeading: String,
    modifier: Modifier,
    color: Color,
    onAction: (DialogAction) -> Unit,
) {

    Surface(
        modifier = modifier
            .clickable {onAction(DialogAction.DisplayHoleNote)}, // modifier.width(100.dp),
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
fun FlipFrontAndBackNine(FlipText: Boolean, onAction: (ScoreCardActions) -> Unit) {
    val displayFlipFrontBackText: String = if (FlipText) "Back Nine" else "Front Nine"

    RightSidePanelButton(
        displayFlipFrontBackText,
        Color(COLOR_PREV_HOLE),
        onClick = { onAction(ScoreCardActions.FlipFrontBackNine) })
}

@Composable
fun DisplayPrevNextHoleButton(onAction: (ScoreCardActions) -> Unit) {

    RightSidePanelButton(
        "Prev",
        Color(COLOR_PREV_HOLE),
        onClick = { onAction(ScoreCardActions.Prev) })
    Spacer(modifier = Modifier.size(15.dp))
    RightSidePanelButton(
        "Next",
        Color(COLOR_NEXT_HOLE),
        onClick = { onAction(ScoreCardActions.Next) })
}

@Composable
fun DisplayScreenModeButton(displayScreenMode: String, onAction: (ScoreCardActions) -> Unit) {
    RightSidePanelButton(
        displayScreenMode,
        Color(COLOR_SCREEN_MODE),
        onClick = { onAction(ScoreCardActions.ScreenMode) })
}

@Composable
fun DisplaySummaryButton(navController: NavController,) {
    RightSidePanelButton(
        "Summary",
        Color(COLOR_SCREEN_MODE),
        onClick = {
            navController.navigate(route = TeamScoreScreen.ScreenSummary.route){
            popUpTo(ROOT_GRAPH_ROUTE)
            }
        }
    )
}

@Composable
fun RightSidePanelButton(buttonText: String, butColor: Color, onClick: () -> Unit) {
    Button(
        onClick = {
            onClick()
        },
        colors = ButtonDefaults.buttonColors(containerColor = butColor),
        contentPadding = PaddingValues(10.dp),
        //shape = RectangleShape,
        modifier = Modifier
            .height(40.dp)
            .width(110.dp),  //vpg 100 did not work
    ) {
        Text(text = buttonText)
    }
}
