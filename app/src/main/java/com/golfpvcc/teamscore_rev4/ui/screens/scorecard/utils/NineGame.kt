package com.golfpvcc.teamscore_rev4.ui.screens.scorecard.utils



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
    var playerScoreArray: Array<Player9game?>

    /*
Constructor
 */
    init {
        playerScoreArray = arrayOfNulls<Player9game>(size = NINE_PLAYERS)
        for (x in 0..<NINE_PLAYERS) {
            playerScoreArray[x] = Player9game() // allocate the player's class for the 9 game
        }
    }

    fun ClearTotals() {
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
        var OutScore: Int

        var out: Int = NINE_PLAYERS - 1
        while (out > 0) {
            // outer loop (backward)
            `in` = 0
            while (`in` < out) {
                inScore = playerScoreArray[`in`]!!.holeScore
                OutScore = playerScoreArray[`in` + 1]!!.holeScore
                if (inScore > OutScore) // out of order?
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
                    playerScoreArray[PLAYER_1_INX]!!.updatePlayerNineScore(3)
                    playerScoreArray[PLAYER_2_INX]!!.updatePlayerNineScore(3)
                    playerScoreArray[PLAYER_3_INX]!!.updatePlayerNineScore(3)
                } else {
                    playerScoreArray[PLAYER_1_INX]!!.updatePlayerNineScore(4)
                    playerScoreArray[PLAYER_2_INX]!!.updatePlayerNineScore(4)
                    playerScoreArray[PLAYER_3_INX]!!.updatePlayerNineScore(1)
                }
            } else {
                playerScoreArray[PLAYER_1_INX]!!.updatePlayerNineScore(5) // lowest score of the three players
                if (playerScoreArray[PLAYER_2_INX]!!.holeScore == playerScoreArray[PLAYER_3_INX]!!.holeScore) {
                    playerScoreArray[PLAYER_2_INX]!!.updatePlayerNineScore(2)
                    playerScoreArray[PLAYER_3_INX]!!.updatePlayerNineScore(2)
                } else {
                    playerScoreArray[PLAYER_2_INX]!!.updatePlayerNineScore(3)
                    playerScoreArray[PLAYER_3_INX]!!.updatePlayerNineScore(1)
                }
            }
        }
        // else hole not scored yet
    }

    fun get9GameScore(inx: Int): Int {
        var Score = 0

        for (x in 0..<NINE_PLAYERS) {
            if (playerScoreArray[x]!!.playerInx == inx) {
                Score = playerScoreArray[x]!!.playerNineScore
            }
        }

        return Score
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