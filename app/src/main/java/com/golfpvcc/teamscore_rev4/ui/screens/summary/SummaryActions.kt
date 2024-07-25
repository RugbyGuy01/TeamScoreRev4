package com.golfpvcc.teamscore_rev4.ui.screens.summary

import android.content.Context

sealed class SummaryActions {       // functions are located in summary view model file
    data object DisplayJunkDialog : SummaryActions()
    data class UpdateJunkRecord(val junkRecIdx: Int) : SummaryActions()
    data object SaveJunkDialog : SummaryActions()
    data object AddRecordJunkDialog : SummaryActions()
    data object DisplayPointsDialog : SummaryActions()
    data object ShowEmailDialog : SummaryActions()
    data object SaveEmailRecord : SummaryActions()
    data object SavePointsDialog : SummaryActions()
    data object CancelPointsDialog : SummaryActions()
    data object ShowBackupRestoreDialog : SummaryActions()
    data class BackupRestoreDialog(val context: Context, val backup:Boolean) : SummaryActions()
    data object DisplayAboutDialog : SummaryActions()
    data class SendEmailToUser(val playerIdx: Int, val context: Context) : SummaryActions()
}