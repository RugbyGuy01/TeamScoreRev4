package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils

import com.golfpvcc.teamscore_rev4.ui.screens.summary.NinePlayerPayout

const val HIGHEST_POINTS = 0
const val MIDDLE_POINTS = 1
const val LOWEST_POINTS = 2

/*
Sort scores  h = 5 M = 3  L = 1

Case 1	a - 3	b - 3	c - 3
Case 2	a - 3	b - 3	c - 4
Case 3	a - 3	b - 4	c - 4
Case 4	a - 3	b - 4	c - 5

if a == b {
   if a == c {
       (a = b = c = 3)		Case 1
   }
   else
   {
       a = b = 4			Case 2
       c = 1
   }
}
else
{
   a = 5
   if( b == c) {
       b = c = 2			Case 3
   }
   else
   {
       b = 3				Case 4
       c = 1
   }
}
*/
/*
    The m_Player_9_Score[][] array will act like a 1 byte record, the player array and the score array
    The m_Player_9_Score[PlayInx][ScoreInx] = (Player gross score)  The score
 */
class NineGame {
    private var playerScoreArray: Array<Player9game?> = arrayOfNulls(size = NINE_PLAYERS)

    /*
Constructor
 */
    init {
        for (x in 0..<NINE_PLAYERS) {
            playerScoreArray[x] = Player9game() // allocate the player's class for the 9 game
        }
    }

    fun clearTotals() {
        for (x in 0..<NINE_PLAYERS) {
            playerScoreArray[x]!!.clearScore()
        }
    }

    /*
    this function will sort the player score from low to high
     */
    fun sort9Scores() {
        var `in`: Int
        var inScore: Int
        var outScore: Int

        var out: Int = NINE_PLAYERS - 1
        while (out > 0) {
            // outer loop (backward)
            `in` = 0
            while (`in` < out) {
                inScore = playerScoreArray[`in`]!!.holeScore
                outScore = playerScoreArray[`in` + 1]!!.holeScore
                if (inScore > outScore) // out of order?
                    swap(`in`, `in` + 1) // swap them

                `in`++
            }
            out--
        }
        calculateScores()
    }

    /*
    This function will swap the two classes
     */
    private fun swap(one: Int, two: Int) {
        val temp = playerScoreArray[one]
        playerScoreArray[one] = playerScoreArray[two]
        playerScoreArray[two] = temp
    }

    /*
    This function will calculate the player scores based on the sort of the lowest score is at the top of the array.
     */
    private fun calculateScores() {
        if (0 < playerScoreArray[PLAYER_1_INX]!!.holeScore) {
            if (playerScoreArray[PLAYER_1_INX]!!.holeScore == playerScoreArray[PLAYER_2_INX]!!.holeScore) {
                if (playerScoreArray[PLAYER_1_INX]!!.holeScore == playerScoreArray[PLAYER_3_INX]!!.holeScore) {
                    playerScoreArray[PLAYER_1_INX]!!.updatePlayerNineScore(3) //all three player have the same score
                    playerScoreArray[PLAYER_2_INX]!!.updatePlayerNineScore(3)
                    playerScoreArray[PLAYER_3_INX]!!.updatePlayerNineScore(3)
                } else {
                    playerScoreArray[PLAYER_1_INX]!!.updatePlayerNineScore(4) // top 2 players have the same score
                    playerScoreArray[PLAYER_2_INX]!!.updatePlayerNineScore(4)
                    playerScoreArray[PLAYER_3_INX]!!.updatePlayerNineScore(1)
                }
            } else {
                playerScoreArray[PLAYER_1_INX]!!.updatePlayerNineScore(5) // lowest score of the three players
                if (playerScoreArray[PLAYER_2_INX]!!.holeScore == playerScoreArray[PLAYER_3_INX]!!.holeScore) {
                    playerScoreArray[PLAYER_2_INX]!!.updatePlayerNineScore(2) // bottom 2 players have the save score
                    playerScoreArray[PLAYER_3_INX]!!.updatePlayerNineScore(2)
                } else {
                    playerScoreArray[PLAYER_2_INX]!!.updatePlayerNineScore(3)
                    playerScoreArray[PLAYER_3_INX]!!.updatePlayerNineScore(1)
                }
            }
        }
        // else hole not scored yet
    }

