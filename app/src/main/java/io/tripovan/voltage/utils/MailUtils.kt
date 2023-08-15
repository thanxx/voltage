package io.tripovan.voltage.utils

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import io.tripovan.voltage.App
import java.io.File

class MailUtils {
    fun sendLogs() {
        val emailSelectorIntent = Intent(Intent.ACTION_SENDTO)
        emailSelectorIntent.data = Uri.parse("mailto:")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("voltagedev@proton.me"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Voltage App logs, ${App.appVersion}")
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        emailIntent.selector = emailSelectorIntent
        val attachment = File(App.instance.filesDir, "log.txt")
        Log.i(Constants.TAG, "Sending log, size=${attachment.length()}")
        val fileUri: Uri = FileProvider.getUriForFile(App.instance, "com.yourapp.provider", attachment)

        emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (emailIntent.resolveActivity(App.instance.packageManager) != null) App.instance.startActivity(
            emailIntent
        )
    }
}