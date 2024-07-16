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
import com.golfpvcc.teamscore_rev4.utils.BACK_NINE_TOTAL_DISPLAYED
import com.golfpvcc.teamscore_rev4.utils.Constants
import com.golfpvcc.teamscore_rev4.utils.DISPLAY_SCORE_CARD_SCREEN
import com.golfpvcc.teamscore_rev4.utils.MINIMUM_LEN_OF_PLAYER_NAME
import com.golfpvcc.teamscore_rev4.utils.SCORE_CARD_REC_ID
import com.golfpvcc.teamscore_rev4.utils.USER_CANCEL
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

    suspend fun getCourseById(courseId: Int?) {
        val courseRecord: CourseRecord = courseDao.getCourseRecord(courseId)

        updateCourseRecord(courseRecord)
        val scoreCardRecord: ScoreCardRecord

        val checkScoreCardRecord = scoreCardDao.isRowIsExist(SCORE_CARD_REC_ID)
        if (checkScoreCardRecord) {
            scoreCardRecord = scoreCardDao.getScoreCardRecord(SCORE_CARD_REC_ID)
            state = state.copy(mTee = scoreCardRecord.mTee)
            state =
                if (scoreCardRecord.mCurrentHole == BACK_NINE_TOTAL_DISPLAYED)   // user finished round
                    state.copy(mStartingHole = "1")  // User can change this hole number
                else
                    state.copy(mStartingHole = (scoreCardRecord.mCurrentHole + 1).toString())  // User can change this hole number
        } else {
            Log.d("VIN", "scoreCardDao - record not found")
            vinScoreCardRecordUpdate()
        }
        Log.d("VIN", "read record  Tee ${state.mTee}  scoreCardRecord.mTee ")
        val playerRecords: List<PlayerRecord> = playerDao.getAllPlayerRecords()
        updatePlayers(playerRecords)

    }

    private fun updateCourseRecord(courseRec: CourseRecord) {
        if (courseRec != null) {
            state = state.copy(mCourseName = courseRec.mCoursename)
            state = state.copy(mPar = courseRec.mPar)
            state = state.copy(mHandicap = courseRec.mHandicap)
            state = state.copy(mCourseId = courseRec.mId)
        } else {
            state = state.copy(mCourseName = "No course record")
        }
    }

    suspend fun vinScoreCardRecordUpdate() {
        if (state.mStartingHole.isEmpty())
            state = state.copy(mStartingHole = "1")

        val scoreCardRecord: ScoreCardRecord = ScoreCardRecord(
            mCourseName = state.mCourseName,
            mTee = state.mTee,
            mCourseId = state.mCourseId,
            mCurrentHole = state.mStartingHole.toInt() - 1, // zero based
            mPar = state.mPar,
            mHandicap = state.mHandicap,
            mScoreCardRecId = state.scoreCardRecId
        )
        scoreCardDao.addUpdateScoreCardRecord(scoreCardRecord)
    }

    fun updatePlayers(playerRecords: List<PlayerRecord>) {

        for ((idx, playerRecord) in playerRecords.withIndex()) {
            Log.d("VIN", "playerRecord $idx score ${playerRecord.mScore}")
            state.mPlayerRecords[idx] = playerRecord
            Log.d("VIN", "updatePlayers $idx score ${state.mPlayerRecords[idx].mScore}")
        }
    }

    fun onTeeStateChange(tee: String) {
        Log.d("VIN", "onTeeStateChange  Name ${state.mTee} Tee string $tee ")
        state = state.copy(mTee = tee)
    }

    fun onStartingHoleChange(startingHole: String) {
        state = if (startingHole.isNotEmpty()) {
            val validateStartingHole: Int = startingHole.toInt()
            if (validateStartingHole in 1..18)
                state.copy(mStartingHole = startingHole)
            else
                state.copy(mStartingHole = "1")
        } else
            state.copy(mStartingHole = "")

        Log.d(
            "VIN",
            "onStartingHoleChange  Name ${state.mStartingHole} start ingHole $startingHole "
        )
    }


    fun saveScoreCardRecord() {
        if (state.mStartingHole.isEmpty())
            state = state.copy(mStartingHole = "1")

        val scoreCardRecord: ScoreCardRecord = ScoreCardRecord(
            mCourseName = state.mCourseName,
            mTee = state.mTee,
            mCourseId = state.mCourseId,
            mCurrentHole = state.mStartingHole.toInt() - 1,  // the value is 1 make it zero base
            mPar = state.mPar,
            mHandicap = state.mHandicap,
            mScoreCardRecId = state.scoreCardRecId
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
        viewModelScope.launch(Dispatchers.IO) {
            deleteAllPlayerRecords()
            savePlayersRecord()
            saveScoreCardRecord()
            state = state.copy(mNextScreen = DISPLAY_SCORE_CARD_SCREEN)
        }
        return 0
    }

    fun onButtonCancel() {
        state = state.copy(mNextScreen = USER_CANCEL)
    }

    fun onButtonUpdate(): Int {
        viewModelScope.launch {
            savePlayersRecord()
            saveScoreCardRecord()
            state = state.copy(mNextScreen = DISPLAY_SCORE_CARD_SCREEN)
        }
        return 0
    }

    private fun deleteAllPlayerRecords() {
        playerDao.deleteAllPlayersRecord()
        val zeroArray: IntArray = IntArray(18) { 0 }
        state = state.copy(mStartingHole = "1")
        for (player in state.mPlayerRecords) {
            zeroArray.copyInto(player.mScore)
            zeroArray.copyInto(player.mTeamHole)
        }
    }

    suspend fun savePlayersRecord() {
        var count: Int = 0
        for (player in state.mPlayerRecords) {

            if (MINIMUM_LEN_OF_PLAYER_NAME < player.mName.length) {
                if (player.mHandicap.length < 1) {
                    player.mHandicap = "0"
                }

                val playerRecord: PlayerRecord = PlayerRecord(
                    player.mName,
                    player.mHandicap,
                    player.mScore,
                    player.mTeamHole,
                    SCORE_CARD_REC_ID,
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
    val mCourseName: String = "",    // current course name from the course list database
    val mTee: String = "",                   // the tee's played or the course yardage
    val mCourseId: Int = 0,      // the current course we are using for the score card
    val mCurrentHole: Int = 0,      // the current hole being played in the game
    val mStartingHole: String = "1",
    val mPar: IntArray = IntArray(18),        // the current course Par,         // the current course Par
    val mHandicap: IntArray = IntArray(18),        // the current course Par,       // current course handicap
    val mTeamHoleScore: IntArray = IntArray(18),
    val mTeamTotalScore: IntArray = IntArray(18),
    val mHoleUsedByPlayers: IntArray = IntArray(18),
    val mPlayerRecords: Array<PlayerRecord> = Array<PlayerRecord>(4) { PlayerRecord() },
    val scoreCardRecId: Int = SCORE_CARD_REC_ID,    // score card record ID
    val observer: Boolean = false,
    val mNextScreen: Int = 0,
)

