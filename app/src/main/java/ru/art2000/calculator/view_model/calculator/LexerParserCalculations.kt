package ru.art2000.calculator.view_model.calculator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.CalculatorLexerParseExpertiseViewBinding
import ru.art2000.calculator.model.calculator.AngleType
import ru.art2000.calculator.view.calculator.LexemeListAdapter
import kotlin.system.measureTimeMillis

abstract class LexerParserCalculations<T> : Calculations<T>() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    abstract val parser: CalculationParser<T>

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    abstract val lexer: CalculationLexer<T>

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
            adapter = LexemeListAdapter(context, lexemes, ::format)
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }

        return binding.root
    }

    private fun <T> debug(action: () -> T): Pair<T, Long> {
        val result: T
        val timeMillis = measureTimeMillis { result = action() }
        return result to timeMillis
    }

}