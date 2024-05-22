package com.golfpvcc.teamscore_rev4.ui.screens.summary

import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.golfpvcc.teamscore_rev4.ui.navigation.ROOT_GRAPH_ROUTE
import com.golfpvcc.teamscore_rev4.ui.navigation.ROUTE_CONFIGURATION
import com.golfpvcc.teamscore_rev4.ui.navigation.TeamScoreScreen
import com.golfpvcc.teamscore_rev4.ui.screens.CardButton
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.DisplayControlButtons
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.DisplayMainScoreCard
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.GetScoreCardRecord
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.ScoreCardViewModel
import com.golfpvcc.teamscore_rev4.utils.SetScreenOrientation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun SummaryScreen(
    navController: NavHostController,
    id: Int?,
) {
    SetScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    val summaryViewModel = viewModel<SummaryViewModel>(
        factory = SummaryViewModel.SummaryViewModelFactor()
    )

    GetScoreCardRecord(summaryViewModel)

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {
        Scaffold()
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it)
            ) {
                Text(text = "Summary Screen")
                Spacer(modifier = Modifier.size(30.dp))
                BottomButtons(navController, summaryViewModel.state.mCourseId)
            }
        } // end of Scaffold
    }
}

@Composable
fun GetScoreCardRecord(
    summaryViewModel: SummaryViewModel
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        scope.launch(Dispatchers.IO) {
            summaryViewModel.getScoreCardAndPlayerRecord()
        }
    }
}

@Composable
fun BottomButtons(navController: NavHostController, courseId:Int) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        CardButton("Resume") {
            navController.navigate(route = TeamScoreScreen.ScreenScoreCard.route) { // back to the start of configuration
                popUpTo(ROOT_GRAPH_ROUTE)    // clear the back stack
            }
        }
        Log.d("VIN", "Summary buttons Id $courseId")
        CardButton("Players") {
            navController.navigate(TeamScoreScreen.ScreenPlayerSetup.passId(courseId))
            { // back to the start of configuration
                popUpTo(ROOT_GRAPH_ROUTE)    // clear the back stack
            }
        }

        CardButton("Courses") {
            navController.navigate(TeamScoreScreen.ScreenCourses.route) {
                popUpTo(ROOT_GRAPH_ROUTE)    // clear the back stack
            }
        }

        CardButton(" Exit ") {
            navController.navigate("exit")
        }

    }

}