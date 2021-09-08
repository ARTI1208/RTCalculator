package ru.art2000.calculator.view_model.settings

import ru.art2000.calculator.model.settings.AuthorLink

interface IInfoViewModel {

    val authorLinks: List<AuthorLink>

    val changeLogText: String?

}