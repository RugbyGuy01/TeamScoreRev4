package com.golfpvcc.teamscore_rev4
/*
Code is model after the you tube view Android Jetpack Compose Notes app with Photos
https://www.youtube.com/watch?v=FJZ__Ri38I0
 */
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.golfpvcc.teamscore_rev4.ui.navigation.SetupNavGraph
import com.golfpvcc.teamscore_rev4.ui.theme.TeamScore_Rev4Theme
import com.golfpvcc.teamscore_rev4.utils.createPointTableRecords
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeamScore_Rev4Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TeamScoreApp()
                }
            }
        }
    }
    @Composable
    fun TeamScoreApp(){
        lateinit var navHostController : NavHostController
        navHostController = rememberNavController()
        SetupNavGraph(navHostController)
    }
}

