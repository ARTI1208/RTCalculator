package ru.art2000.calculator.model.unit

import ru.art2000.calculator.R
import ru.art2000.calculator.view_model.calculator.DoubleCalculations
import ru.art2000.calculator.view_model.unit.UnitConverterFormatter
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

@Singleton
class DoubleFunctionsProvider @Inject constructor(): ConverterFunctionsProvider {

    override val calculations = DoubleCalculations(UnitConverterFormatter)

    private val store = hashMapOf<UnitCategory, MutableMap<String, Any>>()

    override fun getConverterFunctions(category: UnitCategory): ConverterFunctions {
        val items = UnitConverterDependencies.getCategoryItems(category)
        val defaultValue = 1.0
        val categoryStore = store.getOrPut(category) { hashMapOf() }
        return CategoryFunctions(calculations, items, defaultValue, categoryStore).also { functions ->
            if (items.isNotEmpty() && !items.first().isSet) {
                functions.setValue(0, functions.defaultValueString)
            }
        }
    }

    private object UnitConverterDependencies {

        @JvmStatic
        fun getCategoryItems(category: UnitCategory): List<UnitConverterItem<Double>> {
            return when (category) {
                UnitCategory.VELOCITY -> velocityItems
                UnitCategory.DISTANCE -> distanceItems
                UnitCategory.AREA -> areaItems
                UnitCategory.VOLUME -> volumeItems
                UnitCategory.MASS -> massItems
                UnitCategory.PRESSURE -> pressureItems
                UnitCategory.TEMPERATURE -> temperatureItems
            }
        }

        private const val MM = 1e3
        private const val CM = 1e2
        private const val DM = 1e1
        private const val METER = 1.0
        private const val KM = 1e-3
        private const val IN = 39.37
        private const val FT = 3.2808
        private const val YD = 1.0936

        private const val PA = 101325.0

        private val velocityItems = listOf<UnitConverterItem<Double>>(
            DoubleRatioConverterItem(R.string.velocity_mpers, R.string.short_velocity_mpers, 1.0),
            DoubleRatioConverterItem(R.string.velocity_mpermin, R.string.short_velocity_mpermin, 60.0),
            DoubleRatioConverterItem(R.string.velocity_kmpermin, R.string.short_velocity_kmpermin, 60.0 / 1000.0),
            DoubleRatioConverterItem(R.string.velocity_kmperh, R.string.short_velocity_kmperh, 3600.0 / 1000.0),
            DoubleRatioConverterItem(R.string.velocity_ftpers, R.string.short_velocity_ftpers, 3.280839),
            DoubleRatioConverterItem(R.string.velocity_miperh, R.string.short_velocity_miperh, 2.2369),
            DoubleRatioConverterItem(R.string.velocity_mach, R.string.short_velocity_mach, 0.003),
            DoubleRatioConverterItem(R.string.velocity_kn, R.string.short_velocity_kn, 1.9438),
        )

        private val distanceItems = listOf<UnitConverterItem<Double>>(
            DoubleRatioConverterItem(R.string.distance_mm, R.string.short_distance_mm, MM),
            DoubleRatioConverterItem(R.string.distance_cm, R.string.short_distance_cm, CM),
            DoubleRatioConverterItem(R.string.distance_dm, R.string.short_distance_dm, DM),
            DoubleRatioConverterItem(R.string.distance_m, R.string.short_distance_m, METER),
            DoubleRatioConverterItem(R.string.distance_km, R.string.short_distance_km, KM),
            DoubleRatioConverterItem(R.string.distance_in, R.string.short_distance_in, IN),
            DoubleRatioConverterItem(R.string.distance_ft, R.string.short_distance_ft, FT),
            DoubleRatioConverterItem(R.string.distance_yd, R.string.short_distance_yd, YD),
            DoubleRatioConverterItem(R.string.distance_mi, R.string.short_distance_mi, 0.00062137),
            DoubleRatioConverterItem(R.string.distance_nmi, R.string.short_distance_nmi, 0.0005399568),
            DoubleRatioConverterItem(R.string.distance_ly, R.string.short_distance_ly, 1 / 946000000.0),
            DoubleRatioConverterItem(R.string.distance_arshin, R.string.short_distance_arshin, 1 / 0.71),
        )

        private val areaItems = listOf<UnitConverterItem<Double>>(
            DoubleRatioConverterItem(R.string.area_mm2, R.string.short_area_mm2, MM.pow(2)),
            DoubleRatioConverterItem(R.string.area_cm2, R.string.short_area_cm2, CM.pow(2)),
            DoubleRatioConverterItem(R.string.area_dm2, R.string.short_area_dm2, DM.pow(2)),
            DoubleRatioConverterItem(R.string.area_m2, R.string.short_area_m2, METER.pow(2)),
            DoubleRatioConverterItem(R.string.area_km2, R.string.short_area_km2, KM.pow(2)),
            DoubleRatioConverterItem(R.string.area_a, R.string.short_area_a, 1e-2),
            DoubleRatioConverterItem(R.string.area_ha, R.string.short_area_ha, 1e-4),
            DoubleRatioConverterItem(R.string.area_in2, R.string.short_area_in2, IN.pow(2)),
            DoubleRatioConverterItem(R.string.area_ft2, R.string.short_area_ft2, FT.pow(2)),
            DoubleRatioConverterItem(R.string.area_yd2, R.string.short_area_yd2, YD.pow(2)),
            DoubleRatioConverterItem(R.string.area_acre, R.string.short_area_acre, 2.471053814671653E-4),
        )

