package ru.art2000.calculator.calculator.computation.numbers

import org.apache.commons.math3.special.Gamma

internal actual object DoubleMath {

    actual fun factorial(x: Double) = Gamma.gamma(x + 1)

}