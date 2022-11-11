package ru.art2000.calculator.unit.functions

import ru.art2000.calculator.calculator.computation.CalculationNumberFormatter
import ru.art2000.calculator.calculator.computation.DoubleCalculations
import ru.art2000.calculator.unit.model.*
import kotlin.math.pow

internal abstract class DoubleFunctionsProvider<D>(
    formatter: CalculationNumberFormatter<Double>,
): ConverterFunctionsProvider<D> {

    override val calculations = DoubleCalculations(formatter)

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
            DoubleRatioConverterItem(/* mpers */ 1.0),
            DoubleRatioConverterItem(/* mpermin */ 60.0),
            DoubleRatioConverterItem(/* kmpermin */ 60.0 / 1000.0),
            DoubleRatioConverterItem(/* kmperh */ 3600.0 / 1000.0),
            DoubleRatioConverterItem(/* ftpers */ 3.280839),
            DoubleRatioConverterItem(/* miperh */ 2.2369),
            DoubleRatioConverterItem(/* mach */ 0.003),
            DoubleRatioConverterItem(/* kn */ 1.9438),
        )

        private val distanceItems = listOf<UnitConverterItem<Double>>(
            DoubleRatioConverterItem(/* mm */ MM),
            DoubleRatioConverterItem(/* cm */ CM),
            DoubleRatioConverterItem(/* dm */ DM),
            DoubleRatioConverterItem(/* m */ METER),
            DoubleRatioConverterItem(/* km */ KM),
            DoubleRatioConverterItem(/* in */ IN),
            DoubleRatioConverterItem(/* ft */ FT),
            DoubleRatioConverterItem(/* yd */ YD),
            DoubleRatioConverterItem(/* mi */ 0.00062137),
            DoubleRatioConverterItem(/* nmi */ 0.0005399568),
            DoubleRatioConverterItem(/* ly */ 1 / 946000000.0),
            DoubleRatioConverterItem(/* arshin */ 1 / 0.71),
        )

        private val areaItems = listOf<UnitConverterItem<Double>>(
            DoubleRatioConverterItem(/* mm2 */ MM.pow(2)),
            DoubleRatioConverterItem(/* cm2 */ CM.pow(2)),
            DoubleRatioConverterItem(/* dm2 */ DM.pow(2)),
            DoubleRatioConverterItem(/* m2 */ METER.pow(2)),
            DoubleRatioConverterItem(/* km2 */ KM.pow(2)),
            DoubleRatioConverterItem(/* a */ 1e-2),
            DoubleRatioConverterItem(/* ha */ 1e-4),
            DoubleRatioConverterItem(/* in2 */ IN.pow(2)),
            DoubleRatioConverterItem(/* ft2 */ FT.pow(2)),
            DoubleRatioConverterItem(/* yd2 */ YD.pow(2)),
            DoubleRatioConverterItem(/* acre */ 2.471053814671653E-4),
        )

        private val GAL_UK = DM.pow(3) / 4.5461

        private val volumeItems = listOf<UnitConverterItem<Double>>(
            DoubleRatioConverterItem(/* mm3 */ MM.pow(3)),
            DoubleRatioConverterItem(/* cm3 */ CM.pow(3)),
            DoubleRatioConverterItem(/* dm3 */ DM.pow(3)),
            DoubleRatioConverterItem(/* m3 */ METER.pow(3)),
            DoubleRatioConverterItem(/* in3 */ IN.pow(3)),
            DoubleRatioConverterItem(/* ft3 */ FT.pow(3)),
            DoubleRatioConverterItem(/* yd3 */ YD.pow(3)),
            DoubleRatioConverterItem(/* gal_uk */ GAL_UK),
            DoubleRatioConverterItem(/* oz_uk */ 160 * GAL_UK),
            DoubleRatioConverterItem(/* qt_uk */ 4 * GAL_UK),
            DoubleRatioConverterItem(/* pt_uk */ 8 * GAL_UK),
            DoubleRatioConverterItem(/* bbl */ CM.pow(3) / 158.988),
        )

        private val massItems = listOf<UnitConverterItem<Double>>(
            DoubleRatioConverterItem(/* mg */ 1e6),
            DoubleRatioConverterItem(/* g */ 1e3),
            DoubleRatioConverterItem(/* kg */ 1.0),
            DoubleRatioConverterItem(/* qq */ 1e-2),
            DoubleRatioConverterItem(/* t */ 1e-3),
            DoubleRatioConverterItem(/* gr */ 1e6 / 64.79891),
            DoubleRatioConverterItem(/* oz */ 1e6 / 28349.52),
            DoubleRatioConverterItem(/* lb */ 1 / 0.45359237),
            DoubleRatioConverterItem(/* ct */ 1e3 / 0.2),
            DoubleRatioConverterItem(/* u */ 1 / 1.660539066605e-27),
        )

        private val pressureItems = listOf<UnitConverterItem<Double>>(
            DoubleRatioConverterItem(/* atm */ 1.0),
            DoubleRatioConverterItem(/* at */ 1.03323),
            DoubleRatioConverterItem(/* pa */ PA),
            DoubleRatioConverterItem(/* hpa */ 1e-2 * PA),
            DoubleRatioConverterItem(/* kpa */ 1e-3 * PA),
            DoubleRatioConverterItem(/* bar */ 1e-5 * PA),
            DoubleRatioConverterItem(/* mm_hg */ 760.0),
            DoubleRatioConverterItem(/* mm_wg */ 10332.3),
            DoubleRatioConverterItem(/* in_hg */ PA / 3386.389),
            DoubleRatioConverterItem(/* in_wg */ 1 / 0.002456),
            DoubleRatioConverterItem(/* psi */ 14.696),
        )

        private val temperatureItems = listOf<UnitConverterItem<Double>>(
            DoubleFormulaConverterItem(/* celsius */ { it - 273.15 }, { it + 273.15 }),
            DoubleFormulaConverterItem(/* fahrenheit */ { (it * 9 / 5) - 459.67 }, { (it + 459.67) * 5 / 9 }),
            DoubleRatioConverterItem(/* kelvin */ 1.0),
            DoubleRatioConverterItem(/* rankin */ 1.8),
            DoubleFormulaConverterItem(/* réaumur */ { 0.8 * (it - 273.15) }, { 1.25 * it + 273.15 }),
        )

        @Suppress("FunctionName")
        fun DoubleRatioConverterItem(
            ratio: Double,
        ) = RatioConverterItem(ratio, Double::times, Double::div, 0.0)

        @Suppress("FunctionName")
        fun DoubleFormulaConverterItem(
            fromAbsolute: (Double) -> Double,
            toAbsolute: (Double) -> Double,
        ) = FormulaConverterItem(fromAbsolute, toAbsolute, 0.0)
    }
}