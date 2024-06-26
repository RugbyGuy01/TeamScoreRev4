package com.golfpvcc.teamscore_rev4.ui.screens.summary

import android.content.Context

sealed class SummaryActions {       // functions are located in summary view model file
    data object DisplayJunkDialog : SummaryActions()
    data object DisplayPointsDialog : SummaryActions()
    data object ShowEmailDialog : SummaryActions()
    data object SaveEmailRecord : SummaryActions()
    data object SavePointsDialog : SummaryActions()
    data object CancelPointsDialog : SummaryActions()
    data object DisplayBackupRestoreDialog : SummaryActions()
    data object DisplayAboutDialog : SummaryActions()
    data class SendEmailToUser(val playerIdx:Int, val context: Context): SummaryActions()
}