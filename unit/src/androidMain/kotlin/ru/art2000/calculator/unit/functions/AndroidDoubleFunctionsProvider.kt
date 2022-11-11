package ru.art2000.calculator.unit.functions

import ru.art2000.calculator.unit.R
import ru.art2000.calculator.unit.model.DisplayableUnitItem
import ru.art2000.calculator.unit.model.UnitCategory
import javax.inject.Inject

internal class AndroidDoubleFunctionsProvider @Inject constructor() :
    DoubleFunctionsProvider<Int>(UnitConverterFormatter) {

    override fun getConverterItemNames(category: UnitCategory): List<DisplayableUnitItem<Int>> {
        return getCategoryItems(category)
    }

    companion object {

        fun getCategoryItems(category: UnitCategory): List<DisplayableUnitItem<Int>> {
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

        private val velocityItems = listOf(
            DisplayableUnitItem(R.string.velocity_mpers, R.string.short_velocity_mpers),
            DisplayableUnitItem(R.string.velocity_mpermin, R.string.short_velocity_mpermin),
            DisplayableUnitItem(R.string.velocity_kmpermin, R.string.short_velocity_kmpermin),
            DisplayableUnitItem(R.string.velocity_kmperh, R.string.short_velocity_kmperh),
            DisplayableUnitItem(R.string.velocity_ftpers, R.string.short_velocity_ftpers),
            DisplayableUnitItem(R.string.velocity_miperh, R.string.short_velocity_miperh),
            DisplayableUnitItem(R.string.velocity_mach, R.string.short_velocity_mach),
            DisplayableUnitItem(R.string.velocity_kn, R.string.short_velocity_kn)
        )

        private val distanceItems = listOf(
            DisplayableUnitItem(R.string.distance_mm, R.string.short_distance_mm),
            DisplayableUnitItem(R.string.distance_cm, R.string.short_distance_cm),
            DisplayableUnitItem(R.string.distance_dm, R.string.short_distance_dm),
            DisplayableUnitItem(R.string.distance_m, R.string.short_distance_m),
            DisplayableUnitItem(R.string.distance_km, R.string.short_distance_km),
            DisplayableUnitItem(R.string.distance_in, R.string.short_distance_in),
            DisplayableUnitItem(R.string.distance_ft, R.string.short_distance_ft),
            DisplayableUnitItem(R.string.distance_yd, R.string.short_distance_yd),
            DisplayableUnitItem(R.string.distance_mi, R.string.short_distance_mi),
            DisplayableUnitItem(R.string.distance_nmi, R.string.short_distance_nmi),
            DisplayableUnitItem(R.string.distance_ly, R.string.short_distance_ly),
            DisplayableUnitItem(R.string.distance_arshin, R.string.short_distance_arshin)
        )

        private val areaItems = listOf(
            DisplayableUnitItem(R.string.area_mm2, R.string.short_area_mm2),
            DisplayableUnitItem(R.string.area_cm2, R.string.short_area_cm2),
            DisplayableUnitItem(R.string.area_dm2, R.string.short_area_dm2),
            DisplayableUnitItem(R.string.area_m2, R.string.short_area_m2),
            DisplayableUnitItem(R.string.area_km2, R.string.short_area_km2),
            DisplayableUnitItem(R.string.area_a, R.string.short_area_a),
            DisplayableUnitItem(R.string.area_ha, R.string.short_area_ha),
            DisplayableUnitItem(R.string.area_in2, R.string.short_area_in2),
            DisplayableUnitItem(R.string.area_ft2, R.string.short_area_ft2),
            DisplayableUnitItem(R.string.area_yd2, R.string.short_area_yd2),
            DisplayableUnitItem(R.string.area_acre, R.string.short_area_acre)
        )

        private val volumeItems = listOf(
            DisplayableUnitItem(R.string.volume_mm3, R.string.short_volume_mm3),
            DisplayableUnitItem(R.string.volume_cm3, R.string.short_volume_cm3),
            DisplayableUnitItem(R.string.volume_dm3, R.string.short_volume_dm3),
            DisplayableUnitItem(R.string.volume_m3, R.string.short_volume_m3),
            DisplayableUnitItem(R.string.volume_in3, R.string.short_volume_in3),
            DisplayableUnitItem(R.string.volume_ft3, R.string.short_volume_ft3),
            DisplayableUnitItem(R.string.volume_yd3, R.string.short_volume_yd3),
            DisplayableUnitItem(R.string.volume_gal_uk, R.string.short_volume_gal_uk),
            DisplayableUnitItem(R.string.volume_oz_uk, R.string.short_volume_oz_uk),
            DisplayableUnitItem(R.string.volume_qt_uk, R.string.short_volume_qt_uk),
            DisplayableUnitItem(R.string.volume_pt_uk, R.string.short_volume_pt_uk),
            DisplayableUnitItem(R.string.volume_bbl, R.string.short_volume_bbl)
        )

        private val massItems = listOf(
            DisplayableUnitItem(R.string.mass_mg, R.string.short_mass_mg),
            DisplayableUnitItem(R.string.mass_g, R.string.short_mass_g),
            DisplayableUnitItem(R.string.mass_kg, R.string.short_mass_kg),
            DisplayableUnitItem(R.string.mass_qq, R.string.short_mass_qq),
            DisplayableUnitItem(R.string.mass_t, R.string.short_mass_t),
            DisplayableUnitItem(R.string.mass_gr, R.string.short_mass_gr),
            DisplayableUnitItem(R.string.mass_oz, R.string.short_mass_oz),
            DisplayableUnitItem(R.string.mass_lb, R.string.short_mass_lb),
            DisplayableUnitItem(R.string.mass_ct, R.string.short_mass_ct),
            DisplayableUnitItem(R.string.mass_u, R.string.short_mass_u)
        )

        private val pressureItems = listOf(
            DisplayableUnitItem(R.string.pressure_atm, R.string.short_pressure_atm),
            DisplayableUnitItem(R.string.pressure_at, R.string.short_pressure_at),
            DisplayableUnitItem(R.string.pressure_pa, R.string.short_pressure_pa),
            DisplayableUnitItem(R.string.pressure_hpa, R.string.short_pressure_hpa),
            DisplayableUnitItem(R.string.pressure_kpa, R.string.short_pressure_kpa),
            DisplayableUnitItem(R.string.pressure_bar, R.string.short_pressure_bar),
            DisplayableUnitItem(R.string.pressure_mm_hg, R.string.short_pressure_mm_hg),
            DisplayableUnitItem(R.string.pressure_mm_wg, R.string.short_pressure_mm_wg),
            DisplayableUnitItem(R.string.pressure_in_hg, R.string.short_pressure_in_hg),
            DisplayableUnitItem(R.string.pressure_in_wg, R.string.short_pressure_in_wg),
            DisplayableUnitItem(R.string.pressure_psi, R.string.short_pressure_psi)
        )

        private val temperatureItems = listOf(
            DisplayableUnitItem(R.string.temperature_celsius, R.string.short_temperature_celsius),
            DisplayableUnitItem(
                R.string.temperature_fahrenheit,
                R.string.short_temperature_fahrenheit
            ),
            DisplayableUnitItem(R.string.temperature_kelvin, R.string.short_temperature_kelvin),
            DisplayableUnitItem(R.string.temperature_rankin, R.string.short_temperature_rankin),
            DisplayableUnitItem(R.string.temperature_réaumur, R.string.short_temperature_réaumur)
        )

    }
}