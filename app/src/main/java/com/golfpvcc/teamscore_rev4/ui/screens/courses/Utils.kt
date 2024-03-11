package com.golfpvcc.teamscore_rev4.ui.screens.courses

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.golfpvcc.teamscore_rev4.database.model.CourseRecord
import com.golfpvcc.teamscore_rev4.utils.TeamObjects


@Composable  // holeIdx is zero base index
fun DropDownSelectHolePar(
    parCurrentHole: MutableState<Int>,
    parCurrentHoleIdx: MutableState<Int>,
    showParPopup: Boolean,
)  {
    var expanded = showParPopup  // will show popu

    Popup(
        alignment = Alignment.CenterEnd,
        onDismissRequest = {
            parCurrentHoleIdx.value = -1 // close popup
            expanded
        }
    ) {     // Composable content to be shown in the Popup
        Surface(
            modifier = Modifier.padding(1.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, Color.Red)
        ) //Well, its a border)
        {
            Column(
                modifier = Modifier
                    .padding(3.dp)
                    .width(110.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(15.dp),
                    text = "Hole ${parCurrentHoleIdx.value + 1}"
                )
                Divider(color = Color.Blue, thickness = 1.dp)
                TeamObjects.holeParList.forEach {
                    Divider(color = Color.Green, thickness = 1.dp)
                    Text(
                        text = "      ${it.Par}     ",
                        textAlign = TextAlign.Center,
                        color = if (parCurrentHole.value == it.Par) Color.White else Color.Unspecified,
                        style = if (parCurrentHole.value == it.Par) TextStyle(background = Color.Black) else TextStyle(
                            background = Color.Yellow
                        ),
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                parCurrentHole.value = it.Par
                                Log.d("VIN", "after Popup par Hole ${parCurrentHoleIdx.value + 1} par ${parCurrentHole.value}")
                                parCurrentHoleIdx.value = -1
                            }
                    )
                }
            }
        }
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
            text = {  Column() {
                Text(text.value)
            } }
        )
    }
}