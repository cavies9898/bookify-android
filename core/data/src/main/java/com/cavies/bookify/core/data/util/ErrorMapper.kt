package com.cavies.bookify.core.data.util

import retrofit2.HttpException

object ErrorMapper {

    fun mapHttpError(code: Int): String = when (code) {
        400 -> "Solicitud inválida"
        401 -> "Credenciales incorrectas"
        403 -> "No tienes permiso"
        404 -> "Recurso no encontrado"
        409 -> "Conflicto con el recurso existente"
        422 -> "Datos inválidos"
        500 -> "Error interno del servidor"
        502 -> "Servidor no disponible"
        503 -> "Servicio no disponible"
        else -> "Error del servidor ($code)"
    }

    fun mapException(e: Exception): String = when (e) {
        is HttpException -> mapHttpError(e.code())
        is java.net.UnknownHostException -> "Sin conexión a internet"
        is java.net.SocketTimeoutException -> "Tiempo de espera agotado"
        else -> "Error del servidor"
    }
}
