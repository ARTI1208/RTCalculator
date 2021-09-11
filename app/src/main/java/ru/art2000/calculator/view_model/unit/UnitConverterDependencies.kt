package ru.art2000.calculator.view_model.unit

import ru.art2000.calculator.R
import ru.art2000.calculator.model.unit.*
import kotlin.math.pow

object UnitConverterDependencies {

    @JvmStatic
    fun getCategoryItems(str: String): Array<UnitConverterItem<Double>> {
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

    private const val MM = 1e3
    private const val CM = 1e2
    private const val DM = 1e1
    private const val METER = 1.0
    private const val KM = 1e-3
    private const val IN = 39.37
    private const val FT = 3.2808
    private const val YD = 1.0936

    private const val PA = 101325.0

    private val velocityItems = arrayOf<UnitConverterItem<Double>>(
        DoubleRatioConverterItem(R.string.velocity_mpers, 1.0),
        DoubleRatioConverterItem(R.string.velocity_mpermin, 60.0),
        DoubleRatioConverterItem(R.string.velocity_kmpermin, 60.0 / 1000.0),
        DoubleRatioConverterItem(R.string.velocity_kmperh, 3600.0 / 1000.0),
        DoubleRatioConverterItem(R.string.velocity_ftpers, 3.280839),
        DoubleRatioConverterItem(R.string.velocity_miperh, 2.2369),
        DoubleRatioConverterItem(R.string.velocity_mach, 0.003),
        DoubleRatioConverterItem(R.string.velocity_kn, 1.9438)
    )

    private val distanceItems = arrayOf<UnitConverterItem<Double>>(
        DoubleRatioConverterItem(R.string.distance_mm, MM),
        DoubleRatioConverterItem(R.string.distance_cm, CM),
        DoubleRatioConverterItem(R.string.distance_dm, DM),
        DoubleRatioConverterItem(R.string.distance_m, METER),
        DoubleRatioConverterItem(R.string.distance_km, KM),
        DoubleRatioConverterItem(R.string.distance_in, IN),
        DoubleRatioConverterItem(R.string.distance_ft, FT),
        DoubleRatioConverterItem(R.string.distance_yd, YD),
        DoubleRatioConverterItem(R.string.distance_mi, 0.00062137),
        DoubleRatioConverterItem(R.string.distance_nmi, 0.0005399568),
        DoubleRatioConverterItem(R.string.distance_ly, 1 / 946000000.0),
        DoubleRatioConverterItem(R.string.distance_arshin, 1 / 0.71)
    )

    private val areaItems = arrayOf<UnitConverterItem<Double>>(
        DoubleRatioConverterItem(R.string.area_mm2, MM.pow(2)),
        DoubleRatioConverterItem(R.string.area_cm2, CM.pow(2)),
        DoubleRatioConverterItem(R.string.area_dm2, DM.pow(2)),
        DoubleRatioConverterItem(R.string.area_m2, METER.pow(2)),
        DoubleRatioConverterItem(R.string.area_km2, KM.pow(2)),
        DoubleRatioConverterItem(R.string.area_a, 1e-2),
        DoubleRatioConverterItem(R.string.area_ha, 1e-4),
        DoubleRatioConverterItem(R.string.area_in2, IN.pow(2)),
        DoubleRatioConverterItem(R.string.area_ft2, FT.pow(2)),
        DoubleRatioConverterItem(R.string.area_yd2, YD.pow(2)),
        DoubleRatioConverterItem(R.string.area_acre, 4046.8564224),
    )

    private val GAL_UK = 4.5461 * DM.pow(3)

    private val volumeItems = arrayOf<UnitConverterItem<Double>>(
        DoubleRatioConverterItem(R.string.volume_mm3, MM.pow(3)),
        DoubleRatioConverterItem(R.string.volume_cm3, CM.pow(3)),
        DoubleRatioConverterItem(R.string.volume_dm3, DM.pow(3)),
        DoubleRatioConverterItem(R.string.volume_m3, METER.pow(3)),
        DoubleRatioConverterItem(R.string.volume_in3, IN.pow(3)),
        DoubleRatioConverterItem(R.string.volume_ft3, FT.pow(2)),
        DoubleRatioConverterItem(R.string.volume_yd3, YD.pow(3)),
        DoubleRatioConverterItem(R.string.volume_gal_uk, GAL_UK),
        DoubleRatioConverterItem(R.string.volume_oz_uk, 2.5 * GAL_UK),
        DoubleRatioConverterItem(R.string.volume_qt_uk, 4 * GAL_UK),
        DoubleRatioConverterItem(R.string.volume_pt_uk, 8 * GAL_UK),
        DoubleRatioConverterItem(R.string.volume_bbl, 163.66 * CM.pow(3)),
    )

    private val massItems = arrayOf<UnitConverterItem<Double>>(
        DoubleRatioConverterItem(R.string.mass_mg, 1e6),
        DoubleRatioConverterItem(R.string.mass_g, 1e3),
        DoubleRatioConverterItem(R.string.mass_kg, 1.0),
        DoubleRatioConverterItem(R.string.mass_qq, 1e2),
        DoubleRatioConverterItem(R.string.mass_t, 1e3),
        DoubleRatioConverterItem(R.string.mass_gr, 64.79891e6),
        DoubleRatioConverterItem(R.string.mass_oz, 28349.52e6),
        DoubleRatioConverterItem(R.string.mass_lb, 0.45359237),
        DoubleRatioConverterItem(R.string.mass_ct, 0.2e3),
        DoubleRatioConverterItem(R.string.mass_u, 1.660539066605e-27),
    )

    private val pressureItems = arrayOf<UnitConverterItem<Double>>(
        DoubleRatioConverterItem(R.string.pressure_atm, 1.0),
        DoubleRatioConverterItem(R.string.pressure_at, 0.96784),
        DoubleRatioConverterItem(R.string.pressure_pa, PA),
        DoubleRatioConverterItem(R.string.pressure_hpa, 1e-2 * PA),
        DoubleRatioConverterItem(R.string.pressure_kpa, 1e-3 * PA),
        DoubleRatioConverterItem(R.string.pressure_bar, 1e-5 * PA),
        DoubleRatioConverterItem(R.string.pressure_mm_hg, 760.0),
        DoubleRatioConverterItem(R.string.pressure_mm_wg, 10332.3),
        DoubleRatioConverterItem(R.string.pressure_in_hg, 101325.0),
        DoubleRatioConverterItem(R.string.pressure_in_wg, 29.9213),
        DoubleRatioConverterItem(R.string.pressure_psi, 14.696),
    )

    private val temperatureItems = arrayOf<UnitConverterItem<Double>>(
        DoubleFormulaConverterItem(R.string.temperature_celsius, { it - 273.15 }, { it + 273.15 }),
        DoubleFormulaConverterItem(R.string.temperature_fahrenheit, { (it * 9 / 5) - 459.67 }, { (it + 459.67) * 5 / 9 }),
        DoubleRatioConverterItem(R.string.temperature_kelvin, 1.0),
        DoubleRatioConverterItem(R.string.temperature_rankin, 1.8),
        DoubleFormulaConverterItem(R.string.temperature_réaumur, { 0.8 * (it - 273.15) }, { 1.25 * it + 273.15 }),
    )

}