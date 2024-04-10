package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore

import androidx.compose.runtime.MutableState


sealed class DialogAction {
    data class Number(val score: Int):DialogAction()
    data class Net(val playerIdx: Int):DialogAction()
    data class NetLongClick(val playerIdx: Int):DialogAction()
    data class Gross(val playerIdx: Int):DialogAction()
    data class GrossLongClick(val playerIdx: Int):DialogAction()
    data class SetDialogCurrentPlayer(val currentPlayerIdx:Int):DialogAction()
    object Clear:DialogAction()
    object Finished:DialogAction()
    object ButtonEnterScore:DialogAction()

}