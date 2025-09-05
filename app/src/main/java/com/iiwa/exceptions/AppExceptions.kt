package com.iiwa.exceptions

import com.iiwa.data.error.FILE_NOT_FOUND
import com.iiwa.data.error.UNKNOWN_ERROR
import java.io.FileNotFoundException

object AppExceptions {

    fun getErrorCode(throwable: Throwable): Int {
        return when (throwable) {
            is FileNotFoundException -> FILE_NOT_FOUND
            else -> UNKNOWN_ERROR
        }
    }
}
