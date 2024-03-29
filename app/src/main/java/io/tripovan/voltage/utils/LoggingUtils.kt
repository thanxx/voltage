package io.tripovan.voltage.utils

import io.tripovan.voltage.App
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader



class LoggingUtils {
    fun dumpLogs() {

            val process = Runtime.getRuntime().exec("logcat -d ${Constants.TAG}:V *:S")
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?

            while (bufferedReader.readLine().also { line = it } != null) {
                line?.let { writeLogToFile(it) }
            }
            bufferedReader.close()
    }

    private fun writeLogToFile(logMessage: String) {
        val logFile = File(App.instance.filesDir, "log.txt")
            val writer = BufferedWriter(FileWriter(logFile, true))
            writer.append(logMessage)
            writer.newLine()
            writer.close()
    }

}