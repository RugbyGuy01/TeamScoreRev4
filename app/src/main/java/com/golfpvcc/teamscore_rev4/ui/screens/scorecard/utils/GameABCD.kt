package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils

class GameABCD {
    var mABCDGame = mutableListOf<Int>()

    init {
        mABCDGame = emptyList<Int>().toMutableList()
    }
    fun clear() {
        mABCDGame.distinct()
    }

    fun addPlayer( score: Int) {
        mABCDGame += score
    }

    fun sortScores() {
        mABCDGame.sort()
    }

    fun getPlayerScore(idx: Int): Int {
        return (mABCDGame[idx])
    }
}