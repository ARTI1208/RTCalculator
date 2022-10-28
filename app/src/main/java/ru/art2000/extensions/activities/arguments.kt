package ru.art2000.extensions.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle

inline fun <reified E : Enum<*>> Intent.getEnumExtra(name: String) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializableExtra(name, E::class.java)
    } else {
        @Suppress("DEPRECATION")
        getSerializableExtra(name) as E
    }

inline fun <reified E : Enum<*>> Bundle.getEnum(name: String) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializable(name, E::class.java)
    } else {
        @Suppress("DEPRECATION")
        getSerializable(name) as E
    }

fun Bundle?.getInt(key: String, defaultValue: Int) = this?.getInt(key, defaultValue) ?: defaultValue