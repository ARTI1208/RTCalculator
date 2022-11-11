package ru.art2000.calculator.calculator.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.art2000.calculator.calculator.computation.parts.ExpressionPart
import ru.art2000.calculator.calculator.computation.parts.ExpressionValue
import ru.art2000.calculator.calculator.databinding.CalculatorLexemeItemBinding

internal class LexemeListAdapter<CN>(
    private val mContext: Context,
    private val lexemes: List<ExpressionPart<CN>>,
    private val numberFormatter: (CN) -> String,
) : RecyclerView.Adapter<LexemeListAdapter.LexemeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LexemeViewHolder {
        val layoutInflater = LayoutInflater.from(mContext)
        val binding = CalculatorLexemeItemBinding.inflate(layoutInflater)

        return LexemeViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LexemeViewHolder, position: Int) {
        val lexeme = lexemes[position]

        holder.lexemeTextView.text = if (lexeme is ExpressionValue<CN>)
            numberFormatter(lexeme.value)
        else
            lexeme.partAsString()
        holder.lexemeTypeView.text = "[${lexeme.javaClass.simpleName}]"
    }

    override fun getItemCount(): Int = lexemes.size

    class LexemeViewHolder(
        val binding: CalculatorLexemeItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        val lexemeTextView = binding.lexemeText

        val lexemeTypeView = binding.lexemeType

    }
}