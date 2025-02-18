package com.golfpvcc.teamscore_rev4.ui.screens.courses

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import com.golfpvcc.teamscore_rev4.database.model.CourseRecord
import com.golfpvcc.teamscore_rev4.ui.screens.coursedetail.CourseDetailViewModel
import com.golfpvcc.teamscore_rev4.utils.USER_SAVE
import com.golfpvcc.teamscore_rev4.utils.USER_TEXT_SAVE
import com.golfpvcc.teamscore_rev4.utils.USER_TEXT_UPDATE

@Composable
fun SaveCourseRecord(
    navController: NavController,
    saveButtonState: MutableState<Int>,
    courseViewModel:CoursesViewModel,
    recDetail: CourseDetailViewModel
) {
    if (!(saveButtonState.value == USER_TEXT_UPDATE || saveButtonState.value == USER_TEXT_SAVE)) {
        if (saveButtonState.value == USER_SAVE)
            courseViewModel.addOrUpdateCourse(
                CourseRecord(
                    mId = recDetail.state.mId,
                    mCoursename = recDetail.state.mCoursename,
                    mHandicap = recDetail.state.mHandicap,
                    mPar = recDetail.state.mPar,
                    mNotes = recDetail.state.mNotes
                )
            )
        navController.popBackStack()
        saveButtonState.value = USER_TEXT_UPDATE
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteDialog(
    openDialog: MutableState<Boolean>,
    text: MutableState<String>,
    action: () -> Unit,
    courseToDelete: MutableState<List<CourseRecord>>
) {
    Log.d("VIN", "DeleteDialog - Deleted course ")
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            confirmButton = {
                TextButton(onClick = {
                    action.invoke()
                    openDialog.value = false
                    courseToDelete.value = mutableListOf()

                })
                { Text(text = "OK") }
            },
            dismissButton = {
                TextButton(onClick = { openDialog.value = false})
                { Text(text = "Cancel") }
            },
            title = { Text(text = "Delete Course") },
            text = {  Column {
                Text(text.value)
            } }
        )
    }
}