package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore


sealed class DialogAction {
    data class Number(val score: Int) : DialogAction()
    data class Net(val playerIdx: Int) : DialogAction()
    data class NetLongClick(val playerIdx: Int) : DialogAction()
    data class Gross(val playerIdx: Int) : DialogAction()
    data class GrossLongClick(val playerIdx: Int) : DialogAction()


    data class DisplayJunkDialog(val playerIdx: Int) : DialogAction()
    data class ToggleJunkListItem(val listIdx: Int) : DialogAction()
    data object CloseJunkTableList : DialogAction()

    data class SetDialogCurrentPlayer(val currentPlayerIdx: Int) : DialogAction()
    data object DisplayHoleNote : DialogAction()
    data object Clear : DialogAction()
    data object Done : DialogAction()
    data object ButtonEnterScore : DialogAction()
}
