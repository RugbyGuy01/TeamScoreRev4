package com.golfpvcc.teamscore_rev4

import com.golfpvcc.teamscore_rev4.utils.getDateFromMillisForBackup
import org.junit.Test
import org.junit.Assert.*

class UtilsUnitTest {
    @Test
    fun testGetDateFromMillisForBackup() {
        val millis = System.currentTimeMillis()
        val dateString = getDateFromMillisForBackup(millis)
        
        // Format is "dd_M_yyyy_hh_mm_ss"
        // Check if it matches the pattern (roughly)
        val regex = """\d{2}_\d{1,2}_\d{4}_\d{2}_\d{2}_\d{2}""".toRegex()
        assertTrue("Date string '$dateString' does not match expected format", regex.matches(dateString))
    }
}
