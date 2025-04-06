package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils

import com.golfpvcc.teamscore_rev4.utils.DISPLAY_HOLE_NUMBER
import com.golfpvcc.teamscore_rev4.utils.DISPLAY_NOTE_ON_HOLE
import com.golfpvcc.teamscore_rev4.utils.VIN_LIGHT_GRAY


const val HDCP_HEADER: Int = 100
const val PAR_HEADER: Int = 101
const val HOLE_HEADER: Int = 102
const val TEAM_HEADER: Int = 121
const val USED_HEADER: Int = 122
const val DISPLAY_MODE_GROSS: Int = 127
const val DISPLAY_MODE_NET: Int = 128
const val DISPLAY_MODE_POINT_QUOTA: Int = 129
const val DISPLAY_MODE_9_GAME: Int = 130
const val DISPLAY_MODE_STABLEFORD: Int = 131

const val FRONT_NINE_IS_DISPLAYED = 0      //zero base, user is one the 18 nine hole
const val BACK_NINE_IS_DISPLAYED = 1      //zero base, user is one the 18 nine hole
const val DISPLAY_MODE_X_6_6: Int = 2   // first siz holes
const val DISPLAY_MODE_6_X_6: Int = 3   // Second 6 holes
const val DISPLAY_MODE_6_6_X: Int = 4   // Third 6 holes

const val NINE_PLAYERS = 3
const val PLAYER_1_INX: Int = 0
const val PLAYER_2_INX: Int = 1
const val PLAYER_3_INX: Int = 2

// Vin hole played is a flag to high light the hole being played
fun getDisplayScoreCardHeaderColor(vinTag: Int): Long {
    var headerColor: Long = VIN_LIGHT_GRAY

    when (vinTag) {
        HDCP_HEADER -> headerColor = DISPLAY_NOTE_ON_HOLE
        PAR_HEADER -> headerColor = VIN_LIGHT_GRAY
        HOLE_HEADER -> headerColor = DISPLAY_HOLE_NUMBER  // current hole being play
    }

    return (headerColor)
}
