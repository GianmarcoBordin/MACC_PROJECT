package com.mygdx.game.util

import android.content.Intent
import android.os.Build
import android.os.Bundle
import java.io.Serializable

/**
* These inline functions are used to suppress deprecated functions like getSerializableExtra and getSerializable
 * in android version below TIRAMISU
* */

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}

inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}

