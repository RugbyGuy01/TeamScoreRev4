package com.golfpvcc.teamscore_rev4.ui.screens.coursedetail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GerericappBar(
    title: String,
    onIconClick: (() -> Unit),
    icon: @Composable() (() -> Unit),
    iconState: MutableState<Boolean>
) {

    TopAppBar(title = { Text(title) },
        colors = TopAppBarDefaults.topAppBarColors(Color.Yellow),
        actions = {
            IconButton(
                onClick = {
                    onIconClick?.invoke()
                },
                content = {
                    if (iconState.value){
                        icon?.invoke()
                    }
                }
            )
        }
    )
}