    fun get9GameScore(inx: Int): Int { // the index is the player
        var score = 0

        for (x in 0..<NINE_PLAYERS) {
            if (playerScoreArray[x]!!.playerInx == inx) { // playerScoreArray calculated the player's 9 score
                score = playerScoreArray[x]!!.playerNineScore
            }
        }

        return score
    }

    /*
    Set the player's hole score the 9's game. Only 3 player can play
     */
    fun addPlayerGrossScore(inx: Int, netScore: Int) {
        playerScoreArray[inx]!!.setPlayerHoleScore(inx, netScore)
    }

    inner class Player9game {
        var playerInx: Int = 0 // The player's index from the score card - First player is index 0
        var holeScore: Int = 0 // the player's gross score for the current hole
        var playerNineScore: Int = 0 // the calculate score for the 9 game.

        init {
            clearScore()
        }

        fun clearScore() {
            playerInx = 0
            holeScore = 0
            playerNineScore = 0
        }

        fun setPlayerHoleScore(idx: Int, netScore: Int) {
            this.holeScore = netScore
            playerInx = idx
        }

        fun updatePlayerNineScore(nineScore: Int) {
            this.playerNineScore = nineScore
        }
    }
}

class NineGamePayout() {
    var playerNineArray = arrayOf(NinePlayerPayout(), NinePlayerPayout(), NinePlayerPayout())

    init {
        for (x in 0..<NINE_PLAYERS) {
            playerNineArray[x] = NinePlayerPayout() // allocate the player's class for the 9 game
        }
    }

    fun addPlayerNinePoints(
        playerInx: Int,
        playerName: String,
        points: Int,
    ) { // add each players points to the internal array
        playerNineArray[playerInx].mPoints = points
        playerNineArray[playerInx].mPlayerInx = playerInx
        playerNineArray[playerInx].mName = playerName // player name is the index
    }

    fun sort9Points() { // sort the internal array with the highest points in index 0 and the lowest points in index 2
        var inLoop: Int
        var inPoints: Int
        var outPoints: Int

        var outLoop: Int = NINE_PLAYERS - 1
        while (outLoop > 0) {
            // outer loop (backward)
            inLoop = 0
            while (inLoop < outLoop) {
                inPoints = playerNineArray[inLoop].mPoints
                outPoints = playerNineArray[inLoop + 1].mPoints
                if (inPoints < outPoints) // out of order?
                    swap(inLoop, inLoop + 1) // swap them

                inLoop++
            }
            outLoop--
        }
    }

    fun get9GamePayOut(inx: Int): NinePlayerPayout { // the index is the player
        var playerPayout = NinePlayerPayout()

        for (x in 0..<NINE_PLAYERS) {
            if (playerNineArray[x].mPlayerInx == inx) { // playerScoreArray calculated the player's 9 score
                playerPayout = playerNineArray[x]
            }
        }

        return playerPayout
    }

    /*      This function will swap the two classes       */
    private fun swap(one: Int, two: Int) {
        val temp = playerNineArray[one]
        playerNineArray[one] = playerNineArray[two]
        playerNineArray[two] = temp
    }

