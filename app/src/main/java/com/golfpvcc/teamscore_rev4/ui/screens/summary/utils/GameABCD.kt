package com.golfpvcc.teamscore_rev4.ui.screens.summary.utils

import com.golfpvcc.teamscore_rev4.utils.MAX_PLAYERS

class GameABCD(totalPlayers: Int) {
    val playersCount: Int = totalPlayers
    var ABCDGame: IntArray = IntArray(totalPlayers) { 0 }   // A player index 0
    var ABCDGameSorted: IntArray = IntArray(totalPlayers) { 0 }

    fun clear() {
        for (x in 0..<playersCount) {
            ABCDGame[x] = 0
        }
    }

    fun addPlayer(playerIdx: Int, score: Int) {
        ABCDGame[playerIdx] = score
    }

    fun sortScores() {
        ABCDGameSorted = ABCDGame.sortedArray()
    }

    fun getPlayerScore(idx: Int): Int {
        return (ABCDGameSorted[idx])
    }
}