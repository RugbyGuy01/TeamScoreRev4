package com.golfpvcc.teamscore_rev4.ui.screens.courses

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.golfpvcc.teamscore_rev4.TeamScoreCardApp
import com.golfpvcc.teamscore_rev4.database.dao.CourseDao
import com.golfpvcc.teamscore_rev4.database.model.CourseRecord
import com.golfpvcc.teamscore_rev4.repository.CourseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoursesViewModel(
    private val courseDao: CourseDao    // = Graph.courseRepository
) : ViewModel() {

    var courses: LiveData<List<CourseRecord>> = courseDao.getAllCoursesRecordAsc()

    @Suppress("UNCHECKED_CAST")
    class CoursesViewModelFactor() : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CoursesViewModel(TeamScoreCardApp.getCourseDao()) as T
        }
    }


    fun deleteCourse(courseRecord: CourseRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            courseDao.deleteCourseRecord(courseRecord)
        }
    }

    fun addOrUpdateCourse(courseName: String, par: IntArray, handicap: IntArray) {
        val courseRecord = CourseRecord(courseName, "NC", par, handicap)
        viewModelScope.launch(Dispatchers.IO) {
            courseDao.addUpdateCourseRecord(courseRecord)
        }
    }

    suspend fun getCourseById(courseId: Int): CourseRecord? {
        return courseDao.getCourseRecord(courseId)
    }

}


class PlayerSetupViewModelFactor(
    private val courseRepo: CourseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CoursesViewModel(
            courseDao = TeamScoreCardApp.getCourseDao()
        ) as T
    }
}