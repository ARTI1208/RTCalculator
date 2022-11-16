package ru.art2000.calculator.currency.preferences

import ru.art2000.extensions.preferences.*

class CommonCurrencyPreferenceHelper(
    preferences: AppPreferences,
    onCurrencyUpdateTypeInterval: (String, Int) -> Unit,
) : CurrencyPreferenceHelper {

    private val isShouldSaveCurrencyConversion by preferences.booleanPreference(
        CurrencyKeys.KEY_SAVE_CURRENCY, CurrencyDefaults.DEFAULT_SHOULD_SAVE_CONVERSION_VALUE
    ).listen {
        if (!it) {
            clearConversionValues()
        }
    }
    override val updateOnFirstTabOpen by preferences.booleanPreference(
        CurrencyKeys.KEY_CURRENCY_UPDATE_ON_TAB_OPEN,
        CurrencyDefaults.DEFAULT_CURRENCY_UPDATE_ON_TAB_OPEN
    )

    override val updateDateMillisProperty = preferences.longPreference(
        CurrencyKeys.KEY_CURRENCY_UPDATE_DATE_MILLIS,
        CurrencyDefaults.DEFAULT_CURRENCY_PRELOADED_UPDATE_DATE,
    )

    override var updateDateMillis by updateDateMillisProperty

    override var conversionCode by preferences.stringPreference(
        "last_conversion_code", CurrencyDefaults.DEFAULT_CONVERSION_CODE
    )
        private set

    override var conversionValue by preferences.doublePreference(
        "last_conversion_double", CurrencyDefaults.DEFAULT_CONVERSION_VALUE
    ).mapGetter {
        if (isShouldSaveCurrencyConversion) it else CurrencyDefaults.DEFAULT_CONVERSION_VALUE
    }
        private set

    override fun putConversionValuesIfNeeded(code: String, value: Double) {
        if (!isShouldSaveCurrencyConversion) return

        conversionCode = code
        conversionValue = value
    }

    private fun clearConversionValues() {
        conversionCode = CurrencyDefaults.DEFAULT_CONVERSION_CODE
        conversionValue = CurrencyDefaults.DEFAULT_CONVERSION_VALUE
    }

    override val currencyBackgroundUpdateType by preferences.stringPreference(
        CurrencyKeys.KEY_CURRENCIES_BACKGROUND,
        CurrencyDefaults.DEFAULT_CURRENCY_BACKGROUND_UPDATE_TYPE
    ).listen { type -> onCurrencyUpdateTypeInterval(type, currencyBackgroundUpdateInterval) }

    override val currencyBackgroundUpdateInterval: Int by preferences
        .intPreference(
            CurrencyKeys.KEY_CURRENCIES_INTERVAL,
            CurrencyDefaults.DEFAULT_CURRENCY_BACKGROUND_UPDATE_INTERVAL
        ).mapStore(IntStringMapping)
        .listen { interval ->
            onCurrencyUpdateTypeInterval(currencyBackgroundUpdateType, interval)
        }

    override var isDeleteTooltipShown by preferences.booleanPreference(
        CurrencyKeys.KEY_CURRENCY_DELETE_TOOLTIP_SHOWN,
        CurrencyDefaults.DEFAULT_CURRENCY_DELETE_TOOLTIP_SHOWN
    )
        private set

    override fun setDeleteTooltipShown() {
        isDeleteTooltipShown = true
    }

}