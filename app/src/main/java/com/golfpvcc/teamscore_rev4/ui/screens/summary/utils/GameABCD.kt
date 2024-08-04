package com.golfpvcc.teamscore_rev4.ui.screens.summary.utils

import com.golfpvcc.teamscore_rev4.utils.MAX_PLAYERS

class GameABCD {
    var ABCDGame: IntArray = IntArray(MAX_PLAYERS) { 0 }   // A player index 0
    var ABCDGameSorted:IntArray = IntArray(MAX_PLAYERS) { 0 }

    fun clear() {
        for (x in 0..<MAX_PLAYERS) {
            ABCDGame[x] = 0
        }
    }

    fun addPlayer(playerIdx:Int, score: Int) {
        ABCDGame[playerIdx] = score
    }

    fun sortScores() {
        ABCDGameSorted = ABCDGame.sortedArray()
    }

    fun getPlayerScore(idx: Int): Int {
        return (ABCDGameSorted[idx])
    }
}