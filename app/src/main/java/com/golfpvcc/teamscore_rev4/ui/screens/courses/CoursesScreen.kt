package com.golfpvcc.teamscore_rev4.ui.screens.courses

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController


import com.golfpvcc.teamscore_rev4.database.model.CourseRecord
import com.golfpvcc.teamscore_rev4.utils.Constants.orCourseRecHolderList

@Composable
fun CoursesScreen(
    navController: NavHostController
) {
    val courseViewModel = viewModel<CoursesViewModel>(
        factory = CoursesViewModel.CoursesViewModelFactor()
    )
    val openDialog = remember { mutableStateOf(false) }
    val deleteText = remember { mutableStateOf("\"Are you sure you want to delete course?") }
    val courseToDelete = remember { mutableStateOf(listOf<CourseRecord>()) }
    val courses = courseViewModel.courses.observeAsState()
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    navController.navigate(route = "CourseDetail?id={-1}")
                }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            }

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it)
            ) {
                SelectGolfCourse()
                Spacer(modifier = Modifier.size(12.dp))
                DisplayGolfCourses(
                    courses = courses.value.orCourseRecHolderList(),
                    openDialog = openDialog,
                    deleteText = deleteText,
                    navController = navController,
                    courseToDelete = courseToDelete,
                )

            }
            DeleteDialog(
                openDialog = openDialog,
                text = deleteText,
                courseToDelete = courseToDelete,
                action = {
                    courseToDelete.value.forEach {
                        courseViewModel.deleteCourse(it)
                    }
                })

        }
    }
}

@Composable
fun SelectGolfCourse() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Select Golf Course", Modifier.padding(5.dp),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DisplayGolfCourses(
    courses: List<CourseRecord>,
    openDialog: MutableState<Boolean>,
    deleteText: MutableState<String>,
    navController: NavController,
    courseToDelete: MutableState<List<CourseRecord>>,
) {

    LazyColumn(modifier = Modifier.padding(2.dp)) {
        items(coursesState.courseRecords.size) { index ->
            CourseItem(
                coursesState = coursesState,
                index = index,
                navController = navController,
                onDeleteCourse = courseViewModel::deleteCourse
            )
        } // end of CourseItem


    }