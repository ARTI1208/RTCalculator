package ru.art2000.helpers

import java.lang.StringBuilder
import java.text.DecimalFormat
import java.text.NumberFormat

object GeneralHelper  {

    @JvmField
    val resultNumberFormat: NumberFormat = DecimalFormat("#.#######")

    @JvmStatic
    fun joinToString(list: List<*>, separator: String, prefix: String, postfix: String): String {
        val builder = StringBuilder(prefix)
        for ((count, obj) in list.withIndex()) {
            if (count + 1 > 1) {
                builder.append(separator)
            }
            builder.append(obj)
        }
        builder.append(postfix)
        return builder.toString()
    }
}
