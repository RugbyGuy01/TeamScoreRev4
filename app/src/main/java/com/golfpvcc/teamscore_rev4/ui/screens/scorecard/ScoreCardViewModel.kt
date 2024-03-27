package com.golfpvcc.teamscore_rev4.ui.screens.scorecard

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ScoreCardViewModel(

) : ViewModel() {
    var state by mutableStateOf(ScoreCard())
        private set

    class ScoreCardViewModelFactor() : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ScoreCardViewModel() as T
        }
    }


    fun SetHoleScore(playerIdx: Int, idx: Int, score: Int) {
        Log.d("VIN", "SetHoleScore $idx  Score $score")
        state.rowPlayerCells[playerIdx].mHole[idx] = score
        Log.d("VIN", "SetHoleScore ${state.rowPlayerCells[playerIdx].mHole[idx]}")
        state = state.copy(mRepaintScreen = true)
    }

}

data class ScoreCard(
    val mRepaintScreen: Boolean = false,
    val mName: List<String> = emptyList(),
    val rowHeadings: List<RowHeading> = listOf(
        RowHeading("HdCp"),
        RowHeading("Par"),
        RowHeading("Hole"),
    ),
    val rowHeaderCells: List<scoreCardCell> = listOf(
        scoreCardCell(IntArray(18) { i -> i + 1 }), // Handicap
        scoreCardCell(IntArray(18) { 4 }),          // Par
        scoreCardCell(IntArray(18) { i -> i + 1 }), // hole
    ),
    val rowHeaderTotals: List<RowHeading> = listOf(
        RowHeading(""),         // Handicap - blank
        RowHeading("36"),       // determine by the score card record
        RowHeading("Total"),
    ),

    val rowPlayerNames: List<RowHeading> = listOf(
        RowHeading("Ali"),
        RowHeading("Murray"),
        RowHeading("Dennis"),
        RowHeading("Vinnie"),
    ),
    val rowPlayerCells: List<scoreCardCell> = listOf(
        scoreCardCell(IntArray(18) { 5 }),
        scoreCardCell(IntArray(18) { 3 }),
        scoreCardCell(IntArray(18) { 5 }),
        scoreCardCell(IntArray(18) { 3 })
    ),
    val rowPlayerTotals: List<RowHeading> = listOf(
        // determine by the score card record
        RowHeading("40"),
        RowHeading("45"),
        RowHeading("36"),
        RowHeading("43"),
    ),

    val rowTeams: List<RowHeading> = listOf(
        RowHeading("Team"),
        RowHeading("Used"),
    ),

    val rowTeamCells: List<scoreCardCell> = listOf(
        scoreCardCell(IntArray(18) { 5 }),
        scoreCardCell(IntArray(18) { 3 }),
    ),
    val rowTeamTotals: List<RowHeading> = listOf(
        // determine by the score card record
        RowHeading("40"),
        RowHeading("45"),
    )
)

data class RowHeading(
    val mName: String = ""
)

data class scoreCardCell(
    val mHole: IntArray = IntArray(18),
)