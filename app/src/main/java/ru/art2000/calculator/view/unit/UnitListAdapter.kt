package ru.art2000.calculator.view.unit

import android.content.Context
import android.util.Pair
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.view.View.OnCreateContextMenuListener
import android.view.View.OnFocusChangeListener
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.MutableStateFlow
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.ItemUnitConverterListBinding
import ru.art2000.calculator.databinding.ItemUnitConverterListPowerfulBinding
import ru.art2000.calculator.databinding.ItemUnitConverterNamePartBinding
import ru.art2000.calculator.model.unit.CopyMode
import ru.art2000.calculator.model.unit.UnitConverterItem
import ru.art2000.calculator.view.unit.UnitListAdapter.UnitItemHolder
import ru.art2000.calculator.view_model.unit.UnitConverterModel
import ru.art2000.extensions.arch.launchRepeatOnStarted
import ru.art2000.extensions.views.SimpleTextWatcher
import ru.art2000.helpers.getColorAttribute

class UnitListAdapter private constructor(
    private val mContext: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val data: Array<UnitConverterItem<Double>>,
    private val model: UnitConverterModel,
    private val powerfulConverter: Boolean,
    position: Int,
) : RecyclerView.Adapter<UnitItemHolder>() {

    private val selectedPosition = MutableStateFlow(Pair(0, 0))

    private var currentDimension: Int
        get() = selectedPosition.value.second
        set(dimension) {
            val oldValue = selectedPosition.value.second
            if (oldValue == dimension) return
            val newPair = Pair(oldValue, dimension)
            selectedPosition.value = newPair
        }

    @ColorInt
    private val colorAccent: Int
    @ColorInt
    private val colorDefaultBright: Int
    @ColorInt
    private var colorDefaultDimmed: Int

    private var recycler: RecyclerView? = null

    internal constructor(
        ctx: Context,
        lifecycleOwner: LifecycleOwner,
        items: Array<UnitConverterItem<Double>>,
        model: UnitConverterModel,
        isPowerfulConverter: Boolean
    ) : this(ctx, lifecycleOwner, items, model, isPowerfulConverter, 0) {
        if (data.isNotEmpty() && data[0].currentValue == 0.0) setValue(0, 1.0)
    }

    internal constructor(
        ctx: Context,
        lifecycleOwner: LifecycleOwner,
        items: Array<UnitConverterItem<Double>>,
        model: UnitConverterModel,
        pos: Int
    ) : this(ctx, lifecycleOwner, items, model, false, pos)

    init {
        currentDimension = position
        colorAccent = mContext.getColorAttribute(com.google.android.material.R.attr.colorSecondary)
        colorDefaultBright =
            mContext.getColorAttribute(com.google.android.material.R.attr.colorOnBackground)
        colorDefaultDimmed =
            mContext.getColorAttribute(com.google.android.material.R.attr.colorOnSurface)
    }

    fun setValue(position: Int, value: Double, updatePosition: Boolean) {
        if (updatePosition) {
            currentDimension = position
        }
        val from = data[position]
        from.setValue(value)
        repeat(itemCount) { i ->
            if (powerfulConverter && i == position) // don't skip for half-powerful to correctly recalculate
                return@repeat
            val convertedValue = data[i].convert(from)
            if (recycler == null) return@repeat
            val holder = recycler!!.findViewHolderForAdapterPosition(i) as UnitItemHolder?
            if (holder != null) {
                val v = doubleToString(convertedValue)
                holder.dimensionValueView.text = v
            }
        }
    }

    fun setValue(position: Int, value: Double) {
        setValue(position, value, true)
    }

    fun setValue(position: Int, value: String) {
        val result = model.calculations.calculate(value)
        setValue(position, result ?: 1.0)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recycler = recyclerView
        lifecycleOwner.launchRepeatOnStarted {
            selectedPosition.collect { pair ->
                if (pair.first == pair.second) return@collect
                setTextColors(
                    recycler?.findViewHolderForAdapterPosition(pair.first) as UnitItemHolder?,
                    false
                )
                setTextColors(
                    recycler?.findViewHolderForAdapterPosition(pair.second) as UnitItemHolder?,
                    true
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitItemHolder {
        val inflater = LayoutInflater.from(mContext)
        return if (powerfulConverter) {
            UnitItemHolder(
                ItemUnitConverterListPowerfulBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
        } else UnitItemHolder(
            ItemUnitConverterListBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }

    private fun setTextColors(holder: UnitItemHolder?, isSelected: Boolean) {
        if (holder == null) return
        if (isSelected) {
            holder.dimensionShortNameView.setTextColor(colorAccent)
            holder.dimensionNameView.setTextColor(colorAccent)
            holder.dimensionValueView.setTextColor(colorAccent)
        } else {
            holder.dimensionShortNameView.setTextColor(colorDefaultBright)
            holder.dimensionNameView.setTextColor(colorDefaultDimmed)
            holder.dimensionValueView.setTextColor(colorDefaultDimmed)
        }
    }

    private fun doubleToString(d: Double): String {
        return model.calculations.format(d)
    }

    override fun onBindViewHolder(holder: UnitItemHolder, position: Int) {
        holder.bind(data[position], position == currentDimension)
    }

    override fun onViewAttachedToWindow(holder: UnitItemHolder) {
        super.onViewAttachedToWindow(holder)
        val position = holder.absoluteAdapterPosition
        setTextColors(holder, position == currentDimension)
        setValue(position, data[position].currentValue, false)
    }

    override fun getItemCount() = data.size

    inner class UnitItemHolder : RecyclerView.ViewHolder, OnCreateContextMenuListener {
        val dimensionValueView: TextView
        val dimensionNameView: TextView
        val dimensionShortNameView: TextView

        internal constructor(binding: ItemUnitConverterListBinding) : super(binding.root) {
            dimensionValueView = binding.value
        }

        internal constructor(binding: ItemUnitConverterListPowerfulBinding) : super(binding.root) {
            dimensionValueView = binding.value
            binding.value.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) return@OnFocusChangeListener
                currentDimension = bindingAdapterPosition
            }
            val textWatcher = object : SimpleTextWatcher() {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (!binding.value.hasFocus()) return
                    updateValue(s)
                }
            }
            binding.value.addTextChangedListener(textWatcher)
        }

        init {
            itemView.setOnCreateContextMenuListener(this)
            val nameBinding = ItemUnitConverterNamePartBinding.bind(itemView)
            dimensionNameView = nameBinding.type
            dimensionShortNameView = nameBinding.typeShort
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
            menu.setHeaderTitle(mContext.getString(R.string.you_can))
            menu
                .add(Menu.NONE, 0, Menu.NONE, R.string.context_menu_copy_value)
                .setOnMenuItemClickListener { copy(CopyMode.VALUE_ONLY) }
            menu
                .add(Menu.NONE, 0, Menu.NONE, R.string.context_menu_copy_with_short_name)
                .setOnMenuItemClickListener { copy(CopyMode.VALUE_AND_SHORT_NAME) }
            menu
                .add(Menu.NONE, 0, Menu.NONE, R.string.context_menu_copy_with_full_name)
                .setOnMenuItemClickListener { copy(CopyMode.VALUE_AND_FULL_NAME) }
        }

        private fun copy(copyMode: CopyMode): Boolean {
            return model.copy(
                mContext, dimensionValueView.text,
                dimensionShortNameView.text, dimensionNameView.text,
                copyMode
            )
        }

        fun bind(item: UnitConverterItem<Double>, isSelected: Boolean) {
            dimensionNameView.setText(item.nameResourceId)
            dimensionShortNameView.setText(item.shortNameResourceId)
            dimensionValueView.text = doubleToString(item.currentValue)
            setTextColors(this, isSelected)
        }

        fun updateValue(text: CharSequence) {
            if (text.isEmpty()) {
                setValue(bindingAdapterPosition, 1.0)
            } else {
                setValue(bindingAdapterPosition, text.toString())
            }
        }
    }
}