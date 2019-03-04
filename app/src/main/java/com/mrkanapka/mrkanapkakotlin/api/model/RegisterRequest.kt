package com.mrkanapka.mrkanapkakotlin.api.model

data class RegisterRequest (val email : String,
                            val password : String,
                            val id_destination : String)