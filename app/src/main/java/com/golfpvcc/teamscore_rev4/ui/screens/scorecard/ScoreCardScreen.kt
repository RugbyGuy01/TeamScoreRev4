package com.golfpvcc.teamscore_rev4.ui.screens.scorecard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController

@Composable
fun ScoreCardScreen(
    navHostController: NavHostController,
    id: Int?,
) {
    Surface(color = Color.Yellow, modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "ScoreCard")
            Button(
                onClick = { navHostController.navigate(route = "Configuration") },
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Text(text = "Navigate")
            }
        }
    }
}