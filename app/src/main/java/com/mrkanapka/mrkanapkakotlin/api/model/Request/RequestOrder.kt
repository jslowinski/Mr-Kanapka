package com.mrkanapka.mrkanapkakotlin.api.model.Request

data class RequestOrder (
    var access_token: String,
    var day: String,
    var month: String,
    var year: String,
    var comment: String
)