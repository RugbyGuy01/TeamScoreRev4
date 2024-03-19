package com.golfpvcc.teamscore_rev4.ui.screens.playersetup

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.golfpvcc.teamscore_rev4.TeamScoreCardApp
import com.golfpvcc.teamscore_rev4.database.model.CourseRecord

import com.golfpvcc.teamscore_rev4.database.model.PlayerRecord
import com.golfpvcc.teamscore_rev4.database.model.ScoreCardRecord
import com.golfpvcc.teamscore_rev4.utils.Constants
import com.golfpvcc.teamscore_rev4.utils.Constants.DISPLAY_SCORE_CARD_SCREEN
import com.golfpvcc.teamscore_rev4.utils.Constants.MINIMUM_LEN_OF_PLAYER_NAME
import com.golfpvcc.teamscore_rev4.utils.Constants.SCORE_CARD_REC_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PlayerSetupViewModel(
    private val courseId: Int?,
) : ViewModel() {
    private val playerDao = TeamScoreCardApp.getPlayerDao()
    private val courseDao = TeamScoreCardApp.getCourseDao()
    private val scoreCardDao = TeamScoreCardApp.getScoreCardDao()

    var state by mutableStateOf(ScoreCardState())
        private set

    @Suppress("UNCHECKED_CAST")
    class PlayerSetupViewModelFactor(private val id: Int?) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlayerSetupViewModel(
                courseId = id,
            ) as T
        }
    }

    init {

    }

    suspend fun getCourseById(courseId: Int?) {
        val courseRecord: CourseRecord = courseDao.getCourseRecord(courseId)
        updateCourseRecord(courseRecord)
        saveScoreCardRecord()       // make sure we have a record
        val scoreCardRecord: ScoreCardRecord = scoreCardDao.getScoreCardRecord(SCORE_CARD_REC_ID)
        updateScoreCard(scoreCardRecord)
        val playerRecord :List<PlayerRecord> = playerDao.getAllPlayerRecords()

        updatePlayers()
        Log.d("VIN", "Get course id = $courseId")
    }

    private fun updateCourseRecord(courseRec: CourseRecord) {
        state = state.copy(mCourseName = courseRec.mCoursename)
        state = state.copy(mPar = courseRec.mPar)
        state = state.copy(mHandicap = courseRec.mHandicap)
    }

    private fun updateScoreCard(scoreCard: ScoreCardRecord) {
        state = state.copy(mTee = scoreCard.mTee)
        state = state.copy(mCurrentHole = scoreCard.mCurrentHole)
        saveScoreCardRecord()
    }

    fun updatePlayers() {

    }

    fun onTeeStateChange(tee: String) {
        Log.d("VIN", "onTeeStateChange  Name ${state.mTee} Tee string $tee ")
        state = state.copy(mTee = tee)
    }

    fun saveScoreCardRecord() {
        val scoreCardRecord: ScoreCardRecord = ScoreCardRecord(
            mCourseName = state.mCourseName,
            mTee = state.mTee,
            mCurrentHole = state.mCurrentHole,
            mPar = state.mPar,
            mHandicap = state.mHandicap,
            scoreCardRec_Id = state.scoreCardRecId
        )
        viewModelScope.launch {
            scoreCardDao.addUpdateScoreCardRecord(scoreCardRecord)
        }
    }

    fun onPlayerNameChange(idx: Int, newValue: String) {
        state.mPlayerRecords[idx] = state.mPlayerRecords[idx].copy(mName = newValue)
        Log.d("VIN", "onPlayerNameChange $idx, Name ${state.mPlayerRecords[idx].mName} ")
        state = state.copy(observer = !state.observer)
    }

    fun onPlayerHandicapChange(idx: Int, newValue: String) {
        state.mPlayerRecords[idx] = state.mPlayerRecords[idx].copy(mHandicap = newValue)
        Log.d("VIN", "onPlayerHandicapChange handicap ${state.mPlayerRecords[idx].mHandicap} ")
        state = state.copy(observer = !state.observer)
    }

    fun onButtonNewGame(): Int {
        viewModelScope.launch {
            //           deleteAllPlayerRecords()
            savePlayersRecord()
            saveScoreCardRecord()
            state = state.copy(mNextScreen = DISPLAY_SCORE_CARD_SCREEN)
        }
        return 0
    }

    fun onButtonCancel(): Int {
        return Constants.USER_CANCEL
    }

    fun onButtonUpdate(): Int {
        viewModelScope.launch {
            savePlayersRecord()
            saveScoreCardRecord()
            state = state.copy(mNextScreen = DISPLAY_SCORE_CARD_SCREEN)
        }
        return 0
    }

    suspend fun savePlayersRecord() {
        var count: Int = 0
        for (player in state.mPlayerRecords) {
            if (MINIMUM_LEN_OF_PLAYER_NAME < player.mName.length) {
                val playerRecord: PlayerRecord = PlayerRecord(
                    player.mName,
                    player.mHandicap,
                    IntArray(18) { 0 },
                    Constants.SCORE_CARD_REC_ID,
                    mId = count
                )
                playerDao.addUpdatePlayerRecord(playerRecord)
                count++
            }
            Log.d("VIN", "savePlayersRecord ${player.mName}")
        }

    }

}

data class ScoreCardState(
    val nextScreen: Int = 0,
    val mCourseName: String = "",    // current course name from the course list dtabase
    val mTee: String = "",                   // the tee's played or the course yardage
    val mCurrentHole: Int = 0,      // the current hole being played in the game
    val mPar: IntArray = IntArray(18),        // the current course Par,         // the current course Par
    val mHandicap: IntArray = IntArray(18),        // the current course Par,       // current course handicap
    val mTeamHoleScore: IntArray = IntArray(18),
    val mTeamTotalScore: IntArray = IntArray(18),
    val mHoleUsedByPlayers: IntArray = IntArray(18),
    val mPlayerRecords: Array<PlayerRecord> = Array<PlayerRecord>(4) { PlayerRecord() },
    val scoreCardRecId: Int = SCORE_CARD_REC_ID,    // score card record ID
    val observer: Boolean = false,
    val readDatabase: Boolean = true,
    val mNextScreen: Int = 0,
)

