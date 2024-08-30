package com.golfpvcc.teamscore_rev4.utils

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object TeamObjects {
    val holeParList = listOf(
        HoleParList(3),
        HoleParList(4),
        HoleParList(5)
    )
}

data class HoleParList(
    val Par: Int = 4
)
fun buildTime():String {
    val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
    val  current = LocalDateTime.now().format(formatter)
    return (current)
}