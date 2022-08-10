package me.honkling.mhplus.lib

import me.honkling.mhplus.MHPlus
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Field
import java.util.Arrays

class Settings {
    private val settingsFile = File("./config/mhplus.bin")
    var hideAdvertisements = true
    var hideNpcMessages = true
    var hideMinehutBroadcasts = true
    var protectAdvertisementTypos = true

    init {
        read()
    }

    fun write() {
        try {
            if (settingsFile.exists()) settingsFile.delete()
            settingsFile.createNewFile()
            val writer = FileOutputStream(settingsFile, true)

            // Settings octal
            var octal = 0
            var steps = 1
            val fields = Settings::class.java.declaredFields
            for (field in fields) {
                if (field.type != Boolean::class.java) continue
                field.isAccessible = true
                val value = field.getBoolean(this)
                octal += if (value) steps else 0
                steps *= 2
            }
            writer.write(octal.toByte().toInt())

            writer.close()
        } catch (e: IOException) {
            MHPlus.instance.logger.error("An error occurred writing settings.")
            e.printStackTrace()
        }
    }

    private fun read() {
        if (settingsFile.exists()) {
            try {
                val stream = FileInputStream(settingsFile)
                var octalSettings = Integer.toBinaryString(stream.read())
                val fields = Settings::class.java.declaredFields
                val settings = Arrays
                    .stream(fields)
                    .filter { field: Field -> field.type == Boolean::class.java }
                    .toArray { size -> arrayOfNulls<Field>(size) }
                val settingsCount = settings.size
                octalSettings += "0".repeat(settingsCount - octalSettings.length)
                for (i in settings.indices) {
                    val field = settings[i] ?: continue
                    val value = octalSettings[i].toString().toInt()
                    field.isAccessible = true
                    field.setBoolean(this, value == 1)
                }

                stream.close()
            } catch (e: IOException) {
                MHPlus.instance.logger.error("An error occurred reading settings.")
                e.printStackTrace()
            }
        }
    }
}