    /*
    The HIGHEST_POINTS player owes nothing to any player
    The MIDDLE_POINTS player only owes the HIGHEST_POINTS player
    The LOWEST_POINTS player owes HIGHEST_POINTS player and MIDDLE_POINTS player
    Now the rub - if the LOWEST_POINTS player owes MIDDLE_POINTS player and MIDDLE_POINTS player owes HIGHEST_POINTS player,
        do the following
        substrate what the LOWEST_POINTS player owes MIDDLE_POINTS player from what the MIDDLE_POINTS player owes
            the HIGHEST_POINTS player and add that amount to what LOWEST_POINTS player owes the HIGHEST_POINTS player
    player 0 Player owes  2 30 // owes 20 to 3 and owes 10 to 2
    player 0 Player owes  1 10

    player 1 Player owes  2 0 // owes 10 to 3
    player 1 Player owes  0 0

    player 2 Player owes  0 0
    player 2 Player owes  0 0
     */
    fun calculateWhoOwesWho() {
        // highest point player get money from the second place player
        if (playerNineArray[HIGHEST_POINTS].mPoints != playerNineArray[MIDDLE_POINTS].mPoints) {
            playerNineArray[MIDDLE_POINTS].mPayFirstInx =
                playerNineArray[HIGHEST_POINTS].mPlayerInx // we owe the highest point player some money
            playerNineArray[MIDDLE_POINTS].mFirstName = playerNineArray[HIGHEST_POINTS].mName
            playerNineArray[MIDDLE_POINTS].mPayFirstAmount =
                playerNineArray[HIGHEST_POINTS].mPoints - playerNineArray[MIDDLE_POINTS].mPoints
        }
        // highest point player get money from the third place player
        if (playerNineArray[HIGHEST_POINTS].mPoints != playerNineArray[LOWEST_POINTS].mPoints) {
            playerNineArray[LOWEST_POINTS].mPayFirstInx =
                playerNineArray[HIGHEST_POINTS].mPlayerInx // we owe the highest point player some money
            playerNineArray[LOWEST_POINTS].mFirstName =
                playerNineArray[HIGHEST_POINTS].mName // we owe the highest point player some money
            playerNineArray[LOWEST_POINTS].mPayFirstAmount =
                playerNineArray[HIGHEST_POINTS].mPoints - playerNineArray[LOWEST_POINTS].mPoints
        }
        // Second point player get money from the third place player
        if (playerNineArray[MIDDLE_POINTS].mPoints != playerNineArray[LOWEST_POINTS].mPoints) {
            playerNineArray[LOWEST_POINTS].mPaySecondInx =
                playerNineArray[MIDDLE_POINTS].mPlayerInx // we owe the highest point player some money
            playerNineArray[LOWEST_POINTS].mSecondName =
                playerNineArray[MIDDLE_POINTS].mName // we owe the highest point player some money
            playerNineArray[LOWEST_POINTS].mPaySecondAmount =
                playerNineArray[MIDDLE_POINTS].mPoints - playerNineArray[LOWEST_POINTS].mPoints
        }
        // check how much credit the second place player can use for paying the first place player with the money owed by lowest place player
        if (playerNineArray[LOWEST_POINTS].mPaySecondAmount != 0) { // does the LOWEST_POINTS player owes MIDDLE_POINTS player
            if (0 != playerNineArray[MIDDLE_POINTS].mPayFirstAmount) // does MIDDLE_POINTS player owes the HIGHEST_POINTS player
            {   // MIDDLE_POINTS guy owes more than LOWEST_POINTS owes to the MIDDLE_POINTS guy
                if (playerNineArray[MIDDLE_POINTS].mPayFirstAmount >= playerNineArray[LOWEST_POINTS].mPaySecondAmount) {
                    playerNineArray[MIDDLE_POINTS].mPayFirstAmount -= playerNineArray[LOWEST_POINTS].mPaySecondAmount
                    playerNineArray[LOWEST_POINTS].mPayFirstAmount += playerNineArray[LOWEST_POINTS].mPaySecondAmount
                    playerNineArray[LOWEST_POINTS].mPaySecondAmount = 0
                } else {
                    playerNineArray[LOWEST_POINTS].mPayFirstAmount += playerNineArray[MIDDLE_POINTS].mPayFirstAmount
                    playerNineArray[LOWEST_POINTS].mPaySecondAmount -= playerNineArray[MIDDLE_POINTS].mPayFirstAmount
                    playerNineArray[MIDDLE_POINTS].mPayFirstAmount = 0
                }
            }
        }
    }
}