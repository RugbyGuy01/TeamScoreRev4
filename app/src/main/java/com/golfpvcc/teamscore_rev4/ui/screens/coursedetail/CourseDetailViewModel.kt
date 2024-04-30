package com.golfpvcc.teamscore_rev4.ui.screens.coursedetail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.golfpvcc.teamscore_rev4.utils.HoleParList
class CourseDetailViewModel (){
    var state by mutableStateOf(CourseDetailState())
        private set

    fun onCourseNameChange(newValue: String) {
        state = state.copy(mCoursename = newValue)
    }
    fun setHandicap(Handicap: IntArray) {
        state = state.copy(mHandicap = Handicap)
    }
    fun setPar(newValue: IntArray) {
        state = state.copy(mPar = newValue)
    }
    fun setCourseId(id: Int){
        state = state.copy(mId = id)
    }

    fun getFlipHdcps(): Boolean {
        return state.mFlipHdcps
    }

    fun setFlipHdcpsChange(newValue: Boolean) {
        state = state.copy(mFlipHdcps = !newValue)
    }

    //  par configuration
    fun getPopupSelectHolePar(): Int {
        return (state.isPopupSelectHolePar)
    }
    fun setPopupSelectHolePar(holeIdx: Int) {
        state = state.copy(isPopupSelectHolePar = holeIdx)
    }
    fun getHolePar(holeIdx: Int): Int {
        val newValue = state.mPar[holeIdx]
        Log.d("VIN", "getHolePar Inx$holeIdx - Par $newValue")
        return newValue
    }
    fun onParChange(holeIdx: Int, newValue: Int) {
        Log.d("VIN", "onParChange Inx$holeIdx - Par $newValue")
        state.mPar[holeIdx] = newValue
    }
    // end of par configuration
    // Configure course Handicap
    fun getPopupSelectHoleHandicap(): Int {
        return (state.isPopupSelectHoleHdcp)
    }
    fun setPopupSelectHoleHdcp(holeIdx: Int) {
        state = state.copy(isPopupSelectHoleHdcp = holeIdx)
    }
    fun getHoleHandicap(holeIdx: Int): Int {
        val newValue = state.mHandicap[holeIdx]
        Log.d("VIN", "getHoleHandicap Inx$holeIdx - Hdcp $newValue")
        return newValue
    }
    fun onHandicapChange(holeIdx: Int, newHdcp: Int) {
        Log.d("VIN", "onHandicapChange Card Hole-$holeIdx - Hdcp $newHdcp")
        state.mHandicap[holeIdx] = newHdcp
    }
    fun setHandicapAvailable() {
        currentHandicapConfiguration(state.mHandicap, state.availableHandicap)
    }
    fun checkForAvailableHandicaps(){
        val allHandicapsUsed: HoleHandicap? = state.availableHandicap.find { it.available == true }

        state = if (allHandicapsUsed == null){
            state.copy(mEnableSaveButton = true)
        } else {
            state.copy(mEnableSaveButton = false)
        }
    }
    // end of Configure course Handicap
}
data class CourseDetailState(
    val mId: Int = 0,
    val mCoursename: String = "",   // this is the database key for this course in the CourseListRecord class
    val mUsstate: String? = "",
    val mFlipHdcps: Boolean = true,
    val mHoleNumber: IntArray = IntArray(18) { i -> i + 1 },
    val mPar: IntArray = IntArray(18) { 4 },
    val mHandicap: IntArray = IntArray(18) { 0 },
    val isUpdatingCourse: Boolean = false,
    val isPopupSelectHolePar: Int = -1,
    val isPopupSelectHoleHdcp: Int = -1,
    val isDropDownDismissed: Boolean = false,
    val mEnableSaveButton:Boolean = false,
    val parList: HoleParList = HoleParList(),
    var availableHandicap: Array<HoleHandicap> = Array(18) { HoleHandicap(0, false) }
)


data class HoleHandicap(
    var holeHandicap: Int = 0,
    var available: Boolean = false,      // has the hole handicap been used?
)