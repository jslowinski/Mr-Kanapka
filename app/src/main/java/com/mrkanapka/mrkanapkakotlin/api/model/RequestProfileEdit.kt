package com.mrkanapka.mrkanapkakotlin.api.model

data class RequestProfileEdit (
    val access_token: String,
    val email: String,
    val first_name: String,
    val id_destination: Int,
    val last_name: String,
    val telephone: String
)