package com.example.myfilms.domain.utils

import com.example.myfilms.presentation.utils.ExceptionStatus
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

fun getErrorMessage(code: Int): Int {
    return when (code) {
        ExceptionStatus.BAD_REQUEST.code -> ExceptionStatus.BAD_REQUEST.messageRes
        ExceptionStatus.UNAUTHORIZED.code -> ExceptionStatus.UNAUTHORIZED.messageRes
        ExceptionStatus.NOT_FOUND.code -> ExceptionStatus.NOT_FOUND.messageRes
        ExceptionStatus.TIMEOUT.code -> ExceptionStatus.TIMEOUT.messageRes
        ExceptionStatus.INTERNAL_SERVER_EXCEPTION.code -> ExceptionStatus.INTERNAL_SERVER_EXCEPTION.messageRes
        ExceptionStatus.NO_CONNECTION.code -> ExceptionStatus.NO_CONNECTION.messageRes
        ExceptionStatus.SERVICE_UNAVAILABLE.code -> ExceptionStatus.SERVICE_UNAVAILABLE.messageRes
        ExceptionStatus.GATEWAY_TIMEOUT.code -> ExceptionStatus.GATEWAY_TIMEOUT.messageRes
        else -> ExceptionStatus.UNKNOWN_EXCEPTION.messageRes
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