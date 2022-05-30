package com.example.myfilms.utils

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