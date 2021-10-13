package com.ntihs_fk.util

import com.google.gson.GsonBuilder
import java.io.File

fun <T> initConfigFile(file: File, data: T, throwException: Boolean = true) {
    if (!file.exists() && !file.isFile) {
        val gson = GsonBuilder().setPrettyPrinting().create()

        file.writeText(
            gson.toJson(data)
        )

        if (throwException)
            throw NoSuchFileException(file)
    }
}