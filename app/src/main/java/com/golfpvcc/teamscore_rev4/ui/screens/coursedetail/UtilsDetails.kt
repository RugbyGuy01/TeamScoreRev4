package com.golfpvcc.teamscore_rev4.ui.screens.coursedetail

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.golfpvcc.teamscore_rev4.ui.theme.shape
import com.golfpvcc.teamscore_rev4.utils.TeamObjects

@Composable
fun DisplayFlipHdcpsButtons(recDetail: CourseDetailViewModel) {
    Button(
        modifier = Modifier
            .padding(top = 20.dp, start = 20.dp)
            .height(40.dp),
        onClick = {
            recDetail.setFlipHdcpsChange(recDetail.state.mFlipHdcps)
        },
        shape = shape.large,
    ) {
        if (recDetail.state.mFlipHdcps) Text(text = "Normal") else Text(text = "Hdcp Flip")
    }
}

@Composable  // holeIdx is zero base index
fun DropDownSelectHolePar(
    recDetail: CourseDetailViewModel,
    holeIdx: Int
) {
    var expanded = if (recDetail.getPopupSelectHolePar() < 0) false else true
    val currentHolePar = recDetail.getHolePar(holeIdx)

    Popup(
        alignment = Alignment.CenterEnd,
        onDismissRequest = {
            recDetail.setPopupSelectHolePar(-1)
            expanded
        }
    ) {     // Composable content to be shown in the Popup
        Surface(
            modifier = Modifier.padding(1.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, Color.Red)
        ) //Well, its a border)
        {
            Column(
                modifier = Modifier
                    .padding(3.dp)
                    .width(110.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(15.dp),
                    text = "Hole ${holeIdx + 1}"
                )
                Divider(color = Color.Blue, thickness = 1.dp)
                TeamObjects.holeParList.forEach {
                    Divider(color = Color.Green, thickness = 1.dp)
                    Text(
                        text = "      ${it.Par}     ",
                        textAlign = TextAlign.Center,
                        color = if (currentHolePar == it.Par) Color.White else Color.Unspecified,
                        style = if (currentHolePar == it.Par) TextStyle(background = Color.Black) else TextStyle(
                            background = Color.Yellow
                        ),
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                recDetail.onParChange(holeIdx, it.Par)
                                recDetail.setPopupSelectHolePar(-1)
                            }
                    )
                }
            }
        }
    }
}
@Composable     // holeIdx is zero base index
fun DropDownSelectHoleHandicap(
    recDetail: CourseDetailViewModel,
    holeIdx: Int
) {
    var expanded = if (recDetail.getPopupSelectHoleHandicap() < 0) false else true
    val currentHoleHdcp = recDetail.getHoleHandicap(holeIdx)
    val courseHdcp = recDetail.state.availableHandicap
    val FlipHdcps = recDetail.getFlipHdcps()
    val displayFrontNineHdcp: Int

    if (FlipHdcps) {
        displayFrontNineHdcp =
            if (holeIdx < 9) 0 else 1 // used to display the handicap holes to select
    } else {
        displayFrontNineHdcp =
            if (holeIdx < 9) 1 else 0 // used to display the handicap holes to select
    }

    Popup(
        alignment = Alignment.CenterEnd,
        onDismissRequest = {
            recDetail.setPopupSelectHoleHdcp(-1)
           // expanded
        }
    ) {     // Composable content to be shown in the Popup
        Surface(
            modifier = Modifier.padding(1.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, Color.Red)
        ) //Well, its a border)
        {
            Column(
                modifier = Modifier
                    .padding(3.dp)
                    .width(100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(15.dp),
                    text = "Hole ${holeIdx + 1}"
                )
                Divider(color = Color.Blue, thickness = 1.dp)

                courseHdcp.forEachIndexed { inx, holeHdcp ->
                    if ((inx % 2) == displayFrontNineHdcp) {
                        if (courseHdcp[inx].available || currentHoleHdcp == courseHdcp[inx].holeHandicap) {
                            Divider(color = Color.Green, thickness = 1.dp)
                            Text(
                                text = "  ${courseHdcp[inx].holeHandicap}  ",
                                textAlign = TextAlign.Center,
                                color = if (currentHoleHdcp == courseHdcp[inx].holeHandicap) Color.White else Color.Unspecified,
                                style = if (currentHoleHdcp == courseHdcp[inx].holeHandicap) TextStyle(
                                    background = Color.Black
                                ) else TextStyle(
                                    background = Color.Yellow
                                ),
                                modifier = Modifier
                                    .padding(5.dp)
                                    .clickable {
                                        if (currentHoleHdcp != courseHdcp[inx].holeHandicap) {
                                            val returnHdcpToPool =
                                                courseHdcp.find { it.holeHandicap == currentHoleHdcp }
                                            if (returnHdcpToPool != null) {
                                                returnHdcpToPool.available = true
                                                Log.d(
                                                    "VIN",
                                                    "returnHdcpToPool ${returnHdcpToPool.holeHandicap}"
                                                )
                                            }
                                        }

                                        recDetail.onHandicapChange(
                                            holeIdx,
                                            courseHdcp[inx].holeHandicap
                                        )
                                        courseHdcp[inx].available =
                                            false        // this handicap has been selected
                                        Log.d(
                                            "VIN",
                                            "clickable Card Hole $holeIdx set to Hdcp ${courseHdcp[inx].holeHandicap} "
                                        )
                                        recDetail.setPopupSelectHoleHdcp(-1)
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}
fun currentHandicapConfiguration(cardHandicap: IntArray, availableHandicap: Array<HoleHandicap>) {

    for (idx in 0 until availableHandicap.size) {
        availableHandicap[idx].holeHandicap = idx + 1   // display handicap number in drop down menu
        availableHandicap[idx].available = true
    }
    cardHandicap.forEachIndexed { inx, holeHdcp ->
        if (holeHdcp != 0) {
            var removeHdcpFromPool: HoleHandicap? = availableHandicap.find { it.holeHandicap == holeHdcp }
            if (removeHdcpFromPool != null) {
                removeHdcpFromPool.available = false
            }
        }
    }
}
