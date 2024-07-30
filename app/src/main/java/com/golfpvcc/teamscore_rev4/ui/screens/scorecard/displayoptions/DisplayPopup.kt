package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.displayoptions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.ScoreCardActions

@Composable
fun DisplayModeDropDown(onAction: (ScoreCardActions) -> Unit, gameNines: Boolean) {

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .height(40.dp)
            .width(110.dp)
            .padding(2.dp)
            .clickable { expanded = true }      // this causes the popup to be displayed
            .background(color = Color.Yellow),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Display Mode")

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                { Text(text = "Gross") },
                onClick = {
                    onAction(ScoreCardActions.ScreenModeGross)
                    expanded = false
                })
            DropdownMenuItem(
                { Text(text = "Net") },
                onClick = {
                    onAction(ScoreCardActions.ScreenModeNet)
                    expanded = false
                })
            DropdownMenuItem(
                { Text(text = "Standford") },
                onClick = {
                    onAction(ScoreCardActions.ScreenModeStableford)
                    expanded = false
                })
            DropdownMenuItem(
                { Text(text = "Pt Quote") },
                onClick = {
                    onAction(ScoreCardActions.ScreenModePtQuote)
                    expanded = false
                })
            DropdownMenuItem({ Text(text = "6 - 6 - 6") }, onClick = {
                onAction(ScoreCardActions.Screen6_6_6_Mode)
                expanded = false
            }
            )
            if (gameNines) {
                DropdownMenuItem(
                    { Text(text = "Nine Game") },
                    onClick = { onAction(ScoreCardActions.ScreenModeNineGame) })
            }
        }
    }
}