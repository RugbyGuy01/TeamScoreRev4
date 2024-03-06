package com.golfpvcc.teamscore_rev4.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.golfpvcc.teamscore_rev4.database.dao.CourseDao
import com.golfpvcc.teamscore_rev4.database.model.CourseRecord

class CourseRepository(private val courseDao: CourseDao,) {

    suspend fun getAllCourses() = courseDao.getAllCoursesRecordAsc()

    suspend fun addUpdateCourseRecord(courseRecord: CourseRecord) {
        courseDao.addUpdateCourseRecord(courseRecord)
    }

    suspend fun deleteCourseRecord(courseRecord: CourseRecord) {
        courseDao.deleteCourseRecord(courseRecord)
    }
}

