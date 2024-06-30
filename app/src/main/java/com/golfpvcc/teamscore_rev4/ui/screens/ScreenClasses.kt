package com.golfpvcc.teamscore_rev4.ui.screens

import androidx.compose.ui.graphics.Color
import com.golfpvcc.teamscore_rev4.utils.HOLE_ARRAY_SIZE
import com.golfpvcc.teamscore_rev4.utils.VIN_LIGHT_GRAY


data class HdcpParHoleHeading(
    val vinTag: Int = 0,
    var mName: String = "",
    var mHole: IntArray = IntArray(HOLE_ARRAY_SIZE) { i -> i + 1 },
    var mTotal: String = "",
    var mColor: Color = Color(VIN_LIGHT_GRAY),
)

data class PlayerHeading(
    val vinTag: Int = 0,
    var mHdcp: String = "",     // not used on the screen
    var mName: String = "",
    var mScore: IntArray = IntArray(HOLE_ARRAY_SIZE),   // gross scores
    var mDisplayScore: IntArray = IntArray(HOLE_ARRAY_SIZE) { 0 },
    var mStokeHole: IntArray = IntArray(HOLE_ARRAY_SIZE) { 0 },
    var mTeamHole: IntArray = IntArray(HOLE_ARRAY_SIZE),  // used for team scoring
    var mTotal: String = "",
)

data class TeamUsedHeading(
    val vinTag: Int = 0,
    var mName: String = "",
    var mHole: IntArray = IntArray(HOLE_ARRAY_SIZE) { 0 },
    var mTotal: String = "",
)