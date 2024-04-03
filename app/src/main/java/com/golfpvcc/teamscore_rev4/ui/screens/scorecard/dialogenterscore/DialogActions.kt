package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.dialogenterscore


sealed class DialogAction {
    data class Number(val score: Int):DialogAction()
    data class Net(val playerIdx: Int):DialogAction()
    data class Gross(val playerIdx: Int):DialogAction()
    object Clear:DialogAction()
    object Finished:DialogAction()
    object ButtonEnterScore:DialogAction()

}