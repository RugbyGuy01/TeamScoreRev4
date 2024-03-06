package com.golfpvcc.teamscore_rev4.database.converters

import androidx.room.TypeConverter
import java.util.Date


open class DataConverter {
    @TypeConverter
    fun arrayToString(myArray: IntArray): String? {
        if (myArray == null || myArray.isEmpty()) {
            return ""
        }
        var str = myArray[0].toString()
        for (i in 1 until myArray.size) {
            str = str + "," + myArray[i].toString()
        }
        return str
    }
    @TypeConverter
    fun stringToArray(arrayString: String): IntArray {
        var holesList: List<String> = arrayString.split(",") // return a list of strings

        val holeInts = IntArray(holesList.size) { holesList[it].toInt() }

        return holeInts
    }

    @TypeConverter
    fun toDate(date: Long?): Date? {
        return date?.let { Date(it) }
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
}