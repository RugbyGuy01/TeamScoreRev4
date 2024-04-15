package com.golfpvcc.teamscore_rev4.ui.screens.scorecard

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.navigation.NavHostController
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore.ButtonEnterScore
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.DisplayCourseName
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.DisplayPrevNextHoleButton
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.DisplayScoreCardHeader
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.DisplayScoreCardNames
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.DisplayScoreCardTeams
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.FlipNineDisplay
import com.golfpvcc.teamscore_rev4.utils.SetScreenOrientation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            Spacer(modifier = Modifier.padding(it))
            Row() {
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .weight(.85f)
                        .fillMaxHeight()
                ) {
                    DisplayMainScoreCard(scoreCardViewModel)
                    Spacer(modifier = Modifier.size(12.dp))
                }
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.End
                ) {
                    DisplayControlButtons(scoreCardViewModel)
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

    LaunchedEffect(true) {
        scope.launch(Dispatchers.IO) {
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
fun DisplayControlButtons(scoreCardViewModel: ScoreCardViewModel) {
    FlipNineDisplay(scoreCardViewModel)
    Spacer(modifier = Modifier.size(12.dp))
    ButtonEnterScore(scoreCardViewModel, scoreCardViewModel::dialogAction)
    Spacer(modifier = Modifier.size(25.dp))

    DisplayPrevNextHoleButton( scoreCardViewModel::scoreCardActions)
}