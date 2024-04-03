package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


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

@Composable
fun DialogCard(
    symbol: String,
    modifier: Modifier,
    myFontSize: Int,
    backGround: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    // colors: CardColors = CardDefaults.cardColors(),
    Card(
        modifier = Modifier
            .padding(start = 3.dp, top = 3.dp, bottom = 3.dp, end = 3.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
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
