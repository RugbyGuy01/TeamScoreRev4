package com.golfpvcc.teamscore_rev4.ui.screens.scorecard

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.golfpvcc.teamscore_rev4.ui.navigation.ROOT_GRAPH_ROUTE
import com.golfpvcc.teamscore_rev4.ui.navigation.ROUTE_CONFIGURATION
import com.golfpvcc.teamscore_rev4.ui.navigation.ROUTE_GAME_ON
import com.golfpvcc.teamscore_rev4.ui.screens.coursedetail.DisplayFlipHdcpsButtons
import com.golfpvcc.teamscore_rev4.ui.screens.coursedetail.DisplaySaveCancelButtons
import com.golfpvcc.teamscore_rev4.ui.screens.coursedetail.GetCourseName
import com.golfpvcc.teamscore_rev4.ui.screens.coursedetail.ShowHoleDetailsList
import com.golfpvcc.teamscore_rev4.ui.screens.courses.SaveCourseRecord
import com.golfpvcc.teamscore_rev4.utils.setScreenOrientation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ScoreCardScreen(
    navController: NavHostController,
    id: Int?,
) {
    setScreenOrientation(SCREEN_ORIENTATION_LANDSCAPE)

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
                        .fillMaxHeight()
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
fun DisplayControlButtons(scoreCardViewModel: ScoreCardViewModel) {
    FlipNineDisplay(scoreCardViewModel)
    //EnterPlayersScores(scoreCardViewModel)
}