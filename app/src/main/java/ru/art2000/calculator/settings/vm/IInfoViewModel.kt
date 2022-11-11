package ru.art2000.calculator.settings.vm

import ru.art2000.calculator.settings.model.AuthorLink

interface IInfoViewModel {

    val authorLinks: List<AuthorLink>

    val changeLogText: String?

}