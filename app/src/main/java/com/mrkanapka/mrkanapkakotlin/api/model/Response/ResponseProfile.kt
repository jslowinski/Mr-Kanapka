package com.mrkanapka.mrkanapkakotlin.api.model.Response

data class ResponseProfile (
    val email: String,
    val first_name: String,
    val id_city: Int,
    val id_destination: Int,
    val last_name: String,
    val telephone: String,
    val name: String,
    val street: String,
    val house_number: Int
)