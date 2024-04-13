package com.golfpvcc.teamscore_rev4.ui.screens.scorecard

import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore.DialogAction

sealed class ScoreCardActions {
    data object Prev: ScoreCardActions()
    data object Next: ScoreCardActions()
}