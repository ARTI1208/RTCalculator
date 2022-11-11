package ru.art2000.calculator.currency.model

import android.annotation.SuppressLint
import android.content.Context

private val codeToIdentifier = hashMapOf<String, Int>()

@SuppressLint("DiscouragedApi")
internal fun CurrencyItem.getNameIdentifier(context: Context) = codeToIdentifier.getOrPut(code) {
    context.resources.getIdentifier(
        "currency_$code",
        "string",
        context.packageName
    )
}