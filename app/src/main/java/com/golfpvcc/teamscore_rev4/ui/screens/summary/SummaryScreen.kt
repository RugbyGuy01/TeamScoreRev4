package com.golfpvcc.teamscore_rev4.ui.screens.summary

import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.golfpvcc.teamscore_rev4.ui.navigation.ROOT_GRAPH_ROUTE
import com.golfpvcc.teamscore_rev4.ui.navigation.TeamScoreScreen
import com.golfpvcc.teamscore_rev4.ui.screens.CardButton
import com.golfpvcc.teamscore_rev4.ui.screens.summary.utils.AboutDialog
import com.golfpvcc.teamscore_rev4.ui.screens.summary.utils.ConfigureEmailDialog
import com.golfpvcc.teamscore_rev4.ui.screens.summary.utils.ConfigureJunkDialog
import com.golfpvcc.teamscore_rev4.ui.screens.summary.utils.ConfigurePointsDialog
import com.golfpvcc.teamscore_rev4.utils.MENU_BUTTON_TEXT
import com.golfpvcc.teamscore_rev4.utils.MENU_ROW_LIGHT_GRAY
import com.golfpvcc.teamscore_rev4.utils.SUMMARY_BUTTON_TEXT
import com.golfpvcc.teamscore_rev4.utils.SUMMARY_NAME_TEXT_SIZE
import com.golfpvcc.teamscore_rev4.utils.SUMMARY_PAYOUT_COLOR
import com.golfpvcc.teamscore_rev4.utils.SUMMARY_TEXT_SIZE
import com.golfpvcc.teamscore_rev4.utils.SetScreenOrientation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun SummaryScreen(
    navController: NavHostController,
    id: Int?,
) {
    SetScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    val summaryViewModel = viewModel<SummaryViewModel>(
        factory = SummaryViewModel.SummaryViewModelFactor()
    )

    GetScoreCardRecord(summaryViewModel)

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {
        Scaffold()
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(it)
                ) {
                    DisplayTeamTotalScore(summaryViewModel)
                }
                Row() {
                    BottomButtons(navController, summaryViewModel.state.mCourseId, summaryViewModel)
                    DisplayMenuOptionDialogs(summaryViewModel)
                }
                Row() {
                    DisplayPlayersTotalScore(
                        summaryViewModel.state,
                        summaryViewModel::summaryActions
                    )
                }
            }
        }
    } // end of Scaffold
}

@Composable
fun DisplayMenuOptionDialogs(summaryViewModel: SummaryViewModel) {
    if (summaryViewModel.state.mShowAboutDialog) {
        AboutDialog(summaryViewModel::summaryActions)
    }
    if (summaryViewModel.state.mShowPointsDialog) {
        SetScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        ConfigurePointsDialog(summaryViewModel::summaryActions, summaryViewModel)
    } else
        SetScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    if (summaryViewModel.state.mShowEmailDialog) {
        ConfigureEmailDialog(summaryViewModel::summaryActions, summaryViewModel)
    }
    if (summaryViewModel.state.mShowJunkDialog) {
        SetScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        ConfigureJunkDialog(summaryViewModel::summaryActions, summaryViewModel)
    } else
        SetScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

}


@Composable
fun DisplayTeamTotalScore(summaryViewModel: SummaryViewModel) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Column(
            Modifier
                .padding(3.dp),
        ) {
            TeamScoreHeader()
            DisplayTeamPointQuoteSummary(summaryViewModel)
            DisplayTeamPointsSummary(summaryViewModel)
            DisplayScoreOverUnderSummary(summaryViewModel)
            DisplayStablefordSummary(summaryViewModel)
            DisplayABCDGameSummary(summaryViewModel)
        }
    }
}

@Composable
fun DisplayABCDGameSummary(summaryViewModel: SummaryViewModel) {
    val player = listOf("A", "B", "C", "D")
    Row(
        Modifier
            .background(Color.White)
            .fillMaxWidth(),
    ) {
        Text(text = "Game ABCD", Modifier.weight(2 / 4f), fontSize = SUMMARY_TEXT_SIZE.sp)
        for ((idx, element) in summaryViewModel.state.mPlayerSummary.withIndex()) {
            Text(
                text = "${player[idx]} - ${summaryViewModel.getABCDGameScore(idx)}",
                Modifier.weight(.5f),
                fontSize = SUMMARY_TEXT_SIZE.sp
            )
        }
    }
}


