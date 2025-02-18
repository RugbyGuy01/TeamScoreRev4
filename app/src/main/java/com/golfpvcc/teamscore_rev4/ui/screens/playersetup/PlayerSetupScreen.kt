package com.golfpvcc.teamscore_rev4.ui.screens.playersetup

import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.golfpvcc.teamscore_rev4.utils.MAX_COURSE_YARDAGE
import com.golfpvcc.teamscore_rev4.utils.MAX_STARTING_HOLE
import com.golfpvcc.teamscore_rev4.utils.SCORE_CARD_REC_ID
import com.golfpvcc.teamscore_rev4.utils.SetScreenOrientation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun PlayerSetupScreen(
    navController: NavHostController,
    courseId: Int?,
) {
    val viewModel = viewModel<PlayerSetupViewModel>(
        factory = PlayerSetupViewModel.PlayerSetupViewModelFactor(courseId)
    )
    val scope = rememberCoroutineScope()
    val scoreRecState = remember { mutableStateOf(viewModel.state) }
    val modifier: Modifier = Modifier

    SetScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    LaunchedEffect(true) {
        if (courseId != -1) {
            scope.launch(Dispatchers.IO) {
                viewModel.getCourseById(courseId)
            }
        } else {
            Log.d("VIN", "Record ID not passed to Player setup screen!!")
        }
    }


    Scaffold {
        Spacer(modifier = Modifier.padding(it))
        Column(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxHeight()
        ) {
            DisplayCourseNameAndTeeSelection(modifier, viewModel)
            Spacer(modifier = Modifier.size(20.dp))
            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .height(500.dp)
            ) {
                scoreRecState.value.mPlayerRecords.forEachIndexed { index, playerRecord ->
                    EnterPlayerInfo(modifier, viewModel, index)
                }

                Spacer(modifier = Modifier.size(20.dp))
                DisplayBottomButtons(viewModel)
//                moveToNextScreen(viewModel, navController, scoreCardId = SCORE_CARD_REC_ID)
                moveToNextScreen(viewModel, navController)
            }
        }
    }
}

@Composable
fun DisplayCourseNameAndTeeSelection(
    modifier: Modifier,
    viewModel: PlayerSetupViewModel,
) {
    Column(modifier = Modifier) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Course Name:\n ${viewModel.state.mCourseName}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.size(14.dp))
            GetTeeInformation(
                mMaxLength = MAX_COURSE_YARDAGE,
                placeHolder = "Tee or Yardage",
                playerData = viewModel.state.mTee,
                updatedData = viewModel::onTeeStateChange,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                modifier.width(200.dp)
            )
        }
        Spacer(modifier = Modifier.size(10.dp))

        Row {
            GetTeeInformation(
                mMaxLength = MAX_STARTING_HOLE,
                placeHolder = "Starting Hole",
                playerData = viewModel.state.mStartingHole,
                updatedData = viewModel::onStartingHoleChange,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
                modifier.width(100.dp)
            )
        }
    }

}

@Composable
fun DisplayBottomButtons(
    viewModel: PlayerSetupViewModel,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        DisplayPlayerSetupButtons(viewModel::onButtonNewGame, "New Game")
        DisplayPlayerSetupButtons(viewModel::onButtonCancel, "Cancel")
        DisplayPlayerSetupButtons(viewModel::onButtonUpdate, "Update")
    }
}