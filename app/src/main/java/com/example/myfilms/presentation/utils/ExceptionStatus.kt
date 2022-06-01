package com.example.myfilms.presentation.utils

import com.example.myfilms.R

enum class ExceptionStatus(val code: Int, val messageRes: Int) {

    SUCCESS_CODE(300, R.string.success),
    BAD_REQUEST(400, R.string.bad_request),
    UNAUTHORIZED(401, R.string.unauthorized),
    NOT_FOUND(404, R.string.user_not_found),
    TIMEOUT(408, R.string.timeout),
    INTERNAL_SERVER_EXCEPTION(500, R.string.internal_server_exception),
    NO_CONNECTION(502, R.string.no_connection),
    SERVICE_UNAVAILABLE(503, R.string.service_unavailable),
    GATEWAY_TIMEOUT(504, R.string.gateway_timeout),
    UNKNOWN_EXCEPTION(0, R.string.unknown_exception)
}

