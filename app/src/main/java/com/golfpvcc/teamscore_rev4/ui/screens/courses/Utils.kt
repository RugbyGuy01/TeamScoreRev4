package com.golfpvcc.teamscore_rev4.ui.screens.courses

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.golfpvcc.teamscore_rev4.database.model.CourseRecord


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteDialog(
    openDialog: MutableState<Boolean>,
    text: MutableState<String>,
    action: () -> Unit,
    courseToDelete: MutableState<List<CourseRecord>>
) {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            confirmButton = {
                TextButton(onClick = {
                    openDialog.value = false
                    courseToDelete.value = mutableListOf()
                    action.invoke()
                })
                { Text(text = "OK") }
            },
            dismissButton = {
                TextButton(onClick = {})
                { Text(text = "Cancel") }
            },
            title = { Text(text = "Delete Course") },
            text = {  Column() {
                Text(text.value)
            } }
        )
    }
}