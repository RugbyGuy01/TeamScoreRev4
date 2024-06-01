package com.golfpvcc.teamscore_rev4.ui.screens.scorecard

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore.ButtonEnterScore
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.displayoptions.DisplayModeDropDown
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.DisplayCourseName
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.DisplaySummaryButton
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.DisplayPrevNextHoleButton
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.DisplayScoreCardHeader
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.DisplayScoreCardNames
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.DisplayScoreCardTeams
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.FlipFrontAndBackNine
import com.golfpvcc.teamscore_rev4.utils.SetScreenOrientation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScoreCardScreen(
    navController: NavHostController,
    id: Int?,
) {
    SetScreenOrientation(SCREEN_ORIENTATION_LANDSCAPE)

    val scoreCardViewModel = viewModel<ScoreCardViewModel>(
        factory = ScoreCardViewModel.ScoreCardViewModelFactor()
    )
    GetScoreCardRecord(scoreCardViewModel)

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {
        Scaffold()
        {
            Spacer(modifier = Modifier.padding(5.dp))
            Row() {
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                       // .weight(.8f)   // size of score card with
                        .fillMaxHeight()
                ) {
                    DisplayMainScoreCard(scoreCardViewModel)
                    Spacer(modifier = Modifier.size(12.dp))
                }
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxHeight(),
                       // .weight(.2f),  // size of score card with
                    horizontalAlignment = Alignment.End
                ) {
                    DisplayControlButtons(
                        scoreCardViewModel,
                        navController
                    )       // buttons on the side of the score card
                }
            }
        } // end of Scaffold
    }
}

@Composable
fun GetScoreCardRecord(
    scoreCardViewModel: ScoreCardViewModel
) {
    val scope = rememberCoroutineScope()
    Log.d("VIN1", "GetScoreCardRecord screen calling function")

    LaunchedEffect(true) {
        Log.d("VIN1", "LaunchedEffect screen calling function")
        scope.launch(Dispatchers.IO) {
            Log.d("VIN1", "GetScoreCardRecord called scope.launch")
            scoreCardViewModel.getScoreCardAndPlayerRecord()
        }
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
fun DisplayControlButtons(scoreCardViewModel: ScoreCardViewModel, navController: NavController) {

    FlipFrontAndBackNine(
        scoreCardViewModel.state.mWhatNineIsBeingDisplayed,
        scoreCardViewModel::scoreCardActions
    )
    Spacer(modifier = Modifier.size(12.dp))
    ButtonEnterScore(
        scoreCardViewModel,
        scoreCardViewModel::dialogAction,
        scoreCardViewModel::scoreCardActions
    )
    Spacer(modifier = Modifier.size(20.dp))
    DisplayPrevNextHoleButton(scoreCardViewModel::scoreCardActions)
    Spacer(modifier = Modifier.size(25.dp))
    DisplaySummaryButton(navController)
    Spacer(modifier = Modifier.size(20.dp))
    DisplayModeDropDown( scoreCardViewModel::scoreCardActions, scoreCardViewModel.state.mGameNines)
//    DisplayScreenModeButton(
//        scoreCardViewModel.state.mButtonScreenNextText, // what will be display gross, net point quote, stableford screens
//        scoreCardViewModel::scoreCardActions
//    )

}