package com.mrkanapka.mrkanapkakotlin.api.model.Request

data class RequestLogin (
    val email : String,
    val password : String,
    val registrationid : String
)