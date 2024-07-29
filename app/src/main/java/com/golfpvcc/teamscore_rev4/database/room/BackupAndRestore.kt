package com.golfpvcc.teamscore_rev4.database.room

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.util.Log
import com.golfpvcc.teamscore_rev4.TeamScoreCardApp
import com.golfpvcc.teamscore_rev4.utils.BACKUP_FILE_NAME
import com.golfpvcc.teamscore_rev4.utils.DATABASE_NAME
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date


const val SHAREDPREF = "BACKUP_PREFERENCE"
const val MAXIMUM_DATABASE_FILE = 3

// https://medium.com/dwarsoft/how-to-provide-backup-restore-feature-for-room-database-in-android-d87f111d9c64
fun backupDatabase(context: Context): String {
    val teamDatabase = TeamScoreCardApp.getRoomDatabase()
    teamDatabase.close()
    var resultsFromBackup: String = ""
    val teamScoreDatabaseFile = context.getDatabasePath(DATABASE_NAME)
    val teamScoreBackupDirectory: File = buildDirectory("backupTeamScore")
    val backupFileName = BACKUP_FILE_NAME
    val sfpath = teamScoreBackupDirectory.path + File.separator + backupFileName
    Log.d("BACKUP", "Backup file stored at $sfpath")
    if (!teamScoreBackupDirectory.exists()) {
        teamScoreBackupDirectory.mkdirs()
    } else {
        //Directory Exists. Delete a file if count is 5 already. Because we will be creating a new.
        //This will create a conflict if the last backup file was also on the same date. In that case,
        //we will reduce it to 4 with the function call but the below code will again delete one more file.
        //checkAndDeleteBackupFile(sdir, sfpath)
    }
    val saveFile = File(sfpath)
    if (saveFile.exists()) {
        Log.d("BACKUP", "File exists. Deleting it and then creating new file.")
        saveFile.delete()
    }
    try {
        if (saveFile.createNewFile()) {
            val bufferSize = 8 * 1024
            val buffer = ByteArray(bufferSize)
            var bytesRead = bufferSize
            val saveDb: OutputStream = FileOutputStream(sfpath)
            val inDb: InputStream = FileInputStream(teamScoreDatabaseFile)
            while ((inDb.read(buffer, 0, bufferSize).also { bytesRead = it }) > 0) {
                saveDb.write(buffer, 0, bytesRead)
            }
            saveDb.flush()
            inDb.close()
            saveDb.close()
            val sharedPreferences = context.getSharedPreferences(SHAREDPREF, MODE_PRIVATE)
            sharedPreferences.edit().putString("backupFileName", backupFileName).apply()
            resultsFromBackup = "Save: $sfpath "
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Log.d("BACKUP", "ex: $e")
        resultsFromBackup = e.toString()
    }
    return (resultsFromBackup)
}

// For copying file which is the copyFile() method, we can implement it as —
fun restoreDatabase(context: Context): String {
    val teamDatabase = TeamScoreCardApp.getRoomDatabase()
    teamDatabase.close()
    val teamScoreDatabaseFile = context.getDatabasePath(DATABASE_NAME)
    var resultsFromBackup = ""
    Log.d("BACKUP", "Destination file name and path ${teamScoreDatabaseFile.toString()}")

    val sharedPreferences = context.getSharedPreferences(SHAREDPREF, MODE_PRIVATE)
    var backupSaveFile = sharedPreferences.getString("backupFileName", DATABASE_NAME)
    if (backupSaveFile == null) {
        backupSaveFile = DATABASE_NAME
    }

    if (backupSaveFile.isNotEmpty()) {
        val teamScoreBackupDirectory: File = buildDirectory("backupTeamScore")
        val sourceFile = teamScoreBackupDirectory.path + File.separator + backupSaveFile
        Log.d("BACKUP", "Source file name and path $sourceFile")
        val srcFile = File(sourceFile)
        if (srcFile.exists()) {
            File(sourceFile).copyTo(File(teamScoreDatabaseFile.toString()), true);
            resultsFromBackup = "Database restored."
        } else {
            resultsFromBackup = "1. File not found or name changed! File $BACKUP_FILE_NAME"
        }
    } else {
        resultsFromBackup = "2. File not found or name changed! File $BACKUP_FILE_NAME"
    }

    return (resultsFromBackup)
}


//Make sure don't forget to ask runtime permission of read/write file
private fun buildDirectory(dirName: String): File {
    val dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        File(
            Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)
                .toString() + "/" + dirName
        )
    } else {
        File(
            Environment.getExternalStorageDirectory()
                .toString() + "/$DIRECTORY_DOWNLOADS/" + dirName
        )
    }
    return (dir)
}

fun checkAndDeleteBackupFile(directory: File, path: String?) {
    //This is to prevent deleting extra file being deleted which is mentioned in previous comment lines.
    val currentDateFile = path?.let { File(it) }
    var fileIndex = -1
    var lastModifiedTime = System.currentTimeMillis()

    if (currentDateFile != null) {
        if (!currentDateFile.exists()) {
            val files = directory.listFiles()
            if (files != null && files.size >= MAXIMUM_DATABASE_FILE) {
                for (i in files.indices) {
                    val file = files[i]
                    val fileLastModifiedTime = file.lastModified()
                    if (fileLastModifiedTime < lastModifiedTime) {
                        lastModifiedTime = fileLastModifiedTime
                        fileIndex = i
                    }
                }

                if (fileIndex != -1) {
                    val deletingFile = files[fileIndex]
                    if (deletingFile.exists()) {
                        deletingFile.delete()
                    }
                }
            }
        }
    }
}


//Now since we know the file is valid, we will first make a backup file of the existing data using the following method —
//fun backupDatabaseForRestore(activity: Activity, context: Context?) {
//    val dbfile = activity.getDatabasePath(DATABASE_NAME)
//    val sdir: File = File(getFilePath(context, 0), "backup")
//    val sfpath = sdir.path + File.separator + BACKUP_RESTORE_ROLLBACK_FILE_NAME
//    if (!sdir.exists()) {
//        sdir.mkdirs()
//    }
//    val savefile = File(sfpath)
//    if (savefile.exists()) {
//        Log.d(LOGGER, "Backup Restore - File exists. Deleting it and then creating new file.")
//        savefile.delete()
//    }
//    try {
//        if (savefile.createNewFile()) {
//            val buffersize = 8 * 1024
//            val buffer = ByteArray(buffersize)
//            var bytes_read = buffersize
//            val savedb: OutputStream = FileOutputStream(sfpath)
//            val indb: InputStream = FileInputStream(dbfile)
//            while ((indb.read(buffer, 0, buffersize).also { bytes_read = it }) > 0) {
//                savedb.write(buffer, 0, bytes_read)
//            }
//            savedb.flush()
//            indb.close()
//            savedb.close()
//        }
//    } catch (e: java.lang.Exception) {
//        e.printStackTrace()
//        Log.d(LOGGER, "ex for restore file: $e")
//    }
//}
// We will open up an intent for the user to select the database file they want to restore.
//private fun restoreDBIntent() {
//    val i = Intent(Intent.ACTION_GET_CONTENT)
//    i.setType("*/*")
//    startActivityForResult(Intent.createChooser(i, "Select DB File"), 12)
//}