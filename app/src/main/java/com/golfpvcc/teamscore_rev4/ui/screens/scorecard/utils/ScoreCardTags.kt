package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils

import com.golfpvcc.teamscore_rev4.utils.PLAYER_STROKES_0
import com.golfpvcc.teamscore_rev4.utils.PLAYER_STROKES_1
import com.golfpvcc.teamscore_rev4.utils.PLAYER_STROKES_2
import com.golfpvcc.teamscore_rev4.utils.PLAYER_STROKES_3


const val HDCP_HEADER: Int = 100
const val PAR_HEADER: Int = 101
const val HOLE_HEADER: Int = 102
const val TEAM_HEADER: Int = 121
const val USED_HEADER: Int = 122
const val TEAM_CELL: Int = 123
const val USED_CELL: Int = 124
const val TEAM_TOTAL: Int = 125
const val USED_TOTAL: Int = 126
const val DISPLAY_MODE_GROSS: Int = 127
const val DISPLAY_MODE_NET: Int = 128
const val DISPLAY_MODE_POINT_QUOTA: Int = 129
const val DISPLAY_MODE_9_GAME: Int = 130
const val DISPLAY_MODE_STABLEFORD: Int = 131


fun changeDisplayScreenMode(currentScreenMode: Int): Int {
    var newScreenMode: Int = DISPLAY_MODE_GROSS

    when (currentScreenMode) {
        DISPLAY_MODE_GROSS -> {
            newScreenMode = DISPLAY_MODE_NET
        }

        DISPLAY_MODE_NET -> {
            newScreenMode = DISPLAY_MODE_POINT_QUOTA
        }

        DISPLAY_MODE_POINT_QUOTA -> {
            newScreenMode = DISPLAY_MODE_STABLEFORD
        }

        DISPLAY_MODE_9_GAME -> {
            newScreenMode = DISPLAY_MODE_GROSS
        }

        DISPLAY_MODE_STABLEFORD -> {
            newScreenMode = DISPLAY_MODE_GROSS
        }
    }
    return (newScreenMode)
}

fun screenModeText(currentScreenMode: Int): String {
    var newScreenMode: String = "Gross"
    when (currentScreenMode) {
        DISPLAY_MODE_GROSS -> {
            newScreenMode = "Net"
        }

        DISPLAY_MODE_NET -> {
            newScreenMode = "Pt Quote"
        }

        DISPLAY_MODE_POINT_QUOTA -> {
            newScreenMode = "Stableford"
        }

        DISPLAY_MODE_9_GAME -> {
            newScreenMode = "Gross"
        }

        DISPLAY_MODE_STABLEFORD -> {
            newScreenMode = "Gross"
        }
    }
    return (newScreenMode)
}
fun getHoleStrokeMask(strokeForHole: Int): Int {
    var hHoleStrokeMask: Int = PLAYER_STROKES_0

    when (strokeForHole) {
        0 -> {
            hHoleStrokeMask = PLAYER_STROKES_0
        }

        1 -> {
            hHoleStrokeMask = PLAYER_STROKES_1
        }

        2 -> {
            hHoleStrokeMask = PLAYER_STROKES_2
        }

        3 -> {
            hHoleStrokeMask = PLAYER_STROKES_3
        }

    }
    return (hHoleStrokeMask)
}