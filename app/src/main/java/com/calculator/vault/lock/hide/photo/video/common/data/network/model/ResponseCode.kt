package com.calculator.vault.lock.hide.photo.video.common.data.network.model

enum class ResponseCode constructor(val code: Int) {
    OK(200),
    BadRequest(400),
    Unauthenticated(401),
    Unauthorized(403),
    NotFound(404),
    RequestTimeOut(408),
    Conflict(409),
    InvalidData(422),
    Blocked(423),
    ForceUpdate(426),
    InternalServerError(500)
}