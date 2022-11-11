package ru.art2000.calculator.currency.remote.cbr

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "ValCurs", strict = false)
internal data class CurrenciesList @JvmOverloads constructor(
    @field:Attribute(name = "Date")
    var date: String = "666",

    @field:ElementList(entry = "Valute", inline = true)
    var valutes: MutableList<Valute> = mutableListOf(),
)