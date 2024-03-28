package com.golfpvcc.teamscore_rev4.ui.screens.scorecard

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.golfpvcc.teamscore_rev4.utils.Constants
import com.golfpvcc.teamscore_rev4.utils.Constants.COLUMN_TOTAL_WIDTH
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
        DisplayScoreCardHeader(scoreCardViewModel)
        DisplayScoreCardNames(scoreCardViewModel)
        DisplayScoreCardTeams(scoreCardViewModel)
    }

}

@Composable
fun DisplayScoreCardHeader(
    scoreCardViewModel: ScoreCardViewModel
) {
    val rowHeadings = scoreCardViewModel.state.rowHeadings
    val rowHeaderCells = scoreCardViewModel.state.rowHeaderCells
    val rowHeaderTotals = scoreCardViewModel.state.rowHeaderTotals
    var modifier = Modifier.width(100.dp)

    Column {
        Row(
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Column {
                for (rowHeading in rowHeadings) {
                    DisplayRowHeading(
                        rowHeading = rowHeading.mName,
                        modifier,
                        Color(VIN_LIGHT_GRAY)
                    )
                }
            }
            Column {
                for (idx in rowHeaderCells.indices) { // here's how the current hole played is high lighted
                    val color: Long =
                        if (rowHeaderCells[idx].vinTag == HOLE_CELL)  VIN_HOLE_PLAYED else VIN_LIGHT_GRAY

                    DisplayScoreCardCell(
                        Modifier,
                        rowHeaderCells[idx].mHole,
                        Color(color),
                        scoreCardViewModel
                    )
                }
            }
            Column {
                modifier = Modifier.width(COLUMN_TOTAL_WIDTH.dp)
                for (idx in rowHeaderTotals.indices ) {
                    if (rowHeaderTotals[idx].vinTag == PAR_TOTAL) {
                        rowHeaderTotals[idx].mName = scoreCardViewModel.getTotalForNineCell(rowHeaderCells[idx].mHole)
                    }
                    DisplayRowHeading(rowHeaderTotals[idx].mName, modifier, Color(VIN_LIGHT_GRAY))
                }
            }
        }
    }
}

@Composable
fun DisplayScoreCardNames(
    scoreCardViewModel: ScoreCardViewModel
) {
    val rowPlayerNames = scoreCardViewModel.state.rowPlayerNames
    val rowPlayerCells = scoreCardViewModel.state.rowPlayerCells
    val rowPlayerTotals = scoreCardViewModel.state.rowPlayerTotals
    var modifier = Modifier.width(100.dp)

    Column {
        Row() {
            Column {
                for (rowPlayerName in rowPlayerNames) {
                    DisplayRowHeading(rowHeading = rowPlayerName.mName, modifier, Color.Transparent)
                }
            }
            Column {
                for (rowPlayerCell in rowPlayerCells) {
                    DisplayScoreCardCell(
                        Modifier,
                        rowPlayerCell.mHole,
                        Color.Transparent,
                        scoreCardViewModel
                    )
                }
            }
            Column {
                modifier = Modifier.width(COLUMN_TOTAL_WIDTH.dp)
                for (rowPlayerTotal in rowPlayerTotals) {
                    DisplayRowHeading(rowPlayerTotal.mName, modifier, Color.Transparent)
                }
            }
        }
    }
}

@Composable
fun DisplayScoreCardTeams(
    scoreCardViewModel: ScoreCardViewModel
) {
    val rowTeams = scoreCardViewModel.state.rowTeams
    val rowTeamCells = scoreCardViewModel.state.rowTeamCells
    val rowTeamTotals = scoreCardViewModel.state.rowTeamTotals
    var modifier = Modifier.width(100.dp)

    Column {
        Row() {

            Column {
                for (rowTeam in rowTeams) {
                    DisplayRowHeading(rowHeading = rowTeam.mName, modifier, Color(VIN_LIGHT_GRAY))
                }
            }
            Column {
                for (rowTeamCell in rowTeamCells) {
                    DisplayScoreCardCell(
                        Modifier,
                        rowTeamCell.mHole,
                        Color(VIN_LIGHT_GRAY),
                        scoreCardViewModel
                    )
                }
            }
            Column {
                modifier = Modifier.width(COLUMN_TOTAL_WIDTH.dp)
                for (rowTeamTotal in rowTeamTotals) {
                    DisplayRowHeading(rowTeamTotal.mName, modifier, Color(VIN_LIGHT_GRAY))
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
    scoreCardViewModel: ScoreCardViewModel
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
                Log.d("VIN", "DisplayScoreCardCell $cellData[idx]")
                Text(
                    text = cellData[idx].toString(),
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
