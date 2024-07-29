package com.golfpvcc.teamscore_rev4.ui.screens.courses

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.golfpvcc.teamscore_rev4.TeamScoreCardApp
import com.golfpvcc.teamscore_rev4.database.dao.CourseDao
import com.golfpvcc.teamscore_rev4.database.model.CourseRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoursesViewModel(
    private val courseDao: CourseDao    // are companion object
) : ViewModel() {

    var courses: LiveData<List<CourseRecord>> = courseDao.getAllCoursesRecordAsc()

    @Suppress("UNCHECKED_CAST")
    class CoursesViewModelFactor() : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CoursesViewModel(TeamScoreCardApp.getCourseDao()) as T
        }
    }


    fun deleteCourse(courseRecord: CourseRecord) {
        Log.d("VIN", "Deleted course ${courseRecord.mId}")
        viewModelScope.launch(Dispatchers.IO) {
            courseDao.deleteCourseRecord(courseRecord)
        }
    }

    fun addOrUpdateCourse(courseRecord: CourseRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            courseDao.addUpdateCourseRecord(courseRecord)
        }
    }

    suspend fun getCourseById(courseId: Int?): CourseRecord? {
        Log.d("VIN", "Get course id = $courseId")
        return courseDao.getCourseRecord(courseId)
    }

}


