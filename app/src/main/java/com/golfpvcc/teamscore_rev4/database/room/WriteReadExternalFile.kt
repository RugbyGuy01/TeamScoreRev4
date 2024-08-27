package com.golfpvcc.teamscore_rev4.database.room

import android.content.Context
import android.widget.Toast
import com.golfpvcc.teamscore_rev4.TeamScoreCardApp
import com.golfpvcc.teamscore_rev4.utils.DATABASE_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class WriteReadExternalFile(val context: Context) {

    suspend fun writeBackupFile(backupOutput: OutputStream) {
        withContext(Dispatchers.IO) {

            TeamScoreCardApp.closeTeamScoreDatabase()

            val teamScoreDatabaseDir = context.getDatabasePath(DATABASE_NAME)
            val databaseFp = File(teamScoreDatabaseDir.getParent(), DATABASE_NAME)

            val bufferSize = 8 * 1024
            val buffer = ByteArray(bufferSize)
            var bytesRead = bufferSize
            val inputDb: InputStream = FileInputStream(databaseFp)

            while ((inputDb.read(buffer, 0, bufferSize)
                    .also { inputStream -> bytesRead = inputStream }) > 0
            ) {
                backupOutput.write(buffer, 0, bytesRead)
            }
            backupOutput.flush()
            backupOutput.close()    // close backup file
            inputDb.close()      // close database file
        }
    }

    suspend fun readBackupFile(backupInput: InputStream) {

        withContext(Dispatchers.IO) {
            if (0 != backupInput.available()) { // we have a file to read

                TeamScoreCardApp.closeTeamScoreDatabase()

                val teamScoreDatabaseDir = context.getDatabasePath(DATABASE_NAME)
                val databaseFp = File(teamScoreDatabaseDir.getParent(), DATABASE_NAME)

                val bufferSize = 8 * 1024
                val buffer = ByteArray(bufferSize)
                var bytesRead = bufferSize
                val restoreDb: OutputStream = FileOutputStream(databaseFp)
                while ((backupInput.read(buffer, 0, bufferSize)
                        .also { inputStream -> bytesRead = inputStream }) > 0
                ) {
                    restoreDb.write(buffer, 0, bytesRead)
                }
                restoreDb.flush()
                backupInput.close()    // close backup file
                restoreDb.close()      // close database file
            }
        }
    }
}