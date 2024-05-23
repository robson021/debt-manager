package com.github.robson021.debtmanager.extensions

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait


class Response {
    companion object
}

suspend inline fun Response.Companion.toJson(body: Any) = ServerResponse
    .ok()
    .contentType(MediaType.APPLICATION_JSON)
    .bodyValueAndAwait(body)

suspend inline fun Response.Companion.toPlainText(body: Any) = ServerResponse
    .ok()
    .contentType(MediaType.TEXT_PLAIN)
    .bodyValueAndAwait(body)
