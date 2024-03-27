package com.golfpvcc.teamscore_rev4.ui.screens.playersetup

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.golfpvcc.teamscore_rev4.ui.navigation.ROOT_GRAPH_ROUTE
import com.golfpvcc.teamscore_rev4.ui.navigation.ROUTE_CONFIGURATION
import com.golfpvcc.teamscore_rev4.ui.navigation.ROUTE_GAME_ON
import com.golfpvcc.teamscore_rev4.ui.navigation.TeamScoreScreen
import com.golfpvcc.teamscore_rev4.ui.theme.shape
import com.golfpvcc.teamscore_rev4.utils.Constants

@Composable
fun EnterPlayerInfo(
    modifier: Modifier,
    viewModel: PlayerSetupViewModel,
    index: Int,
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        GetPlayerSetupInformation(
            index = index,
            mMaxLength = Constants.MAX_PLAYER_NAME,
            placeHolder = "Enter Player Name",
            nameOrHandicap = viewModel.state.mPlayerRecords[index].mName,
            updatedData = viewModel::onPlayerNameChange,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
            modifier = modifier.width(175.dp),
        )

        val keyboardType: ImeAction
        keyboardType = if (index == Constants.LAST_PLAYER) {
            ImeAction.Done
        } else {
            ImeAction.Next
        }
        Spacer(modifier = Modifier.size(10.dp))
        GetPlayerSetupInformation(
            index = index,
            Constants.MAX_HANDICAP,
            "Handicap",
            viewModel.state.mPlayerRecords[index].mHandicap.toString(),
            updatedData = viewModel::onPlayerHandicapChange,
            KeyboardType.Number,
            imeAction = keyboardType,
            modifier = modifier.width(100.dp),
        )

    }
}

@Composable
fun GetPlayerSetupInformation(
    index: Int,
    mMaxLength: Int,
    placeHolder: String,
    nameOrHandicap: String,
    updatedData: (Int, String) -> Unit,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    modifier: Modifier,
) {
    val focusManager = LocalFocusManager.current
    val mContext = LocalContext.current

    OutlinedTextField(
        modifier = modifier,
        value = nameOrHandicap,
        textStyle = MaterialTheme.typography.headlineSmall, //to small for screen bodyLarge -- to large for screen - displaySmall
        singleLine = true,
        onValueChange = { playerData ->
            if (!playerData.contains('.')) {
                if (playerData.length <= mMaxLength) updatedData(index, playerData)
                else Toast.makeText(
                    mContext,
                    "Cannot be more than $mMaxLength Characters",
                    Toast.LENGTH_SHORT
                ).show()
            }
        },
        label = { Text(text = placeHolder) },
        placeholder = { Text(text = placeHolder) },
        shape = shape.small,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Red,
            unfocusedBorderColor = Color.Blue,
            focusedLabelColor = Color.Red,
            unfocusedLabelColor = Color.Blue,
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = keyboardType,
            imeAction = imeAction  //.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        )

    )
}

@Composable
fun GetTeeInformation(
    mMaxLength: Int,
    placeHolder: String,
    playerData: String,
    updatedData: (String) -> Unit,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    modifier : Modifier,
) {
    val focusManager = LocalFocusManager.current
    val mContext = LocalContext.current

    OutlinedTextField(
        modifier = modifier,    // clear the Tee field white, green
        value = playerData,
        textStyle = MaterialTheme.typography.headlineSmall,
        singleLine = true,
        onValueChange = { teeData ->
            if (teeData.length <= mMaxLength) updatedData(teeData)
            else Toast.makeText(
                mContext,
                "Cannot be more than $mMaxLength Characters",
                Toast.LENGTH_SHORT
            ).show()
        },
        label = { Text(text = placeHolder) },
        placeholder = { Text(text = placeHolder) },
        shape = shape.small,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Red,
            unfocusedBorderColor = Color.Blue,
            focusedLabelColor = Color.Red,
            unfocusedLabelColor = Color.Blue,
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = keyboardType,
            imeAction = imeAction  //.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        )
    )
}

@Composable
fun DisplayPlayerSetupButtons(
    onButtonSelection: () -> Int,
    buttonText: String,
    navHostController: NavHostController,
) {
    var nextScreen: Int

    Button(
        modifier = Modifier
            .padding(top = 5.dp, start = 5.dp)
            .height(40.dp),
        onClick = {
            nextScreen = onButtonSelection()
            if (nextScreen == Constants.USER_CANCEL) {
                navHostController.popBackStack() // display courses screen
            }
        },
        shape = shape.large
    ) {
        Text(text = buttonText)
    }
}

fun moveToNextScreen(
    viewModel: PlayerSetupViewModel,
    navController: NavHostController,
    scoreCardId: Int
) {

    if (viewModel.state.mNextScreen == Constants.DISPLAY_SCORE_CARD_SCREEN) {
        Log.d("VIN", "On to game on")
        navController.navigate(ROUTE_GAME_ON) {
            popUpTo(ROOT_GRAPH_ROUTE) {
                inclusive = true
            }
        }
    }
}
