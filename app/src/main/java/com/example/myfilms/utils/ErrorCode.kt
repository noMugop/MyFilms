package com.example.myfilms.utils

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

fun getErrorCode(throwable: Throwable): Int {
    return when (throwable) {
        is HttpException -> {
            throwable.code()
        }
        is SocketTimeoutException -> {
            ErrorStatus.TIMEOUT.code
        }
        is IOException -> {
            ErrorStatus.NO_CONNECTION.code
        }
        else -> ErrorStatus.UNKNOWN_ERROR.code
    }
}