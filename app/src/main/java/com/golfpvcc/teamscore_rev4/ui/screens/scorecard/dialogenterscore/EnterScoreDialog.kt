package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.ScoreCardViewModel
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils.ScoreCardActions
import com.golfpvcc.teamscore_rev4.utils.MAX_PLAYERS

const val TEXT_WIDTH = 65
const val ROW_WIDTH = 500
const val CARD_ROW_WIDTH = 650
const val ROW_BOTTOM_PAD = 8
const val PLAYER_TEXT_SIZE = 25
const val PLAYER_NAME_WIDTH = 145
const val PLAYER_SCORE_WIDTH = 55
const val TEAM_SCORE_WIDTH = 70
const val BUTTON_NUMBER_FONT = 45
const val BUTTON_ACTION_FONT = 25
const val BUTTON_NET_GROSS_FONT = 15
const val BUTTON_JUNK_FONT = 15
const val BUTTON_ENTER_SCORE_TEXT = "Enter Scores"

@Composable
fun ButtonEnterScore(
    scoreCardViewModel: ScoreCardViewModel,
    onAction: (DialogAction) -> Unit,
    onScoreCardAction: (ScoreCardActions) -> Unit
) {

    if (scoreCardViewModel.state.mDialogEnterScores) {
        EnterPlayersScores(scoreCardViewModel, onAction)
    }

    Button(
        // onDismissRequest = { onAction(DialogAction.Done) }
        onClick = {
            Log.d("VIN", "Click ButtonEnterScore")
            onScoreCardAction(ScoreCardActions.ButtonEnterScore)
            onScoreCardAction(ScoreCardActions.SetDialogCurrentPlayer)
        },
        contentPadding = PaddingValues(10.dp),
        shape = RectangleShape,
        modifier = Modifier.height(40.dp),
    ) {
        Text(text = BUTTON_ENTER_SCORE_TEXT)
    }
}

