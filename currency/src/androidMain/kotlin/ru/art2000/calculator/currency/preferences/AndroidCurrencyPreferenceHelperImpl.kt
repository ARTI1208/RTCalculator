package ru.art2000.calculator.currency.preferences

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.work.ExistingPeriodicWorkPolicy
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.art2000.calculator.currency.background.CurrencyDownloadWorker
import ru.art2000.extensions.preferences.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AndroidCurrencyPreferenceHelperImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : AndroidCurrencyPreferenceHelper {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

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
        context.getString(CurrencyDefaults.DEFAULT_CURRENCY_UPDATE_DATE).toLong()
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

    private fun setCurrencyUpdateTypeInterval(type: String, interval: Int) =
        CurrencyDownloadWorker.setupCurrencyDownload(
            context, type, interval, ExistingPeriodicWorkPolicy.REPLACE,
        )

    override val currencyBackgroundUpdateType by preferences.stringPreference(
        CurrencyKeys.KEY_CURRENCIES_BACKGROUND,
        CurrencyDefaults.DEFAULT_CURRENCY_BACKGROUND_UPDATE_TYPE
    ).listen { type -> setCurrencyUpdateTypeInterval(type, currencyBackgroundUpdateInterval) }

    override val currencyBackgroundUpdateInterval: Int by preferences
        .intPreference(
            CurrencyKeys.KEY_CURRENCIES_INTERVAL,
            CurrencyDefaults.DEFAULT_CURRENCY_BACKGROUND_UPDATE_INTERVAL
        ).mapStore(IntStringMapping)
        .listen { interval ->
            setCurrencyUpdateTypeInterval(currencyBackgroundUpdateType, interval)
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