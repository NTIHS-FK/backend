package com.ntihs_fk.functions

import com.google.gson.Gson
import com.ntihs_fk.data.TwitterConfigData
import java.io.File

fun <T> initConfigFile(file: File, data: T, throwException: Boolean = true) {
    if (!file.exists() && !file.isFile) {
        val gson = Gson()

        file.writeText(
            gson.toJson(data)
        )

        if (throwException)
            throw NoSuchFileException(file)
    }
}