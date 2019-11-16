package com.lxw.dsladapter.utils

//
// Created by lxw on 2019-10-16.
//
open class DslPlaceHolder<T:Any> constructor(val valueClass: Class<*>) {
    var value: T? = null

    companion object {
        inline operator fun <reified U:Any> invoke(value: U? = null): DslPlaceHolder<U> {
            val optional = DslPlaceHolder<U>(U::class.java)
            optional.value = value;
            return optional
        }
    }

    fun notify(adapter: DslMultiTypeAdapter) {
        adapter.forEachIndexed { position, data ->
            if (data == this) {
                adapter.notifyItemChanged(position)
            }
        }
    }
}