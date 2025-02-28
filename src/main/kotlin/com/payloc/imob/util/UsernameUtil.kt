package com.payloc.imob.util

import java.util.*

class UsernameUtil {

    companion object {
        fun generateUsername(email: String): String {
            return email.split("@")[0] + "_" + UUID.randomUUID().toString().take(8)
        }
    }
}