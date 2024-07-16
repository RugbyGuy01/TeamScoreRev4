package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.JunkTableList
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.ScoreCardViewModel
import com.golfpvcc.teamscore_rev4.utils.DOUBLE_TEAM_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_DOUBLE_GROSS_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_DOUBLE_NET_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_GROSS_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_NET_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_SINGLE_GROSS_SCORE
import com.golfpvcc.teamscore_rev4.utils.TEAM_SINGLE_NET_SCORE


@Composable
fun DialogButton(
    symbol: String,
    modifier: Modifier,
    myFontSize: Int,
    onClick: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(2.dp))
            .clickable { onClick() }
            .then(modifier)
    ) {
        Text(
            text = symbol,
            fontSize = myFontSize.sp,
            color = Color.Black
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DialogCard(
    symbol: String,
    modifier: Modifier,
    myFontSize: Int,
    backGround: Color,
    textColor: Color,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(start = 3.dp, top = 3.dp, bottom = 3.dp, end = 3.dp)
            .clip(RoundedCornerShape(10.dp))
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            ),
        colors = CardDefaults.cardColors(
            containerColor = backGround, //Card background color
            contentColor = textColor  //Card content color,e.g.text
        )
    ) {
        Text(
            text = symbol,
            modifier = Modifier.padding(15.dp),
            fontSize = myFontSize.sp,
//            color = Color.Black
        )
    }
}

fun getTeamButtonColor(teamHoleMask: Int): Color {
    var resultColor: Color

    when (teamHoleMask) {
        TEAM_GROSS_SCORE -> {
            resultColor = Color(TEAM_SINGLE_GROSS_SCORE)
        }

        TEAM_NET_SCORE -> {
            resultColor = Color(TEAM_SINGLE_NET_SCORE)
        }

        TEAM_GROSS_SCORE + DOUBLE_TEAM_SCORE -> {
            resultColor = Color(TEAM_DOUBLE_GROSS_SCORE)
        }

        TEAM_NET_SCORE + DOUBLE_TEAM_SCORE -> {
            resultColor = Color(TEAM_DOUBLE_NET_SCORE)
        }

        else -> {
            resultColor = Color.LightGray
        }
    }
    return resultColor
}

fun teamScoreTypeNet(teamHoleMask: Int): Boolean {        // return True if the score is a net score else false for a gross score

    val grossSingle: Boolean = (teamHoleMask == TEAM_GROSS_SCORE)
    val grossDouble: Boolean = (teamHoleMask == (TEAM_GROSS_SCORE + DOUBLE_TEAM_SCORE))

    return if (grossSingle or grossDouble) (true) else (false)
}

@Composable
fun DisplayPlayerNames(
    playerName: String,
    backGroundColorForStrokes: Color,
    playerHoleScore: String,
    playerIdx: Int,
    onAction: (DialogAction) -> Unit,
) {
    Text(
        text = playerName,
        modifier = Modifier
            .padding(5.dp)
            .width(PLAYER_NAME_WIDTH.dp),
        fontSize = PLAYER_TEXT_SIZE.sp
    )

    Text(
        text = playerHoleScore,
        modifier = Modifier
            .padding(5.dp)
            .width(PLAYER_SCORE_WIDTH.dp)
            .clickable { onAction(DialogAction.SetDialogCurrentPlayer(playerIdx)) },
        style = TextStyle(background = backGroundColorForStrokes),
        fontSize = PLAYER_TEXT_SIZE.sp
    )
}

@Composable
fun DisplayJunkDialog(
    scoreCardViewModel: ScoreCardViewModel,
    onAction: (DialogAction) -> Unit,
) {

    Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { }) {   //must hit Exit
        Card(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxHeight(),
            shape = RoundedCornerShape(6.dp),
        ) {
            Column(
                Modifier
                    .width(100.dp)
                //.padding(10.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(.9f)
                ) {
                    itemsIndexed(scoreCardViewModel.state.mJunkTableSelection.mJunkTableList) { index, junkList ->
                        JunkListItem(junkList, index, onAction)
                    }
                } // end of junk selection list
                HorizontalDivider(thickness = 1.dp, color = Color.Blue)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .weight(.1f)
                        .clickable { onAction(DialogAction.CloseJunkTableList) },
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(text = "Done")
                }
            }
        }
    }
}

@Composable //Display the Junk record in a Card
fun JunkListItem(
    junkTableList: JunkTableList,
    listIdx: Int,
    onAction: (DialogAction) -> Unit,
) {
    val backGround = if (junkTableList.mSelected) {
        Color.LightGray
    } else {
        Color.Transparent
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .height(30.dp)
            .clickable { onAction(DialogAction.ToggleJunkListItem(listIdx)) },
        border = BorderStroke(1.dp, Color.Black),
        colors = CardDefaults.cardColors(
            containerColor = backGround, //Card background color
            contentColor = Color.Blue  //Card content color,e.g.text
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = junkTableList.mJunkName,
                //style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Right
            )
        }
    }
}