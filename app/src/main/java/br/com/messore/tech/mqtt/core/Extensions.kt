package br.com.messore.tech.mqtt.core

import android.util.Log

fun Any.log(message: String = "", exception: Throwable? = null) {
    Log.e(javaClass.name, message, exception)
}
