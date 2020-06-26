package ru.art2000.calculator.view_model.currency

import dagger.Module
import dagger.Provides

@Module
class CurrencyModule {

    @Provides
    fun getStub(): Stub {
        return Stub()
    }

}