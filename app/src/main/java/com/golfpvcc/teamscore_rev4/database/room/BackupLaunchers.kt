package com.golfpvcc.teamscore_rev4.database.room

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch


@Composable
fun buildBackupLauncher(): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val writeExternalFile = WriteReadExternalFile(context)
    val backupResultLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            onResult = {
                it.data?.data?.let {
                    context.contentResolver.openOutputStream(it)?.let {
                        scope.launch {
                            writeExternalFile.writeBackupFile(it)
                        }
                    }
                }
            }
        )
    return (backupResultLauncher)
}

@Composable
fun buildRestoreLauncher(): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val writeExternalFile = WriteReadExternalFile(context)  // class that hold the read/write
    val restoreResultLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            onResult = {
                it.data?.data?.let {
                    context.contentResolver.openInputStream(it)?.let {
                        scope.launch {
                            writeExternalFile.readBackupFile(it)
                        }
                    }
                }
            }
        )
    return (restoreResultLauncher)
}