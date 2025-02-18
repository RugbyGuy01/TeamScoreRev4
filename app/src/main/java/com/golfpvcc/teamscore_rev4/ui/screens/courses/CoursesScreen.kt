package com.golfpvcc.teamscore_rev4.ui.screens.courses

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController


import com.golfpvcc.teamscore_rev4.database.model.CourseRecord
import com.golfpvcc.teamscore_rev4.ui.navigation.ROOT_GRAPH_ROUTE
import com.golfpvcc.teamscore_rev4.ui.navigation.TeamScoreScreen
import com.golfpvcc.teamscore_rev4.ui.screens.GenericAppBar
import com.golfpvcc.teamscore_rev4.utils.Constants.orCourseRecHolderList
import com.golfpvcc.teamscore_rev4.utils.SetScreenOrientation

@Composable
fun CoursesScreen(
    navController: NavHostController,
) {
    val courseViewModel = viewModel<CoursesViewModel>(
        factory = CoursesViewModel.CoursesViewModelFactor()
    )
    val openDialog = remember { mutableStateOf(false) }
    val deleteText = remember { mutableStateOf("\"Are you sure you want to delete course?") }
    val courseToDelete = remember { mutableStateOf(listOf<CourseRecord>()) }
    val courses = courseViewModel.courses.observeAsState()

    SetScreenOrientation(SCREEN_ORIENTATION_PORTRAIT)

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {
        Scaffold(
            topBar = {
                GenericAppBar("Team Summary")
                {
                    navController.navigate(route = TeamScoreScreen.ScreenSummary.route) {
                        popUpTo(ROOT_GRAPH_ROUTE)
                    }
                }
            },
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
        itemsIndexed(courses) { index, course ->
            CourseItem(
                course,
                openDialog,
                deleteText = deleteText,
                navController,
                courseToDelete = courseToDelete,
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            )
        }
    } // end of CourseItem
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CourseItem(
    course: CourseRecord,
    openDialog: MutableState<Boolean>,
    deleteText: MutableState<String>,
    navController: NavController,
    courseToDelete: MutableState<List<CourseRecord>>,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .height(70.dp)
            .clickable {
                if (0 < course.mId) {
                    Log.d("VIN:", "CourseItem onItemClick navigate to Player Setup")
                    navController.navigate(route = TeamScoreScreen.ScreenPlayerSetup.passId(course.mId))
                }
            }

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    Log.d("VIN", "CourseDetail?id={${course.mId}}")
                    if (course.mId != 0) {
                        navController.navigate(
                            route = TeamScoreScreen.ScreenDetailCourse.passId(
                                course.mId
                            )
                        )  // goto detail screen
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit Course",
                    modifier = Modifier.size(35.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.size(14.dp))
            Log.d("VIN", "IconButton Delete Course ${course.mId}")
            IconButton(
                onClick = {
                    if (course.mId != 0) {
                        openDialog.value = true
                        deleteText.value =
                            "Are you sure you want to delete this course - ${course.mCoursename}?"
                        courseToDelete.value = mutableListOf(course)
                        Log.d("VIN", "Delete Course ${course.mId}")
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete course",
                    modifier = Modifier.size(35.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.size(14.dp))
            Text(
                text = course.mCoursename,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Normal
            )

        } // end of row
    } // end of card
}