@Composable
fun TeamScoreHeader(modifier: Modifier = Modifier) {
    Row(
        Modifier
            .background(Color.LightGray)
            .fillMaxWidth(),
    ) {
        Text(text = "Team Summary", Modifier.weight(2 / 4f), fontSize = SUMMARY_TEXT_SIZE.sp)
        Text(text = "Front", Modifier.weight(.5f), fontSize = SUMMARY_TEXT_SIZE.sp)
        Text(text = "Back", Modifier.weight(.5f), fontSize = SUMMARY_TEXT_SIZE.sp)
        Text(text = "Total", Modifier.weight(.5f), fontSize = SUMMARY_TEXT_SIZE.sp)
    }
}

@Composable
fun DisplayTeamPointQuoteSummary(summaryViewModel: SummaryViewModel) {
    Row(
        Modifier
            .background(Color.White)
            .fillMaxWidth(),
    ) {
        Text(text = "Pt. Quote (used)", Modifier.weight(2 / 4f), fontSize = SUMMARY_TEXT_SIZE.sp)
        Text(
            text = summaryViewModel.frontPtQuota(),
            Modifier.weight(.5f),
            fontSize = SUMMARY_TEXT_SIZE.sp
        )
        Text(
            text = summaryViewModel.backPtQuota(),
            Modifier.weight(.5f),
            fontSize = SUMMARY_TEXT_SIZE.sp
        )
        Text(
            text = summaryViewModel.totalPtQuota(),
            Modifier.weight(.5f),
            fontSize = SUMMARY_TEXT_SIZE.sp
        )
    }
}

@Composable
fun DisplayTeamPointsSummary(summaryViewModel: SummaryViewModel) {
    Row(
        Modifier
            .background(Color.White)
            .fillMaxWidth(),
    ) {
        Text(text = "Points  (Quota)", Modifier.weight(2 / 4f), fontSize = SUMMARY_TEXT_SIZE.sp)
        Text(
            text = summaryViewModel.frontPointsQuota(),
            Modifier.weight(.5f),
            fontSize = SUMMARY_TEXT_SIZE.sp
        )
        Text(
            text = summaryViewModel.backPointsQuota(),
            Modifier.weight(.5f),
            fontSize = SUMMARY_TEXT_SIZE.sp
        )
        Text(
            text = summaryViewModel.totalPointsQuota(),
            Modifier.weight(.5f),
            fontSize = SUMMARY_TEXT_SIZE.sp
        )
    }
}

@Composable
fun DisplayScoreOverUnderSummary(summaryViewModel: SummaryViewModel) {
    Row(
        Modifier
            .background(Color.White)
            .fillMaxWidth(),
    ) {
        Text(text = "Score (O/U)", Modifier.weight(2 / 4f), fontSize = SUMMARY_TEXT_SIZE.sp)
        Text(
            text = summaryViewModel.frontScoreOverUnder(),
            Modifier.weight(.5f),
            fontSize = SUMMARY_TEXT_SIZE.sp
        )
        Text(
            text = summaryViewModel.backScoreOverUnder(),
            Modifier.weight(.5f),
            fontSize = SUMMARY_TEXT_SIZE.sp
        )
        Text(
            text = summaryViewModel.totalScoreOverUnder(),
            Modifier.weight(.5f),
            fontSize = SUMMARY_TEXT_SIZE.sp
        )
    }
}

@Composable
fun DisplayStablefordSummary(summaryViewModel: SummaryViewModel) {
    Row(
        Modifier
            .background(Color.White)
            .fillMaxWidth(),
    ) {
        Text(text = "Stableford (Used)", Modifier.weight(2 / 4f), fontSize = SUMMARY_TEXT_SIZE.sp)
        Text(
            text = summaryViewModel.frontStablefordUsed(),
            Modifier.weight(.5f),
            fontSize = SUMMARY_TEXT_SIZE.sp
        )
        Text(
            text = summaryViewModel.backStablefordUsed(),
            Modifier.weight(.5f),
            fontSize = SUMMARY_TEXT_SIZE.sp
        )
        Text(
            text = summaryViewModel.totalStablefordUsed(),
            Modifier.weight(.5f),
            fontSize = SUMMARY_TEXT_SIZE.sp
        )
    }
}

@Composable
fun DisplayPlayersTotalScore(state: State, onAction: (SummaryActions) -> Unit) {
    val playersRecord = state.mPlayerSummary
    Column(
        Modifier
            .height(200.dp)
            .padding(1.dp),
    ) {
        LazyRow(Modifier.padding(4.dp)) {
            items(playersRecord) { player ->
                DisplayPlayerScore(player, onAction)
                Spacer(modifier = Modifier.size(10.dp))
            }
        }
    }
}

