package ru.art2000.calculator.calculator.computation.numbers

import platform.posix.tgamma

internal actual object DoubleMath {

    actual fun factorial(x: Double) = tgamma(x + 1)

}