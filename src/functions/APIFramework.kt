package com.ntihs_fk.functions

data class APIData <T>(val error: Boolean = false, val message: String = "ok", val data: T?)

fun <T> apiFrameworkFun(data: T?): APIData<T> {
    return APIData(data = data)
}