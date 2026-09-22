package com.golfpvcc.teamscore_rev4.utils

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