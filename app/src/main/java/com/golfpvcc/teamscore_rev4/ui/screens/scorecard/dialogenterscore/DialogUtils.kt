package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onClick: () -> Unit
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
    onLongClick:() -> Unit
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
    playerHoleScore:String,
    playerIdx:Int,
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
