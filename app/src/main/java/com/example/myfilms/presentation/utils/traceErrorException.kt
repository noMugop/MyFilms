package com.example.myfilms.domain.utils

import com.example.myfilms.presentation.utils.ExceptionStatus
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

fun getErrorMessage(code: Int): String {
    return when (code) {
        ExceptionStatus.BAD_REQUEST.code -> ExceptionStatus.BAD_REQUEST.message
        ExceptionStatus.UNAUTHORIZED.code -> ExceptionStatus.UNAUTHORIZED.message
        ExceptionStatus.NOT_FOUND.code -> ExceptionStatus.NOT_FOUND.message
        ExceptionStatus.TIMEOUT.code -> ExceptionStatus.TIMEOUT.message
        ExceptionStatus.INTERNAL_SERVER_EXCEPTION.code -> ExceptionStatus.INTERNAL_SERVER_EXCEPTION.message
        ExceptionStatus.NO_CONNECTION.code -> ExceptionStatus.NO_CONNECTION.message
        ExceptionStatus.SERVICE_UNAVAILABLE.code -> ExceptionStatus.SERVICE_UNAVAILABLE.message
        ExceptionStatus.GATEWAY_TIMEOUT.code -> ExceptionStatus.GATEWAY_TIMEOUT.message
        else -> ExceptionStatus.UNKNOWN_EXCEPTION.message
    }
}

fun getErrorCode(throwable: Throwable): Int {
    return when (throwable) {
        is HttpException -> {
            throwable.code()
        }
        is SocketTimeoutException -> {
            ExceptionStatus.TIMEOUT.code
        }
        is IOException -> {
            ExceptionStatus.NO_CONNECTION.code
        }
        else -> ExceptionStatus.UNKNOWN_EXCEPTION.code
    }
}