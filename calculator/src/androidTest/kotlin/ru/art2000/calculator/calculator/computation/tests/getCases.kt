package ru.art2000.calculator.calculator.computation.tests

internal actual fun getCases(): List<TestCase<Double>> = buildList {

    val methods = TestCases::class.java.declaredMethods

    methods.filter {
        it.returnType == TestCase::class.java && it.parameterCount == 0
    }.forEach {
        it.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val result = it.invoke(TestCases) as TestCase<Double>
        this += result
    }
}