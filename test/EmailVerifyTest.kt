package com.ntihs_fk

import com.ntihs_fk.functions.EmailVerify
import junit.framework.TestCase.assertEquals
import org.junit.Test

class EmailVerifyTest {
    @Test
    fun emailVerifyTest() {
        val emailVerify = EmailVerify()
        emailVerify.sendEmail("aijdfajodwsdf@gmail.com")
    }
}