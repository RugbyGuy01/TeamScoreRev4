package com.golfpvcc.teamscore_rev4.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.golfpvcc.teamscore_rev4.database.model.CourseRecord

@Dao
interface CourseDao {
    @Query("SELECT * FROM CourseTable WHERE mId = :courseId ")
     fun getCourseRecord(courseId: Int?): CourseRecord

    @Query("Select * FROM CourseTable ORDER BY mCoursename ASC")
    fun getAllCoursesRecordAsc(): LiveData<List<CourseRecord>>

    @Delete
     fun deleteCourseRecord(courseRecord: CourseRecord)

    @Upsert
    fun addUpdateCourseRecord(courseRecord: CourseRecord)      // Add and update
}