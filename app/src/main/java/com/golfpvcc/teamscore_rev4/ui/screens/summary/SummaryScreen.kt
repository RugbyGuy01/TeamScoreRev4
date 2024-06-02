package com.golfpvcc.teamscore_rev4.ui.screens.summary

import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.golfpvcc.teamscore_rev4.ui.navigation.ROOT_GRAPH_ROUTE
import com.golfpvcc.teamscore_rev4.ui.navigation.TeamScoreScreen
import com.golfpvcc.teamscore_rev4.ui.screens.CardButton
import com.golfpvcc.teamscore_rev4.ui.screens.coursedetail.HoleDetail
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.PlayerHeading
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
                DisplayTeamTotalScore(summaryViewModel.state)
                Spacer(modifier = Modifier.size(20.dp))
                DisplayPlayersTotalScore(summaryViewModel.state)
                Spacer(modifier = Modifier.size(20.dp))
                BottomButtons(navController, summaryViewModel.state.mCourseId)
            }
        } // end of Scaffold
    }
}

@Composable
fun DisplayTeamTotalScore(state: State) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp), Arrangement.Center, Alignment.CenterHorizontally
        ) {
            Text(text = "Team scores")
        }
    }
}

@Composable
fun DisplayPlayersTotalScore(state: State) {
    val playersRecord = state.playerHeading
    LazyColumn(Modifier.padding(4.dp)) {
        items(playersRecord) { player ->
            DisplayPlayerScore(player)
            Spacer(modifier = Modifier.size(10.dp))
        }

    }
}

@Composable
fun DisplayPlayerScore(player: PlayerHeading) {
    Card(
        Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, Color.Black),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Row(
            modifier = Modifier
                .width(220.dp)
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Player Score ${player.mName}")
        }
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
fun BottomButtons(navController: NavHostController, courseId: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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