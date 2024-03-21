package com.golfpvcc.teamscore_rev4.ui.screens.scorecard

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.golfpvcc.teamscore_rev4.ui.navigation.ROOT_GRAPH_ROUTE
import com.golfpvcc.teamscore_rev4.ui.navigation.ROUTE_CONFIGURATION
import com.golfpvcc.teamscore_rev4.ui.navigation.ROUTE_GAME_ON

@Composable
fun ScoreCardScreen(
    navHostController: NavHostController,
    id: Int?,
) {
    val activity = LocalContext.current as Activity
    activity.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE


    Surface(color = Color.Yellow, modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "ScoreCard id: $id")
            Button(
                onClick = {
                    navHostController.navigate(route = ROUTE_CONFIGURATION) {
                        popUpTo(ROOT_GRAPH_ROUTE){
                            inclusive = true
                        }
                    }

                },
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Text(text = "Navigate")
            }
        }
    }
}