@Composable
fun EnterPlayersScores(
    // display the dialog window
    scoreCardViewModel: ScoreCardViewModel,
    onAction: (DialogAction) -> Unit,
) {
    val rowPlayerNames = scoreCardViewModel.state.mPlayerHeading
    val state = scoreCardViewModel.state
    val currentHole: Int = state.mCurrentHole
    val holeHandicap: Int = scoreCardViewModel.getHoleHandicap(currentHole)


    if (state.mDialogEnterScores) {
        if (state.mDialogDisplayJunkSelection) {
            DisplayJunkDialog(scoreCardViewModel, onAction)
        } else {
            Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
                onDismissRequest = { onAction(DialogAction.Done) }) {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    DisplayEnterScoreHeading()

                    Row(horizontalArrangement = Arrangement.Start) { // put the header test here

                        Column(modifier = Modifier.weight(.75f)) {
                            DisplayCurrentHoleHeading(currentHole, holeHandicap)
                            DisplayPlayerNameAndScoreHeading()
                            for (idx in rowPlayerNames.indices) {
                                if (idx < MAX_PLAYERS) {
                                    Row(horizontalArrangement = Arrangement.Start) {
                                        val playerName =
                                            rowPlayerNames[idx].mHdcp + " - " + rowPlayerNames[idx].mName

                                        val playerStokeHoleColor =
                                            scoreCardViewModel.getStokeOnHolePlayerColor( //determine the stokes a player gets on hole by the color
                                                rowPlayerNames[idx].mStokeHole[currentHole]
                                            )


                                        val backgroundColorForStokes: Color =
                                            scoreCardViewModel.getHighLiteActivePlayerColor(idx)
                                        val playerHoleScore =
                                            scoreCardViewModel.getDialogPlayerHoleScore(
                                                idx,
                                                currentHole
                                            )

                                        DisplayPlayerNames(
                                            playerName,
                                            playerStokeHoleColor,
                                            backgroundColorForStokes,
                                            playerHoleScore,
                                            idx,
                                            onAction
                                        )
                                        DisplayTeamGrossNetButton(scoreCardViewModel, idx, onAction)
                                        DisplayJunkButton(
                                            scoreCardViewModel,
                                            idx,
                                            currentHole,
                                            onAction
                                        )
                                    }
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(.4f)
                                .background(Color.Cyan, shape = RoundedCornerShape(20.dp))
                                .padding(top = 8.dp, start = 5.dp, end = 4.dp, bottom = 0.dp)
                        ) {
                            val modifier: Modifier = Modifier
                                .width(TEXT_WIDTH.dp)
                                .background(Color.LightGray)
                            DisplayKeyPad(onAction, modifier)
                            DisplayActionButtons(onAction, modifier)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayJunkButton(
    scoreCardViewModel: ScoreCardViewModel,
    idx: Int,
    currentHole: Int,
    onAction: (DialogAction) -> Unit
) {

    DialogCard(
        symbol = "Junk",
        modifier = Modifier.padding(1.dp),
        myFontSize = BUTTON_JUNK_FONT,
        backGround = scoreCardViewModel.state.mJunkButtonColor[idx],
        textColor = Color.Black,
        onClick = { onAction(DialogAction.DisplayJunkDialog(idx)) }) {
    }
}

@Composable
fun DisplayTeamGrossNetButton(
    scoreCardViewModel: ScoreCardViewModel, idx: Int, onAction: (DialogAction) -> Unit
) {

    DialogCard(symbol = "Net",
        modifier = Modifier.padding(1.dp),
        myFontSize = BUTTON_NET_GROSS_FONT,
        backGround = scoreCardViewModel.state.mNetButtonColor[idx],
        textColor = Color.Black,
        onClick = { onAction(DialogAction.Net(idx)) },
        onLongClick = { onAction(DialogAction.NetLongClick(idx)) })
    Spacer(modifier = Modifier.width(10.dp))
    DialogCard(
        symbol = "Gross",
        modifier = Modifier.padding(1.dp),
        myFontSize = BUTTON_NET_GROSS_FONT,
        backGround = scoreCardViewModel.state.mGrossButtonColor[idx],
        textColor = Color.Black,
        onClick = { onAction(DialogAction.Gross(idx)) },
        onLongClick = { onAction(DialogAction.GrossLongClick(idx)) },
    )
}

@Composable
fun DisplayPlayerNameAndScoreHeading() {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "Player Name",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(PLAYER_NAME_WIDTH.dp)
                .padding(vertical = 2.dp),
            fontSize = 20.sp
        )
        Text(
            text = "Score",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(PLAYER_SCORE_WIDTH.dp)
                .padding(vertical = 2.dp),
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.width(50.dp))
        Text(
            text = "Team",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(TEAM_SCORE_WIDTH.dp)
                .padding(vertical = 2.dp),
            fontSize = 20.sp
        )
    }
}

@Composable
fun DisplayCurrentHoleHeading(currentHole: Int, holeHandicap: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Current Hole ${currentHole + 1} Handicap $holeHandicap",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(300.dp)
                .padding(vertical = 5.dp),
            fontSize = 20.sp
        )
    }
}

@Composable
fun DisplayEnterScoreHeading() {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Enter Score",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(300.dp)
                .padding(vertical = 5.dp),
            fontSize = 20.sp
        )
    }
}

@Composable
fun DisplayActionButtons(
    onAction: (DialogAction) -> Unit,
    modifier: Modifier
) {

    Row(
        modifier = Modifier
            .padding(bottom = ROW_BOTTOM_PAD.dp)
            .width(CARD_ROW_WIDTH.dp),
        horizontalArrangement = Arrangement.Start,
    ) {
        DialogCard(symbol = "Clear",
            modifier = Modifier.padding(start = 10.dp, top = 2.dp, bottom = 2.dp, end = 10.dp),
            myFontSize = BUTTON_ACTION_FONT,
            backGround = Color.DarkGray,
            textColor = Color.White,
            onClick = { onAction(DialogAction.Clear) },
            onLongClick = {})
        Spacer(modifier = Modifier.width(30.dp))
        DialogCard(symbol = "Done",
            modifier = Modifier.width(80.dp),
            myFontSize = BUTTON_ACTION_FONT,
            backGround = Color.DarkGray,
            textColor = Color.White,
            onClick = { onAction(DialogAction.Done) },
            onLongClick = {})
    }
}

@Composable
fun DisplayKeyPad(
    onAction: (DialogAction) -> Unit, modifier: Modifier
) {
    Row(
        modifier = Modifier
            .padding(bottom = ROW_BOTTOM_PAD.dp)
            .width(ROW_WIDTH.dp),
        horizontalArrangement = Arrangement.spacedBy(ROW_BOTTOM_PAD.dp),
    ) {
        for (idx in 1..3) {
            DialogButton(symbol = idx.toString(),
                modifier = modifier,
                myFontSize = BUTTON_NUMBER_FONT,
                onClick = { onAction(DialogAction.Number(idx)) })
        }
    }
    Row(
        modifier = Modifier
            .padding(bottom = ROW_BOTTOM_PAD.dp)
            .width(ROW_WIDTH.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (idx in 4..6) {
            DialogButton(symbol = idx.toString(),
                modifier = modifier,
                myFontSize = BUTTON_NUMBER_FONT,
                onClick = { onAction(DialogAction.Number(idx)) })
        }
    }
    Row(
        modifier = Modifier
            .width(ROW_WIDTH.dp)
            .padding(bottom = ROW_BOTTOM_PAD.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (idx in 7..9) {
            DialogButton(symbol = idx.toString(),
                modifier = modifier,
                myFontSize = BUTTON_NUMBER_FONT,
                onClick = { onAction(DialogAction.Number(idx)) })
        }
    }
}