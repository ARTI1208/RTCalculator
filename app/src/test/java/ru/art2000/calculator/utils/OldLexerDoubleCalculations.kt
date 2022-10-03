package ru.art2000.calculator.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.CalculatorLexerParseExpertiseViewBinding
import ru.art2000.calculator.model.calculator.AngleType
import ru.art2000.calculator.model.calculator.numbers.DoubleField
import ru.art2000.calculator.view.calculator.LexemeListAdapter
import ru.art2000.calculator.view_model.calculator.*
import kotlin.system.measureTimeMillis

sealed class OldLexerDoubleCalculations(
    override val formatter: CalculationNumberFormatter<Double>,
) : Calculations<Double> {

    final override val field = DoubleField

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val parser = CalculationParser(DoubleParserConfiguration(field))

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val lexer = OriginalLexer(DoubleLexerConfiguration(field))

    override fun calculate(expression: String, angleType: AngleType): Double? {
        return parser.parse(expression, angleType, lexer).compute()
    }

    override fun createDebugView(
        context: Context,
        expression: String,
        angleType: AngleType,
    ): View {
        val binding = CalculatorLexerParseExpertiseViewBinding.inflate(LayoutInflater.from(context))

        val (lexemes, lexerTime) = debug { lexer.getLexemes(expression.toCharArray()) ?: emptyList() }
        val (computable, parserTime) = debug { parser.fromLexemes(lexemes, angleType) }
        val (_, computeTime) = debug { computable.compute() }

        binding.lexerTime.text = context.getString(R.string.debug_lexer_time, lexerTime)
        binding.parserTime.text = context.getString(R.string.debug_parser_time, parserTime)
        binding.computeTime.text = context.getString(R.string.debug_compute_time, computeTime)

        binding.lexemesList.apply {
            adapter = LexemeListAdapter(context, lexemes, this@OldLexerDoubleCalculations)
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }

        return binding.root
    }

    private fun <T> debug(action: () -> T): Pair<T, Long> {
        val result: T
        val timeMillis = measureTimeMillis { result = action() }
        return result to timeMillis
    }

    companion object : OldLexerDoubleCalculations(CalculatorFormatter)
}