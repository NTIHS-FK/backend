package com.ntihs_fk.error

import java.lang.Exception

class UnauthorizedRequestException(message: String? = "Authentication Error.") : Exception(message)