        private val GAL_UK = DM.pow(3) / 4.5461

        private val volumeItems = listOf<UnitConverterItem<Double>>(
            DoubleRatioConverterItem(R.string.volume_mm3, R.string.short_volume_mm3, MM.pow(3)),
            DoubleRatioConverterItem(R.string.volume_cm3, R.string.short_volume_cm3, CM.pow(3)),
            DoubleRatioConverterItem(R.string.volume_dm3, R.string.short_volume_dm3, DM.pow(3)),
            DoubleRatioConverterItem(R.string.volume_m3, R.string.short_volume_m3, METER.pow(3)),
            DoubleRatioConverterItem(R.string.volume_in3, R.string.short_volume_in3, IN.pow(3)),
            DoubleRatioConverterItem(R.string.volume_ft3, R.string.short_volume_ft3, FT.pow(3)),
            DoubleRatioConverterItem(R.string.volume_yd3, R.string.short_volume_yd3, YD.pow(3)),
            DoubleRatioConverterItem(R.string.volume_gal_uk, R.string.short_volume_gal_uk, GAL_UK),
            DoubleRatioConverterItem(R.string.volume_oz_uk, R.string.short_volume_oz_uk, 160 * GAL_UK),
            DoubleRatioConverterItem(R.string.volume_qt_uk, R.string.short_volume_qt_uk, 4 * GAL_UK),
            DoubleRatioConverterItem(R.string.volume_pt_uk, R.string.short_volume_pt_uk, 8 * GAL_UK),
            DoubleRatioConverterItem(R.string.volume_bbl, R.string.short_volume_bbl, CM.pow(3) / 158.988),
        )

        private val massItems = listOf<UnitConverterItem<Double>>(
            DoubleRatioConverterItem(R.string.mass_mg, R.string.short_mass_mg, 1e6),
            DoubleRatioConverterItem(R.string.mass_g, R.string.short_mass_g, 1e3),
            DoubleRatioConverterItem(R.string.mass_kg, R.string.short_mass_kg, 1.0),
            DoubleRatioConverterItem(R.string.mass_qq, R.string.short_mass_qq, 1e-2),
            DoubleRatioConverterItem(R.string.mass_t, R.string.short_mass_t, 1e-3),
            DoubleRatioConverterItem(R.string.mass_gr, R.string.short_mass_gr, 1e6 / 64.79891),
            DoubleRatioConverterItem(R.string.mass_oz, R.string.short_mass_oz, 1e6 / 28349.52),
            DoubleRatioConverterItem(R.string.mass_lb, R.string.short_mass_lb, 1 / 0.45359237),
            DoubleRatioConverterItem(R.string.mass_ct, R.string.short_mass_ct, 1e3 / 0.2),
            DoubleRatioConverterItem(R.string.mass_u, R.string.short_mass_u, 1 / 1.660539066605e-27),
        )

        private val pressureItems = listOf<UnitConverterItem<Double>>(
            DoubleRatioConverterItem(R.string.pressure_atm, R.string.short_pressure_atm, 1.0),
            DoubleRatioConverterItem(R.string.pressure_at, R.string.short_pressure_at, 1.03323),
            DoubleRatioConverterItem(R.string.pressure_pa, R.string.short_pressure_pa, PA),
            DoubleRatioConverterItem(R.string.pressure_hpa, R.string.short_pressure_hpa, 1e-2 * PA),
            DoubleRatioConverterItem(R.string.pressure_kpa, R.string.short_pressure_kpa, 1e-3 * PA),
            DoubleRatioConverterItem(R.string.pressure_bar, R.string.short_pressure_bar, 1e-5 * PA),
            DoubleRatioConverterItem(R.string.pressure_mm_hg, R.string.short_pressure_mm_hg, 760.0),
            DoubleRatioConverterItem(R.string.pressure_mm_wg, R.string.short_pressure_mm_wg, 10332.3),
            DoubleRatioConverterItem(R.string.pressure_in_hg, R.string.short_pressure_in_hg, PA / 3386.389),
            DoubleRatioConverterItem(R.string.pressure_in_wg, R.string.short_pressure_in_wg, 1 / 0.002456),
            DoubleRatioConverterItem(R.string.pressure_psi, R.string.short_pressure_psi, 14.696),
        )

        private val temperatureItems = listOf<UnitConverterItem<Double>>(
            DoubleFormulaConverterItem(R.string.temperature_celsius, R.string.short_temperature_celsius, { it - 273.15 }, { it + 273.15 }),
            DoubleFormulaConverterItem(R.string.temperature_fahrenheit, R.string.short_temperature_fahrenheit, { (it * 9 / 5) - 459.67 }, { (it + 459.67) * 5 / 9 }),
            DoubleRatioConverterItem(R.string.temperature_kelvin, R.string.short_temperature_kelvin, 1.0),
            DoubleRatioConverterItem(R.string.temperature_rankin, R.string.short_temperature_rankin, 1.8),
            DoubleFormulaConverterItem(R.string.temperature_réaumur, R.string.short_temperature_réaumur, { 0.8 * (it - 273.15) }, { 1.25 * it + 273.15 }),
        )

    }
}