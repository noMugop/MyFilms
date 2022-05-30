package com.example.myfilms.utils

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

fun getErrorMessage(code: Int): String {
    return when (code) {
        ErrorStatus.BAD_REQUEST.code -> ErrorStatus.BAD_REQUEST.message
        ErrorStatus.UNAUTHORIZED.code -> ErrorStatus.UNAUTHORIZED.message
        ErrorStatus.NOT_FOUND.code -> ErrorStatus.NOT_FOUND.message
        ErrorStatus.TIMEOUT.code -> ErrorStatus.TIMEOUT.message
        ErrorStatus.INTERNAL_SERVER_ERROR.code -> ErrorStatus.INTERNAL_SERVER_ERROR.message
        ErrorStatus.NO_CONNECTION.code -> ErrorStatus.NO_CONNECTION.message
        ErrorStatus.SERVICE_UNAVAILABLE.code -> ErrorStatus.SERVICE_UNAVAILABLE.message
        ErrorStatus.GATEWAY_TIMEOUT.code -> ErrorStatus.GATEWAY_TIMEOUT.message
        else -> ErrorStatus.UNKNOWN_ERROR.message
    }
}

fun getErrorCode(throwable: Throwable): Int {
    return when (throwable) {
        is HttpException -> {
            checkHttpCode(throwable.code())
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

private fun checkHttpCode(code: Int): Int {
    return when (code) {
        ErrorStatus.BAD_REQUEST.code -> ErrorStatus.BAD_REQUEST.code
        ErrorStatus.UNAUTHORIZED.code -> ErrorStatus.UNAUTHORIZED.code
        ErrorStatus.NOT_FOUND.code -> ErrorStatus.NOT_FOUND.code
        ErrorStatus.INTERNAL_SERVER_ERROR.code -> ErrorStatus.INTERNAL_SERVER_ERROR.code
        ErrorStatus.SERVICE_UNAVAILABLE.code -> ErrorStatus.SERVICE_UNAVAILABLE.code
        ErrorStatus.GATEWAY_TIMEOUT.code -> ErrorStatus.GATEWAY_TIMEOUT.code
        else -> ErrorStatus.UNKNOWN_ERROR.code
    }
}