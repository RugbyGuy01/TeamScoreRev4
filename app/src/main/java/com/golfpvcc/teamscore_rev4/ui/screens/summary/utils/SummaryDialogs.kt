package com.golfpvcc.teamscore_rev4.ui.screens.summary.utils

import android.content.pm.ActivityInfo
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.room.util.TableInfo
import com.golfpvcc.teamscore_rev4.ui.screens.CardButton
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore.DialogAction
import com.golfpvcc.teamscore_rev4.ui.screens.summary.SummaryViewModel
import com.golfpvcc.teamscore_rev4.utils.DIALOG_BUTTON_TEXT_SIZE
import com.golfpvcc.teamscore_rev4.utils.PQ_EAGLE
import com.golfpvcc.teamscore_rev4.utils.PQ_TARGET
import com.golfpvcc.teamscore_rev4.utils.SUMMARY_DIALOG_TEXT_SIZE
import com.golfpvcc.teamscore_rev4.utils.SUMMARY_TEXT_SIZE
import com.golfpvcc.teamscore_rev4.utils.SetScreenOrientation

@Composable
fun ConfigurePointsDialog(onAction: (SummaryActions) -> Unit, summaryViewModel: SummaryViewModel) {


    Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { onAction(SummaryActions.DisplayPointsDialog) }) {
        Card(
            modifier = Modifier
                .padding(10.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                Modifier
                    .width(200.dp)
                    .padding(10.dp)
            ) {
                for (ptTable in summaryViewModel.state.mGamePointsTable) {
                    UpdatePointsTable(
                        ptTable.key,
                        ptTable.label,
                        summaryViewModel.getPointTableValue(ptTable.key),
                        if (ptTable.key == PQ_TARGET) 2 else 1,
                        summaryViewModel::onPointsTableChange
                    )
                }
                CardButton("Save")
                { onAction(SummaryActions.DisplayPointsDialog) }
            }
        }
    }
}

@Composable
fun UpdatePointsTable(
    idx: Int,
    prompt: String,
    pointsValue: String,
    mMaxLength: Int,
    updatedData: (Int, String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val mContext = LocalContext.current

    Log.d("VIN", "UpdatePointsTable idx $idx  pt Data $pointsValue")
    OutlinedTextField(
        value = pointsValue,
        singleLine = true,
        label = { Text(text = prompt) },
        onValueChange = { points ->
            if (points.length <= mMaxLength && points != "-") updatedData(idx, points)
            else Toast.makeText(
                mContext,
                "Cannot be more than $mMaxLength Characters",
                Toast.LENGTH_SHORT
            ).show()
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Red,
            unfocusedBorderColor = Color.Blue,
            focusedLabelColor = Color.Red,
            unfocusedLabelColor = Color.Blue,
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next,  //.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        )
    )
}

@Composable
fun AboutDialog(onAction: (SummaryActions) -> Unit) {
    val openDialog = remember { mutableStateOf(true) }

    AlertDialog( // 3
        onDismissRequest = { // 4
            // SummaryActions.DisplayAboutDialog
        },
        // 5
        title = { Text(text = "About Team Score") },
        text = {
            Text(
                text = "Team Score App.\n" +
                        " Written by Vinnie Gamble\n\n" +
                        "Contact: Vgamble@golfpvcc.com",
                fontSize = SUMMARY_DIALOG_TEXT_SIZE.sp,
            )
        },
        confirmButton = { // 6
            Button(
                onClick = {
                    onAction(SummaryActions.DisplayAboutDialog)
                }
            ) {
                Text(
                    text = "Ok",
                    fontSize = DIALOG_BUTTON_TEXT_SIZE.sp,
                    color = Color.White
                )
            }
        }
    )
}