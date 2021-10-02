package com.ntihs_fk.error

import java.lang.Exception

class ForbiddenRequestException(message: String? = "Forbidden") : Exception(message)