package com.martorell.albert.basketcoach.data.network.config

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Properties

/**
 * Utility responsible for safely inspecting local Android properties files to extract developer credentials.
 */
object LocalPropertiesReader {

    private const val FILE_NAME: String = "local.properties"

    private val SEARCH_PATHS: List<String> = listOf(
        FILE_NAME,
        "../$FILE_NAME",
        "../../$FILE_NAME"
    )

    /**
     * Attempts to read a property value by [key] across standard local paths.
     *
     * @param key The property key name.
     * @return Trimmed property value if found and non-blank; null otherwise.
     */
    fun getProperty(key: String): String? {
        for (path in SEARCH_PATHS) {
            val file = File(path)
            if (file.exists() && file.isFile && file.canRead()) {
                val value = readKeyFromFile(file, key)
                if (!value.isNullOrBlank()) {
                    return value.trim()
                }
            }
        }
        return null
    }

    private fun readKeyFromFile(file: File, key: String): String? {
        return try {
            val properties = Properties()
            FileInputStream(file).use { stream ->
                properties.load(stream)
            }
            properties.getProperty(key)
        } catch (_: IOException) {
            null
        }
    }
}
