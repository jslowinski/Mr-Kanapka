package com.swapi.swapikotlin.api

class Cart {
    companion object {
        var info = "This is info"
        @JvmStatic fun setInfoFilm(info: String){
            this.info = info
        }
        @JvmStatic fun InfoFilm(): String {
            return info
        }
    }
}