package com.golfpvcc.teamscore_rev4.ui.screens.summary.utils

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.golfpvcc.teamscore_rev4.ui.screens.CardButton
import com.golfpvcc.teamscore_rev4.ui.screens.coursedetail.DisplaySaveCancelButtons
import com.golfpvcc.teamscore_rev4.ui.screens.playersetup.GetTeeInformation
import com.golfpvcc.teamscore_rev4.ui.screens.summary.SummaryActions
import com.golfpvcc.teamscore_rev4.ui.screens.summary.SummaryViewModel
import com.golfpvcc.teamscore_rev4.utils.DIALOG_BUTTON_TEXT_SIZE
import com.golfpvcc.teamscore_rev4.utils.LAST_PLAYER
import com.golfpvcc.teamscore_rev4.utils.MAX_COURSE_YARDAGE
import com.golfpvcc.teamscore_rev4.utils.MAX_EMAIL_ADDRESS_LEN
import com.golfpvcc.teamscore_rev4.utils.MAX_EMAIL_NAME_LEN
import com.golfpvcc.teamscore_rev4.utils.MAX_STARTING_HOLE
import com.golfpvcc.teamscore_rev4.utils.PQ_OTHER
import com.golfpvcc.teamscore_rev4.utils.SUMMARY_DIALOG_TEXT_SIZE
import com.golfpvcc.teamscore_rev4.utils.USER_TEXT_SAVE


@Composable
fun ConfigureEmailDialog(onAction: (SummaryActions) -> Unit, summaryViewModel: SummaryViewModel) {
    val saveButtonState = remember { mutableIntStateOf(USER_TEXT_SAVE) }

    Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { }) {
        Card(
            modifier = Modifier
                .width(400.dp)
                .padding(10.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                Arrangement.SpaceEvenly
            ) {
                GetTeeInformation(
                    mMaxLength = MAX_EMAIL_NAME_LEN,
                    placeHolder = "Email Name",
                    playerData = summaryViewModel.state.mEmailRecords[0].mEmailName,
//                    playerData = summaryViewModel.state.mEmailName,
                    updatedData = summaryViewModel::onEmailNameChange,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    Modifier.fillMaxWidth()
                )
                GetTeeInformation(
                    mMaxLength = MAX_EMAIL_ADDRESS_LEN,
                    placeHolder = "Email Address",
                    playerData =summaryViewModel.state.mEmailRecords[0].mEmailAddress,
//                    playerData = summaryViewModel.state.mEmailAddress,
                    updatedData = summaryViewModel::onEmailAddressChange,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done,
                    Modifier.fillMaxWidth()
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    Arrangement.SpaceEvenly
                ) {
                    CardButton("Cancel", Color.Transparent)
                    { onAction(SummaryActions.ShowEmailDialog) }
                    CardButton(
                        "Save",
                        Color.LightGray,
                        summaryViewModel.checkForValidEmailAddress()
                    )
                    { onAction(SummaryActions.SaveEmailRecord) }
                }
            }
        }
    }
}


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
                    val keyboardType: ImeAction
                    keyboardType = if (ptTable.key == PQ_OTHER) {
                        ImeAction.Done
                    } else {
                        ImeAction.Next
                    }

                    UpdatePointsTable(
                        ptTable.key,
                        ptTable.label,
                        summaryViewModel.getPointTableValue(ptTable.key),
                        2,
                        keyboardType,
                        summaryViewModel::onPointsTableChange
                    )
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CardButton("Save", Color.LightGray)
                    { onAction(SummaryActions.SavePointsDialog) }
                    CardButton("Cancel", Color.Transparent)
                    { onAction(SummaryActions.CancelPointsDialog) }
                }
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
    imeAction: ImeAction,
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
            imeAction = imeAction,  //.Done
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