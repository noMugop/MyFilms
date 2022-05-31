package com.example.myfilms.presentation.utils

enum class ExceptionStatus(val code: Int, val message: String) {

    SUCCESS_CODE(300, "Успех"),
    BAD_REQUEST(400, "Неверный запрос"),
    UNAUTHORIZED(401, "Не зарегистрированный пользователь"),
    NOT_FOUND(404, "Пользователь не найден"),
    TIMEOUT(408, "Время ожидания вышло"),
    INTERNAL_SERVER_EXCEPTION(500, "Ошибка сервера"),
    NO_CONNECTION(502, "Проверьте подключение к интеренету"),
    SERVICE_UNAVAILABLE(503, "Сервис недоступен"),
    GATEWAY_TIMEOUT(504, "Превышено время ожидания сервера"),
    UNKNOWN_EXCEPTION(0, "Неизвестная ошибка")
}

