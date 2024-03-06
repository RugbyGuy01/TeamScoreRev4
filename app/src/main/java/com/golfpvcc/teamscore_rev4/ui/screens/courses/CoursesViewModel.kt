package com.golfpvcc.teamscore_rev4.ui.screens.courses

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.golfpvcc.teamscore_rev4.TeamScoreCardApp.Companion.getCourseDao
import com.golfpvcc.teamscore_rev4.database.dao.CourseDao
import com.golfpvcc.teamscore_rev4.database.model.CourseRecord
import com.golfpvcc.teamscore_rev4.repository.CourseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoursesViewModel(
    private val courseDao: CourseDao    // = Graph.courseRepository
) : ViewModel() {

    var courses: LiveData<List<CourseRecord>> = courseDao.getAllCoursesRecordAsc()


    fun deleteCourse(courseRecord: CourseRecord) {
        viewModelScope.launch {
            courseDao.deleteCourseRecord(courseRecord)
        }
    }

    fun addOrUpdateCourse(courseName: String, par: IntArray, handicap: IntArray) {
        val courseRecord = CourseRecord(courseName, "NC", par, handicap, 0)
        viewModelScope.launch {
            courseDao.deleteCourseRecord(courseRecord)
        }
    }

    suspend fun getCourseById(courseId: Int): CourseRecord {
        return courseDao.getCourseRecord(courseId)
    }

}


class PlayerSetupViewModelFactor(
    private val courseRepo: CourseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CoursesViewModel(
            courseDao = getCourseDao()
        ) as T
    }
}