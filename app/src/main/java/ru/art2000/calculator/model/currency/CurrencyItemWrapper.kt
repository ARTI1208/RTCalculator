package ru.art2000.calculator.model.currency

import androidx.room.Ignore

class CurrencyItemWrapper(code: String, rate: Double, position: Int) : CurrencyItem(code, rate, position) {

    constructor(code: String, rate: Double): this(code, rate, -1)

    constructor(item: CurrencyItem): this(item.code, item.rate, item.position)

    @Ignore
    var isSelected = false
}