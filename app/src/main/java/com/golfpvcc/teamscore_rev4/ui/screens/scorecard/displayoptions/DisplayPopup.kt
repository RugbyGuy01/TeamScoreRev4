package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.displayoptions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.ScoreCardActions

@Composable
fun DisplayModeDropDown(onAction: (ScoreCardActions) -> Unit, GameNines:Boolean) {

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .height(40.dp)
            .width(110.dp)
            .padding(2.dp)
            .clickable { expanded = true }
            .background(color = Color.Yellow),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Display Mode")

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem({ Text(text = "Gross") }, onClick = {onAction(ScoreCardActions.ScreenModeGross)})
            DropdownMenuItem({ Text(text = "Net") }, onClick = {onAction(ScoreCardActions.ScreenModeNet)})
            DropdownMenuItem({ Text(text = "Standford") }, onClick = {onAction(ScoreCardActions.ScreenModeStableford)})
            DropdownMenuItem({ Text(text = "Pt Quote") }, onClick = {onAction(ScoreCardActions.ScreenModePtQuote)})
            if(GameNines) {
                DropdownMenuItem(
                    { Text(text = "Nine Game") },
                    onClick = { onAction(ScoreCardActions.ScreenModeNineGame) })
            }
        }
    }
}