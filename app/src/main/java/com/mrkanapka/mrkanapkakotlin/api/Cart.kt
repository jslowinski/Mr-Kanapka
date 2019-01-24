package com.mrkanapka.mrkanapkakotlin.api

import com.mrkanapka.mrkanapkakotlin.api.model.CartDto

class Cart {
    companion object {
        //private var info = "This is info"
        val cartList : MutableList<CartDto> = ArrayList()
        @JvmStatic fun setInfoItem(info: CartDto){
            cartList.add(info)
        }
        @JvmStatic fun infoItem(): MutableList<CartDto> {
            return cartList
        }
        @JvmStatic fun deleteItem(position: Int){
            cartList.removeAt(position)
        }
    }
}