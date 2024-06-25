package com.golfpvcc.teamscore_rev4.ui.screens.summary.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.golfpvcc.teamscore_rev4.ui.screens.scorecard.PlayerHeading
import com.golfpvcc.teamscore_rev4.utils.FRONT_NINE_DISPLAY
import com.golfpvcc.teamscore_rev4.utils.TOTAL_18_HOLE
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.*
import java.util.Date


class EmailScores(private val mContext: Context) {
    private val mTo = arrayOf("vgamble@golfpvcc.com")
    private var mSubject = "Team Score"
    private var mBody = "my scores"

    fun toPostOffice() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // Only email apps handle this.
            putExtra(Intent.EXTRA_EMAIL, mTo)
            putExtra(Intent.EXTRA_SUBJECT, mSubject)
            putExtra(Intent.EXTRA_TEXT, mBody)
        }
        if (intent.resolveActivity(mContext.packageManager) != null) {
            mContext.startActivity(intent)
            Log.d("VIN", "toPostOffice startActivity")
        } else {
            Log.d("VIN", "toPostOffice failed")
        }
    }

    fun setEmailAddress(emailAddress: String) {
        mTo[0] = emailAddress
    }

    fun setEmailSubject(subject: String) {
        mSubject = subject
    }

    fun setEmailBody(body: String) {
        mBody = body
    }
}

/*
This function is used by the email player's score function - build the message body so the user can add it to a excel spreadsheet
 */
fun getSpreadSheetScore(player: PlayerHeading, holePar: IntArray): String {
    var playersScore: String? = " "
    var courseParStr: String? = ""
    var nineHole: Int = 0
    var score: Int = 0
    var total: Int = 0
    var parForHole: Int
    val today = Date()

    val myDate = getLocalDate()

    playersScore = "$myDate,"

    for (holeNumber in 0..<TOTAL_18_HOLE) {
        if (holeNumber == FRONT_NINE_DISPLAY) {
            playersScore += "$nineHole,"
            nineHole = 0
        }
        score = player.mScore[holeNumber]
        total += score
        nineHole += score
        playersScore += "$score,"
    }
    playersScore += "$nineHole,"
    playersScore += "$total,"
// now add the par for the course to the email
    total = 0
    nineHole = 0
    for (holeNumber in 0 until TOTAL_18_HOLE) {
        if (holeNumber == FRONT_NINE_DISPLAY) {
            courseParStr += "$nineHole,"
            nineHole = 0
        }
        parForHole = holePar[holeNumber]
        total += parForHole
        nineHole += parForHole
        courseParStr += "$parForHole,"
    }
    courseParStr += "$nineHole,"
    courseParStr += "$total,"
    playersScore += courseParStr

    return playersScore
}

fun getLocalDate(): String {
    val formatter = ofPattern("MM-dd-yyyy")
    val current = LocalDateTime.now().format(formatter)
    return (current)
}