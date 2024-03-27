package com.golfpvcc.teamscore_rev4.ui.screens.scorecard

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golfpvcc.teamscore_rev4.utils.Constants.COLUMN_TOTAL_WIDTH
import com.golfpvcc.teamscore_rev4.utils.Constants.SCORE_CARD_TEXT
import com.golfpvcc.teamscore_rev4.utils.Constants.VIN_LIGHT_GRAY

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
                    DisplayRowHeading(rowHeading = rowHeading.mName, modifier, Color(VIN_LIGHT_GRAY))
                }
            }
            Column {
                for (rowHeaderCell in rowHeaderCells) {
                    DisplayScoreCardCell(Modifier, rowHeaderCell.mHole, Color(VIN_LIGHT_GRAY))
                }
            }
            Column {
                modifier = Modifier.width(COLUMN_TOTAL_WIDTH.dp)
                for (rowHeaderTotal in rowHeaderTotals) {
                    DisplayRowHeading(rowHeaderTotal.mName, modifier, Color(VIN_LIGHT_GRAY))
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
                    DisplayScoreCardCell(Modifier, rowPlayerCell.mHole, Color.Transparent)
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
                    DisplayScoreCardCell(Modifier, rowTeamCell.mHole, Color(VIN_LIGHT_GRAY))
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
) {
    Row()
    {
        val displayData = cellData.drop(9)
        for (cell in displayData) {
            Surface(
                modifier = modifier.width(45.dp),
                border = BorderStroke(Dp.Hairline, color = Color.Blue),
                color = color,
                contentColor = contentColorFor(Color.Transparent),
            ) {
                Log.d("VIN", "DisplayScoreCardCell $cell")
                Text(
                    text = cell.toString(),
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
