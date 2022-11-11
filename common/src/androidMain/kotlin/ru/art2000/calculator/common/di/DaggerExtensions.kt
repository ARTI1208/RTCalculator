package ru.art2000.calculator.common.di

import dagger.MapKey
import ru.art2000.calculator.common.model.MainPage

@MapKey
annotation class PageKey(val value: MainPage)