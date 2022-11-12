package com.example.fixurfonemobile.model

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class PasswordHash {

    @Throws(Exception::class)
    fun encryptSHA(password: String): String? {
        val md = MessageDigest.getInstance("SHA-256")
        md.reset()
        md.update(password.toByteArray())
        val mdArray = md.digest()
        val sb = StringBuilder(mdArray.size * 2)
        for (b in mdArray) {
            val v: Int = (b.toInt() and 0xff)
            if (v < 16) sb.append('0')
            sb.append(Integer.toHexString(v))
        }
        return sb.toString()
    }
}