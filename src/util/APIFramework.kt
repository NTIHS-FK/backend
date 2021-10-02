package com.ntihs_fk.functions

data class APIData <T>(val error: Boolean, val message: String?, val data: T?)

fun <T> apiFrameworkFun(data: T?, error: Boolean = false, message: String? = "ok"): APIData<T> {
    return APIData(error, message, data)
}