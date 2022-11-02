package ru.art2000.calculator.view.currency

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.ItemCurrencyConverterListBinding
import ru.art2000.calculator.model.currency.CurrencyItem
import ru.art2000.calculator.model.currency.getNameIdentifier
import ru.art2000.calculator.view_model.currency.CurrencyListAdapterModel
import ru.art2000.extensions.collections.calculateDiff
import ru.art2000.extensions.views.SimpleTextWatcher
import ru.art2000.extensions.getColorAttribute
import java.text.DecimalFormat
import com.google.android.material.R as MaterialR

class CurrencyListAdapter internal constructor(
    private val mContext: Context,
    private val adapterModel: CurrencyListAdapterModel
) : RecyclerView.Adapter<CurrencyListAdapter.Holder>() {
    @ColorInt
    private val colorAccent = mContext.getColorAttribute(MaterialR.attr.colorSecondary)

    @ColorInt
    private val colorDefaultBright = mContext.getColorAttribute(MaterialR.attr.colorOnBackground)

    @ColorInt
    private val colorDefaultDimmed = mContext.getColorAttribute(MaterialR.attr.colorOnSurface)
    private val codeTextSizeNormal =
        mContext.resources.getDimension(R.dimen.currency_list_item_code_normal)
    private val codeTextSizeHighlighted =
        mContext.resources.getDimension(R.dimen.currency_list_item_code_highlight)
    private val valueTextSizeNormal =
        mContext.resources.getDimension(R.dimen.currency_list_item_value_normal)
    private var data: List<CurrencyItem> = emptyList()
    private var recycler: RecyclerView? = null

    private val dot2dig = DecimalFormat("#.##")

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recycler = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val binding = ItemCurrencyConverterListBinding.inflate(
            LayoutInflater.from(mContext), parent, false
        )

        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if (adapterModel.lastInputItemPosition == position) {
            holder.highlightElements()
        } else {
            removeEditText(holder)
            holder.removeElementsHighlighting()
        }
        val currencyItem = data[position]
        holder.bind(currencyItem)
    }

    override fun getItemCount() = data.size

    fun setNewData(newData: List<CurrencyItem>) {
        if (adapterModel.lastInputItemPosition == -1) {
            for (i in newData.indices) {
                val newItem = newData[i]
                if (newItem.code == adapterModel.savedInputItemCode) {
                    adapterModel.lastInputItemPosition = i
                    break
                }
            }
        }
        if (data.isEmpty()) {
            data = newData
            notifyItemRangeInserted(0, newData.size)
        } else {
            val result = calculateDiff(data, newData,
                { code == it.code }, { position == it.position && rate == it.rate })
            data = newData
            result.dispatchUpdatesTo(this)
        }
    }

    fun removeEditText() {
        if (data.isEmpty()) return
        val holder =
            recycler!!.findViewHolderForAdapterPosition(adapterModel.lastInputItemPosition) as Holder?
        removeEditText(holder)
    }

    private fun removeEditText(holder: Holder?) {
        if (holder == null) return
        holder.removeEditText()
        if (holder.bindingAdapterPosition != adapterModel.lastInputItemPosition) return
        val imm = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(recycler!!.windowToken, 0)
    }

    inner class Holder internal constructor(binding: ItemCurrencyConverterListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val codeView = binding.currencyCode
        private val nameView = binding.currencyName
        private val value = binding.currencyValue
        private val input = binding.currencyInputValue

        init {
            itemView.setOnClickListener {
                val holderPosition = bindingAdapterPosition
                if (holderPosition != adapterModel.lastInputItemPosition) {
                    val previousHolder =
                        recycler!!.findViewHolderForAdapterPosition(adapterModel.lastInputItemPosition) as Holder?
                    previousHolder?.removeElementsHighlighting()
                    adapterModel.lastInputItemPosition = bindingAdapterPosition
                    highlightElements()
                }
                value.visibility = View.GONE
                input.visibility = View.VISIBLE
                input.isEnabled = true
                input.requestFocus()
                val keyboard =
                    mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboard.showSoftInput(input, 0)
            }
            input.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    removeEditText(this)
                }
            }
            input.addTextChangedListener(object : SimpleTextWatcher() {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.isNotEmpty()) {
                        val holderPosition = bindingAdapterPosition
                        adapterModel.lastInputItemValue =
                            s.toString().toDouble() / data[holderPosition].rate
                        for (i in 0 until itemCount) {
                            val holder = recycler!!.findViewHolderForAdapterPosition(i) as Holder?
                                ?: continue
                            val item = data[i]
                            holder.value.text = dot2dig.format(
                                adapterModel.lastInputItemValue * item.rate
                            )
                        }
                    }
                }

                override fun afterTextChanged(s: Editable) {
                    adapterModel.saveConversionIfNeeded(codeView.text.toString())
                }
            })
        }

        fun bind(currencyItem: CurrencyItem) {
            value.text =
                dot2dig.format(adapterModel.lastInputItemValue * currencyItem.rate)
            codeView.text = currencyItem.code
            nameView.setText(currencyItem.getNameIdentifier(mContext))
        }

        fun highlightElements() {
            codeView.setTextColor(colorAccent)
            nameView.setTextColor(colorAccent)
            value.setTextColor(colorAccent)
            codeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, codeTextSizeHighlighted)
            codeView.setTypeface(null, Typeface.BOLD)
            value.setTextSize(TypedValue.COMPLEX_UNIT_PX, codeTextSizeHighlighted)
            value.setTypeface(null, Typeface.BOLD)
        }

        fun removeElementsHighlighting() {
            codeView.setTextColor(colorDefaultBright)
            nameView.setTextColor(colorDefaultDimmed)
            value.setTextColor(colorDefaultDimmed)
            codeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, codeTextSizeNormal)
            codeView.setTypeface(null, Typeface.NORMAL)
            value.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueTextSizeNormal)
            value.setTypeface(null, Typeface.NORMAL)
        }

        fun removeEditText() {
            value.visibility = View.VISIBLE
            input.visibility = View.GONE
            input.isEnabled = false
            input.text.clear()
            input.clearFocus()
        }
    }

}