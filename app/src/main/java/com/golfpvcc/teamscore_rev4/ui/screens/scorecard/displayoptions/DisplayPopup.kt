package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.displayoptions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.sp
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.ScoreCardActions
import com.golfpvcc.teamscore_rev4.utils.SCORE_CARD_COURSE_NAME_TEXT

@Composable
fun DisplayModeDropDown(
    onAction: (ScoreCardActions) -> Unit,
    gameNines: Boolean,
    displayScoreText: String,
) {

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .height(25.dp)  // 40
            .width(110.dp)  // 110
            .padding(2.dp)
            .clickable { expanded = true }      // this causes the popup to be displayed
            .background(color = Color.Yellow),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Display Mode",
            fontSize = SCORE_CARD_COURSE_NAME_TEXT.sp
        )

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
            if (gameNines) {
                DropdownMenuItem(
                    { Text(text = "Nine Game") },
                    onClick = {
                        onAction(ScoreCardActions.ScreenModeNineGame)
                        expanded = false
                    })
            }
            HorizontalDivider(thickness = 2.dp)
            DropdownMenuItem({ Text(text = displayScoreText) }, onClick = {
                onAction(ScoreCardActions.Screen666Mode)
                expanded = false
            }
            )
        }
    }
}