@Composable
fun DisplayPlayerScore(player: PlayerSummary, onAction: (SummaryActions) -> Unit) {
    Card(
        Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, Color.Black),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            Modifier
                .padding(3.dp)
                .width(550.dp)
        ) {
            DisplayPlayerNameAndScore(player, onAction)
            HorizontalDivider(thickness = 2.dp)
            DisplayPlayerScoreLine1(player)
            DisplayPlayerScoreLine2(player)
            DisplayPlayerScoreLine3(player)
            HorizontalDivider(thickness = 2.dp, color = Color.Red)
            DisplayPlayerScorePayouts(player)
        }
    }
}

@Composable
fun DisplayPlayerScorePayouts(player: PlayerSummary) {

    var playerJunkPayoutList: List<PlayerJunkPayoutRecord> = emptyList()
    val start = 0
    var end = 4
    var newStart = 0
    var totalRecords = player.mJunkPayoutList.count()
    if (totalRecords > 4) {
        end = 4
        newStart = 5
    } else {
        end = totalRecords
    }
    playerJunkPayoutList = player.mJunkPayoutList.subList(start, end)
    DisplayPayoutRow(playerJunkPayoutList)
    if (newStart != 0) {
        playerJunkPayoutList = player.mJunkPayoutList.subList(newStart, totalRecords)
        DisplayPayoutRow(playerJunkPayoutList)
    }

}

fun Row(`modifier`: Modifier, horizontalArrangement: Arrangement.HorizontalOrVertical) {

}

@Composable
fun DisplayPayoutRow(playerJunkPayoutList: List<PlayerJunkPayoutRecord>) {

    Row(
        Modifier
            .background(Color(SUMMARY_PAYOUT_COLOR))
            .padding(4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Payouts: ",
           // Modifier.weight(1.1f),
            fontSize = SUMMARY_TEXT_SIZE.sp
        )
        playerJunkPayoutList.forEachIndexed { idx, payout ->
            Text(
                text = "${payout.mJunkName} : ${payout.mCount}",
               // Modifier.weight(1.1f),
                fontSize = SUMMARY_TEXT_SIZE.sp
            )
        }
    }
}

@Composable
fun DisplayPlayerNameAndScore(player: PlayerSummary, onAction: (SummaryActions) -> Unit) {
    val mContext = LocalContext.current

    Row(
        Modifier
            .background(Color.LightGray)
            .fillMaxWidth(),
    ) {
        Icon(
            imageVector = Icons.Default.Email,
            contentDescription = "Email",
            modifier = Modifier.clickable {
                onAction(
                    SummaryActions.SendEmailToUser(
                        player.mPlayer.vinTag,
                        mContext
                    )
                )
            }
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = player.mPlayer.mHdcp + "-" + player.mPlayer.mName,
            Modifier.weight(.5f),
            fontSize = SUMMARY_NAME_TEXT_SIZE.sp,
            color = Color.Blue
        )
        Text(
            text = "Front: ${player.mFront}",
            Modifier.weight(.5f),
            fontSize = SUMMARY_NAME_TEXT_SIZE.sp
        )
        Text(
            text = "Back: ${player.mBack}",
            Modifier.weight(.5f),
            fontSize = SUMMARY_NAME_TEXT_SIZE.sp
        )
        Text(
            text = "Total: ${player.mBack + player.mFront}",
            Modifier.weight(.5f),
            fontSize = SUMMARY_NAME_TEXT_SIZE.sp
        )
    }
}

@Composable
fun DisplayPlayerScoreLine1(player: PlayerSummary) {
    Row(
        Modifier
            .background(Color.White)
            .fillMaxWidth(),
    ) {
        Text(
            text = "Eagles: ${player.mEagles}",
            Modifier.weight(.5f), fontSize = SUMMARY_TEXT_SIZE.sp
        )
        Text(
            text = "Birdies: ${player.mBirdies}",
            Modifier.weight(.5f),
            fontSize = SUMMARY_TEXT_SIZE.sp
        )
        Text(text = "Pars: ${player.mPars}", Modifier.weight(.5f), fontSize = SUMMARY_TEXT_SIZE.sp)
    }
}

