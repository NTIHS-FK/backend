package com.ntihs_fk.data

import org.jetbrains.exposed.sql.ResultRow

data class VerifyUserPasswordData(val verify: Boolean, val userData: ResultRow)
