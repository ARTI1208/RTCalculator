package ru.art2000.calculator.view_model.unit

import ru.art2000.calculator.R
import ru.art2000.calculator.model.unit.FormulaConverterItem
import ru.art2000.calculator.model.unit.RatioConverterItem
import ru.art2000.calculator.model.unit.UnitConverterItem

object UnitConverterDependencies {

    @JvmStatic
    fun getCategoryItems(str: String): Array<UnitConverterItem> {
        return when (str) {
            "velocity" -> velocityItems
            "distance" -> distanceItems
            "area" -> areaItems
            "volume" -> volumeItems
            "mass" -> massItems
            "pressure" -> pressureItems
            "temperature" -> temperatureItems
            else -> velocityItems
        }
    }

    @JvmStatic
    fun getCategoryInt(str: String?): Int {
        return when (str) {
            "velocity" -> VELOCITY
            "distance" -> DISTANCE
            "area" -> AREA
            "volume" -> VOLUME
            "mass" -> MASS
            "pressure" -> PRESSURE
            "temperature" -> TEMPERATURE
            else -> VELOCITY
        }
    }

    private const val VELOCITY = 0
    private const val DISTANCE = 1
    private const val AREA = 2
    private const val VOLUME = 3
    private const val MASS = 4
    private const val PRESSURE = 5
    private const val TEMPERATURE = 6

    private val velocityItems = arrayOf<UnitConverterItem>(
            RatioConverterItem(R.string.velocity_mpers, 1.0),
            RatioConverterItem(R.string.velocity_mpermin, 60.0),
            RatioConverterItem(R.string.velocity_kmpermin, 60.0 / 1000.0),
            RatioConverterItem(R.string.velocity_kmperh, 3600.0 / 1000.0),
            RatioConverterItem(R.string.velocity_ftpers, 3.280839),
            RatioConverterItem(R.string.velocity_miperh, 2.2369),
            RatioConverterItem(R.string.velocity_mach, 0.003),
            RatioConverterItem(R.string.velocity_kn, 1.9438)
    )

    private val distanceItems = arrayOf<UnitConverterItem>(
            RatioConverterItem(R.string.distance_mm, 1000.0),
            RatioConverterItem(R.string.distance_cm, 100.0),
            RatioConverterItem(R.string.distance_dm, 10.0),
            RatioConverterItem(R.string.distance_m, 1.0),
            RatioConverterItem(R.string.distance_km, 1 / 1000.0),
            RatioConverterItem(R.string.distance_in, 39.37),
            RatioConverterItem(R.string.distance_ft, 3.2808),
            RatioConverterItem(R.string.distance_yd, 1.0936),
            RatioConverterItem(R.string.distance_mi, 0.00062137),
            RatioConverterItem(R.string.distance_nmi, 0.0005399568),
            RatioConverterItem(R.string.distance_ly, 1 / 946000000.0),
            RatioConverterItem(R.string.distance_arshin, 1 / 0.71)
    )

    private val areaItems = arrayOf<UnitConverterItem>(
            RatioConverterItem(R.string.area_mm2, 1000000.0),
            RatioConverterItem(R.string.area_cm2, 10000.0),
            RatioConverterItem(R.string.area_dm2, 100.0),
            RatioConverterItem(R.string.area_m2, 1.0)
    )

    private val volumeItems = arrayOf<UnitConverterItem>(
            RatioConverterItem(R.string.volume_cm3, 1000000.0),
            RatioConverterItem(R.string.volume_m3, 1.0)
    )

    private val massItems = arrayOf<UnitConverterItem>(
            RatioConverterItem(R.string.mass_g, 1000.0),
            RatioConverterItem(R.string.mass_kg, 1.0)
    )

    private val pressureItems = arrayOf<UnitConverterItem>(
            RatioConverterItem(R.string.pressure_atm, 101325.0),
            RatioConverterItem(R.string.pressure_pa, 1.0)
    )

    private val temperatureItems = arrayOf<UnitConverterItem>(
            FormulaConverterItem(R.string.temperature_celsius, "X", "X"),
            FormulaConverterItem(R.string.temperature_fahrenheit, "9÷5×X+32", "5÷9×(X-32)")
    )

}