@Composable
fun DisplayPlayerScoreLine2(player: PlayerSummary) {
    Row(
        Modifier
            .background(Color.Yellow)
            .fillMaxWidth(),
    ) {
        Text(
            text = "Bogeys: ${player.mBogeys}",
            Modifier.weight(.5f), fontSize = SUMMARY_TEXT_SIZE.sp
        )
        Text(
            text = "Double: ${player.mDouble}",
            Modifier.weight(.5f),
            fontSize = SUMMARY_TEXT_SIZE.sp
        )
        Text(
            text = "Other: ${player.mOthers}",
            Modifier.weight(.5f),
            fontSize = SUMMARY_TEXT_SIZE.sp
        )
    }
}

@Composable
fun DisplayPlayerScoreLine3(player: PlayerSummary) {
    Row(
        Modifier
            .background(Color.White)
            .fillMaxWidth(),
    ) {
        Text(
            text = "Stableford: ${player.mStableford}",
            Modifier.weight(.5f),
            fontSize = SUMMARY_TEXT_SIZE.sp
        )
        Text(
            text = "Quote: ${player.mQuote}",
            Modifier.weight(.5f), fontSize = SUMMARY_TEXT_SIZE.sp
        )
        Text(
            text = "Nine Pts: ${player.mNineTotal}",
            Modifier.weight(.5f),
            fontSize = SUMMARY_TEXT_SIZE.sp
        )
    }

}

@Composable
fun GetScoreCardRecord(
    summaryViewModel: SummaryViewModel,
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        scope.launch(Dispatchers.IO) {
            summaryViewModel.getScoreCardAndPlayerRecord()
            summaryViewModel.checkPointRecords()
        }
    }
}

@Composable
fun BottomButtons(
    navController: NavHostController,
    courseId: Int,
    summaryViewModel: SummaryViewModel,
) {
    Log.d("VIN", "Summary buttons Id $courseId")
    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .background(Color(MENU_ROW_LIGHT_GRAY)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CardButton(" Resume ", Color.White) {
            navController.navigate(route = TeamScoreScreen.ScreenScoreCard.route) { // back to the start of configuration
                popUpTo(ROOT_GRAPH_ROUTE)    // clear the back stack
            }
        }
        Log.d("VIN", "Summary buttons Id $courseId")
        CardButton(" Players ", Color.White) {
            navController.navigate(TeamScoreScreen.ScreenPlayerSetup.passId(courseId))
            { // back to the start of configuration
                popUpTo(ROOT_GRAPH_ROUTE)    // clear the back stack
            }
        }
        CardButton(" Courses ", Color.White) {
            navController.navigate(TeamScoreScreen.ScreenCourses.route) {
                popUpTo(ROOT_GRAPH_ROUTE)    // clear the back stack
            }
        }
        DisplayOptionMenuDown(summaryViewModel::summaryActions)

        CardButton(" Exit ", Color.White) {
            navController.navigate("exit")
        }
    } // end of row
}

@Composable
fun DisplayOptionMenuDown(onAction: (SummaryActions) -> Unit) {

    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(modifier = Modifier
        .wrapContentSize()
        .clip(RoundedCornerShape(4.dp))
        .padding(1.dp),
        border = BorderStroke(2.dp, Color.LightGray),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        onClick = { expanded = true }
    ) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = " Menu ",
            fontSize = SUMMARY_BUTTON_TEXT.sp,
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                { Text(text = "Junk", fontSize = MENU_BUTTON_TEXT.sp) },
                onClick = {
                    expanded = false
                    onAction(SummaryActions.DisplayJunkDialog)
                }
            )
            DropdownMenuItem(
                { Text(text = "Points", fontSize = MENU_BUTTON_TEXT.sp) },
                onClick = {
                    expanded = false
                    onAction(SummaryActions.DisplayPointsDialog)
                })
            DropdownMenuItem(
                { Text(text = "Email", fontSize = MENU_BUTTON_TEXT.sp) },
                onClick = {
                    expanded = false
                    onAction(SummaryActions.ShowEmailDialog)
                })
            DropdownMenuItem(
                { Text(text = "Backup/Restore", fontSize = MENU_BUTTON_TEXT.sp) },
                onClick = {
                    expanded = false
                    onAction(SummaryActions.DisplayBackupRestoreDialog(context))
                })
            DropdownMenuItem(
                { Text(text = "About", fontSize = MENU_BUTTON_TEXT.sp) },
                onClick = {
                    expanded = false
                    onAction(SummaryActions.DisplayAboutDialog)
                })
        }
    }
}