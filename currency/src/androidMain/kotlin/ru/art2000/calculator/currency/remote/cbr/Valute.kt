package ru.art2000.calculator.currency.remote.cbr

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "Valute", strict = false)
internal data class Valute @JvmOverloads constructor(
        @field:Element(name = "CharCode") var charCode: String = "USD",
        @field:Element(name = "Nominal") var quantity: Int = 1,
        @field:Element(name = "Value") var valueString: String = "1",
//        @field:Element(name = "Value") var value: Double = 1.0,
) {

    val value: Double get() = valueString.replace(',', '.').toDouble()
}