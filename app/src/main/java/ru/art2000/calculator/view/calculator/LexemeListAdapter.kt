package ru.art2000.calculator.view.calculator

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.art2000.calculator.databinding.CalculatorLexemeItemBinding
import ru.art2000.calculator.model.calculator.parts.ExpressionPart
import ru.art2000.calculator.model.calculator.parts.ExpressionValue
import ru.art2000.calculator.view_model.calculator.Calculations

class LexemeListAdapter<CN>(
        private val mContext: Context,
        private val lexemes: List<ExpressionPart<*>>,
        private val calculations: Calculations<CN>,
) : RecyclerView.Adapter<LexemeListAdapter.LexemeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LexemeViewHolder {
        val layoutInflater = LayoutInflater.from(mContext)
        val binding = CalculatorLexemeItemBinding.inflate(layoutInflater)

        return LexemeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LexemeViewHolder, position: Int) {
        val lexeme = lexemes[position]

        holder.lexemeTextView.text = if (lexeme is ExpressionValue<*>)
            calculations.format(lexeme.value as CN)
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