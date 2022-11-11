package ru.art2000.calculator.calculator.computation.numbers

import org.apache.commons.math3.special.Gamma
import org.apache.commons.math3.util.FastMath

internal actual object DoubleMath {

    actual fun factorial(x: Double) = Gamma.gamma(x + 1)

    actual fun toRadians(x: Double) = FastMath.toRadians(x)

}