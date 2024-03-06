package com.golfpvcc.teamscore_rev4.ui.screens.coursedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.golfpvcc.teamscore_rev4.TeamScoreCardApp.Companion.getCourseDao
import com.golfpvcc.teamscore_rev4.database.dao.CourseDao
import com.golfpvcc.teamscore_rev4.database.model.CourseRecord
import com.golfpvcc.teamscore_rev4.repository.CourseRepository
import kotlinx.coroutines.launch

