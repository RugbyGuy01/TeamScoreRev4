package com.golfpvcc.teamscore_rev4.ui.screens.coursedetail

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.golfpvcc.teamscore_rev4.ui.screens.courses.CoursesViewModel
import com.golfpvcc.teamscore_rev4.ui.screens.courses.SaveCourseRecord
import com.golfpvcc.teamscore_rev4.utils.Constants.USER_CANCEL
import com.golfpvcc.teamscore_rev4.utils.Constants.USER_SAVE
import com.golfpvcc.teamscore_rev4.utils.Constants.USER_TEXT_SAVE
import com.golfpvcc.teamscore_rev4.utils.Constants.USER_TEXT_UPDATE
import com.golfpvcc.teamscore_rev4.utils.Constants.courseDetailPlaceHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CourseDetailScreen(
    navController: NavController,
    courseID: Int?,
) {
    val courseViewModel = viewModel<CoursesViewModel>(
        factory = CoursesViewModel.CoursesViewModelFactor()
    )
    val scope = rememberCoroutineScope()
    val courseRec = remember {
        mutableStateOf(courseDetailPlaceHolder)
    }
    val recDetail = remember { CourseDetailViewModel() }

    val saveButtonState = remember { mutableStateOf(USER_TEXT_SAVE) }
    val currentFlipButton = remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        scope.launch(Dispatchers.IO) {
            courseRec.value = courseViewModel.getCourseById(courseID) ?: courseDetailPlaceHolder
            Log.d("VIN", "Get mCoursename = ${courseRec.value.mCoursename}")
            recDetail.onCourseNameChange(courseRec.value.mCoursename)
            recDetail.setHandicap(courseRec.value.mHandicap)
            recDetail.setPar(courseRec.value.mPar)
            recDetail.setCourseId(courseRec.value.mId)

            saveButtonState.value =
                if (courseRec.value.mId == 0) USER_TEXT_SAVE else USER_TEXT_UPDATE
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {
        Scaffold()
        {
            Spacer(modifier = Modifier.padding(it))
            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxHeight()
            ) {
                Row {
//                    GetCourseName(currentCoursename)
                    GetCourseName(recDetail)
                    DisplayFlipHdcpsButtons(currentFlipButton)
                }
                Spacer(modifier = Modifier.size(12.dp))
                Divider(color = Color.Blue, thickness = 1.dp)
                Spacer(modifier = Modifier.size(12.dp))
                ShowHoleDetailsList(
                    modifier = Modifier.weight(1f),
                    recDetail,
                )
//        if (parCurrentHoleIdx.value != -1) {     // the function is called many time, if click par/hdcp button then popup select hole will be set
//            val holeIdx = parCurrentHoleIdx
//            Log.d("VIN", "before Popup par Hole ${parCurrentHoleIdx.value + 1} par $parCurrentHole")
//            DropDownSelectHolePar(parCurrentHole, parCurrentHoleIdx)
//            Log.d("VIN", "after Popup par Hole ${parCurrentHoleIdx.value + 1} par ${parCurrentHole.value}")
//        }
                Spacer(modifier = Modifier.size(12.dp))
                Divider(color = Color.Blue, thickness = 1.dp)
                Spacer(modifier = Modifier.size(12.dp))
                DisplaySaveCancelButtons(saveButtonState)
                SaveCourseRecord(
                    navController,
                    saveButtonState,
                    courseViewModel,
                    recDetail,
                )
            }
        }
    }
}

@Composable
fun DisplaySaveCancelButtons(
    saveButtonState: MutableState<Int>,
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = {
                saveButtonState.value = USER_CANCEL
            },
            modifier = Modifier
                .height(40.dp),  //vpg
        ) {
            Text(text = "Cancel")
        }
        Button(
            onClick = {
                saveButtonState.value = USER_SAVE
            },
            modifier = Modifier
                .height(40.dp),  //vpg
        ) {
            Text(
                text = if (saveButtonState.value == USER_TEXT_UPDATE) {
                    "Update"
                } else {
                    "Save"
                }
            )
        }
    }
}

@Composable
fun ShowHoleDetailsList(
    modifier: Modifier,
    recDetail: CourseDetailViewModel,
) {

    LazyColumn(modifier) {
        items(recDetail.state.mPar.size) { idx ->
            HoleDetail(recDetail, idx)
        }

    }
//    if (parCurrentHole.value != -1) {     // the function is called many time, if click par/hdcp button then popup select hole will be set
//        DropDownSelectHolePar(parCurrentHole, parCurrentHoleIdx)
//        Log.d("VIN", "Popup par Hole ${parCurrentHoleIdx.value + 1} par $parCurrentHole")
//    }
//    if (hdcpHoleInx != -1) {
//        DropDownSelectHoleHandicap(viewModel, hdcpHoleInx)
//    }
}

@Composable
fun HoleDetail(
    recDetail: CourseDetailViewModel,
    holeIdx: Int,
) {

    Card(
        border = BorderStroke(1.dp, Color.Black),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Row(
            modifier = Modifier
                .width(210.dp)
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = {

                },
            ) {
                Text(
                    text = "Par ${recDetail.state.mPar[holeIdx]}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "Hole ${holeIdx + 1}",
                style = MaterialTheme.typography.bodyLarge,
            )
            TextButton(
                onClick = { },
            ) {
                val displayText =
                    if (recDetail.state.mHandicap[holeIdx] == 0) " --  " else recDetail.state.mHandicap[holeIdx]
                Text(
                    text = "Hdcp $displayText",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun GetCourseName(
    recDetail: CourseDetailViewModel
//    currentCoursename: MutableState<String>,

) {
    val focusManager = LocalFocusManager.current
    val mMaxLength = 15
    val mContext = LocalContext.current

    if (courseDetailPlaceHolder.mCoursename == recDetail.state.mCoursename) {
        recDetail.onCourseNameChange("")
    }

    OutlinedTextField(
        modifier = Modifier.width(200.dp),
        value = recDetail.state.mCoursename,
        textStyle = MaterialTheme.typography.bodyLarge,
        singleLine = true,
        onValueChange = { value ->
            if (value.length <= mMaxLength) recDetail.onCourseNameChange(value)
            else Toast.makeText(
                mContext,
                "Cannot be more than $mMaxLength Characters",
                Toast.LENGTH_SHORT
            ).show()
        },
        label = { Text(text = "Course Name") },
        placeholder = { Text(text = "Enter Course Name") },
//        shape = shape.,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Red,
            unfocusedBorderColor = Color.Blue,
            focusedLabelColor = Color.Red,
            unfocusedLabelColor = Color.Blue,
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        )
    )
}

@Composable
fun DisplayFlipHdcpsButtons(currentFlipButton: MutableState<Boolean>) {
    Button(
        modifier = Modifier
            .padding(top = 20.dp, start = 20.dp)
            .height(40.dp),
        onClick = {
            currentFlipButton.value = !currentFlipButton.value
        },
    ) {
        if (currentFlipButton.value) Text(text = "Normal") else Text(text = "Hdcp Flip")
    }
}

