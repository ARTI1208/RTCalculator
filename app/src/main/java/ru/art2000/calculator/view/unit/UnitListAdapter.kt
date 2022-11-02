package ru.art2000.calculator.view.unit

import android.content.Context
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
import ru.art2000.calculator.model.unit.*
import ru.art2000.calculator.view.unit.BaseUnitPageFragment.Companion.CONVERT_FROM_KEY
import ru.art2000.extensions.arch.launchAndCollect
import ru.art2000.extensions.arch.launchRepeatOnStarted
import ru.art2000.extensions.views.SimpleTextWatcher
import ru.art2000.extensions.getColorAttribute
import com.google.android.material.R as MaterialR

typealias CopyFunction = (CharSequence, CharSequence, CharSequence, CopyMode) -> Boolean

class UnitListAdapter private constructor(
    private val mContext: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val converterFunctions: ConverterFunctions,
    private val copy: CopyFunction,
    private val powerfulConverter: Boolean,
    position: Int,
) : RecyclerView.Adapter<UnitListAdapter.UnitItemHolder>() {

    private val selectedPosition = MutableStateFlow(run {
        val initialValue = if (position >= 0) position
        else converterFunctions.getInt(CONVERT_FROM_KEY, 0)
        Pair(initialValue, initialValue)
    })

    private var currentDimension: Int
        get() = selectedPosition.value.second
        set(dimension) {
            val oldValue = selectedPosition.value.second
            if (oldValue == dimension) return
            val newPair = Pair(oldValue, dimension)
            selectedPosition.value = newPair
        }

    @ColorInt
    private val colorAccent = mContext.getColorAttribute(MaterialR.attr.colorSecondary)

    @ColorInt
    private val colorDefaultBright = mContext.getColorAttribute(MaterialR.attr.colorOnBackground)

    @ColorInt
    private var colorDefaultDimmed = mContext.getColorAttribute(MaterialR.attr.colorOnSurface)

    private var recycler: RecyclerView? = null

    internal constructor(
        ctx: Context,
        lifecycleOwner: LifecycleOwner,
        converterFunctions: ConverterFunctions,
        copy: CopyFunction,
        isPowerfulConverter: Boolean,
    ) : this(ctx, lifecycleOwner, converterFunctions, copy, isPowerfulConverter, -1)

    internal constructor(
        ctx: Context,
        lifecycleOwner: LifecycleOwner,
        converterFunctions: ConverterFunctions,
        copy: CopyFunction,
        pos: Int,
    ) : this(ctx, lifecycleOwner, converterFunctions, copy, false, pos)

    init {
        lifecycleOwner.launchRepeatOnStarted {
            launchAndCollect(selectedPosition) { (_, dimension) ->
                converterFunctions.storeInt(CONVERT_FROM_KEY, dimension)
            }
        }
    }

    fun setValue(position: Int, value: CharSequence) {
        currentDimension = position

        val callback = object : ConverterFunctions.ValueCallback {

            override fun shouldSkip(i: Int) = powerfulConverter && i == position

            override fun onValueUpdated(i: Int, newValueGetter: () -> String) {
                val holder = recycler?.findViewHolderForAdapterPosition(i) as UnitItemHolder?
                    ?: return

                holder.dimensionValueView.text = newValueGetter()
            }

        }

        if (value.isEmpty()) {
            converterFunctions.setValue(position, converterFunctions.defaultValueString, callback)
        } else {
            converterFunctions.setValue(position, value.toString(), callback)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recycler = recyclerView

        fun getItemHolder(position: Int) = recycler?.findViewHolderForAdapterPosition(position)
                as UnitListAdapter.UnitItemHolder?

        lifecycleOwner.launchRepeatOnStarted {
            selectedPosition.collect { pair ->
                if (pair.first == pair.second) return@collect
                setTextColors(getItemHolder(pair.first), false)
                setTextColors(getItemHolder(pair.second), true)
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

    override fun onBindViewHolder(holder: UnitItemHolder, position: Int) {
        holder.bind(position, position == currentDimension)
    }

    override fun onViewAttachedToWindow(holder: UnitItemHolder) {
        super.onViewAttachedToWindow(holder)
        val position = holder.absoluteAdapterPosition
        setTextColors(holder, position == currentDimension)
    }

    override fun getItemCount() = converterFunctions.items.size

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
                    setValue(bindingAdapterPosition, s)
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
            return copy(
                dimensionValueView.text,
                dimensionShortNameView.text, dimensionNameView.text,
                copyMode
            )
        }

        fun bind(position: Int, isSelected: Boolean) {
            val item = converterFunctions.items[position]
            dimensionNameView.setText(item.nameResourceId)
            dimensionShortNameView.setText(item.shortNameResourceId)
            dimensionValueView.text = converterFunctions.displayValue(position)
            setTextColors(this, isSelected)
        }
    }
}