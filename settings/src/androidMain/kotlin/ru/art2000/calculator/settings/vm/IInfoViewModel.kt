package ru.art2000.calculator.settings.vm

import ru.art2000.calculator.settings.model.AuthorLink

internal interface IInfoViewModel {

    val authorLinks: List<AuthorLink>

    val changeLogText: String?

}