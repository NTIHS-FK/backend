package com.ntihs_fk.error

import java.lang.Exception

class UnauthorizedException(message: String? = "Authentication Error.") : Exception(message)