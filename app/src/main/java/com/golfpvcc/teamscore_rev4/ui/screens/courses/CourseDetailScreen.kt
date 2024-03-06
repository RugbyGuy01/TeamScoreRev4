package com.golfpvcc.teamscore_rev4.ui.screens.courses

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import com.golfpvcc.teamscore_rev4.Constants.courseDetailPlaceHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CourseDetailScreen(
    courseID: Int,
    navController: NavController,
    viewModel: CoursesViewModel
) {
   val scope = rememberCoroutineScope()
    val courseDetail = remember{
        mutableStateOf(courseDetailPlaceHolder)
    }
    LaunchedEffect(true ){
        scope.launch(Dispatchers.IO) {
            courseDetail.value = viewModel.getCourseById(courseID)
        }
    }

}