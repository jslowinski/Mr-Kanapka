package com.swapi.swapikotlin.api

import com.swapi.swapikotlin.api.model.CartDto

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