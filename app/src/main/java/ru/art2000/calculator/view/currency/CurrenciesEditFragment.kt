package ru.art2000.calculator.view.currency

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.ItemAddCurrenciesListBinding
import ru.art2000.calculator.databinding.ItemEditCurrenciesListBinding
import ru.art2000.calculator.databinding.ModifyCurrenciesLayoutBinding
import ru.art2000.calculator.model.currency.CurrencyItem
import ru.art2000.calculator.model.currency.getNameIdentifier
import ru.art2000.calculator.view_model.currency.CurrenciesEditModel
import ru.art2000.calculator.view_model.currency.CurrenciesSettingsModel
import ru.art2000.extensions.collections.LiveList
import ru.art2000.extensions.collections.LiveList.LiveListObserver
import ru.art2000.extensions.collections.calculateDiff
import ru.art2000.extensions.fragments.UniqueReplaceableFragment
import ru.art2000.extensions.views.createTextEmptyView
import ru.art2000.helpers.dip2px

class CurrenciesEditFragment : UniqueReplaceableFragment() {

    private var itemTouchHelper: ItemTouchHelper? = null
    private val binding by viewBinding<ModifyCurrenciesLayoutBinding>(CreateMethod.INFLATE)
    private val model: CurrenciesEditModel by activityViewModels<CurrenciesSettingsModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding.modifyCurrenciesList.setPadding(0, 0, 0, requireContext().dip2px(20f))
        binding.modifyCurrenciesList.emptyViewGenerator = { ctx, _, _ ->
            createTextEmptyView(ctx, emptyTextRes)
        }
        val adapter = EditCurrenciesAdapter()
        binding.modifyCurrenciesList.adapter = adapter
        val llm = LinearLayoutManager(requireContext())
        llm.orientation = RecyclerView.VERTICAL
        binding.modifyCurrenciesList.layoutManager = llm
        itemTouchHelper = ItemTouchHelper(CurrenciesEditRecyclerTouchCallback(
            requireContext(),
            { position ->
                val removedItem = model.displayedVisibleItems[position]
                model.databaseMarkHidden(removedItem)
            }
        ) { firstPosition, secondPosition ->
            adapter.swap(firstPosition, secondPosition)
        }).apply {
            attachToRecyclerView(binding.modifyCurrenciesList)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        itemTouchHelper = null
    }

    override fun onReselected() {
        binding.modifyCurrenciesList.smoothScrollToPosition(0)
    }

    @get:StringRes
    private val emptyTextRes: Int
        get() = R.string.empty_text_no_currencies_added

    override fun getTitle(): Int {
        return R.string.currencies_edit
    }

    companion object {
        private const val REORDER_MODE = 0
        private const val SELECTION_MODE = 1
    }

    private inner class EditCurrenciesAdapter :
        RecyclerView.Adapter<EditCurrenciesAdapter.Holder>() {

        private var curMode = REORDER_MODE

        init {
            model.selectedVisibleItems.observe(
                viewLifecycleOwner,
                object : LiveListObserver<CurrencyItem>() {
                    override fun onItemsInserted(
                        previousList: List<CurrencyItem>,
                        liveList: LiveList<CurrencyItem>,
                        insertedIndices: List<Int>
                    ) {
                        super.onItemsInserted(previousList, liveList, insertedIndices)
                        for (i in insertedIndices) {
                            markItem(liveList[i], true)
                        }
                    }

                    override fun onItemsRemoved(
                        previousList: List<CurrencyItem>,
                        removedIndices: List<Int>
                    ) {
                        super.onItemsRemoved(previousList, removedIndices)
                        if (model.selectedVisibleItems.isEmpty()) {
                            setReorderMode()
                        }
                        for (i in removedIndices) {
                            markItem(previousList[i], false)
                        }
                    }
                })
            model.displayedVisibleItems.observe(
                viewLifecycleOwner,
                object : LiveListObserver<CurrencyItem>() {
                    override fun onAnyChanged(previousList: List<CurrencyItem>) {
                        dispatchListUpdate(previousList, model.displayedVisibleItems)
                    }
                })
        }

        override fun getItemViewType(position: Int) = curMode

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val inflater = LayoutInflater.from(requireContext())
            return when (viewType) {
                SELECTION_MODE -> Holder(
                    ItemAddCurrenciesListBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
                REORDER_MODE -> Holder(
                    ItemEditCurrenciesListBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
                else -> throw IllegalArgumentException("Unknown viewType '$viewType'")
            }
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val currencyItem = model.displayedVisibleItems[position]
            holder.bind(currencyItem)
        }

        override fun getItemCount() = model.displayedVisibleItems.size

        fun swap(position: Int, anotherPosition: Int) {
            if (position < 0 || anotherPosition < 0 || position >= itemCount || anotherPosition >= itemCount) {
                return
            }
            val item = model.displayedVisibleItems[position]
            val anotherItem = model.displayedVisibleItems[anotherPosition]
            item.position = anotherPosition
            anotherItem.position = position
            val map = buildMap {
                this[item] = anotherItem
                this[anotherItem] = item
            }
            model.displayedVisibleItems.replaceAll(map)
        }

        fun dispatchListUpdate(oldData: List<CurrencyItem>, newData: List<CurrencyItem>) {
            val result = calculateDiff(oldData, newData)
            result.dispatchUpdatesTo(this)
        }

        private fun setReorderMode() {
            if (curMode == REORDER_MODE) return
            curMode = REORDER_MODE
            model.isEditSelectionMode = false
            itemTouchHelper!!.attachToRecyclerView(binding.modifyCurrenciesList)
            model.selectedVisibleItems.clear()
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun setSelectionMode(holder: RecyclerView.ViewHolder) {
            curMode = SELECTION_MODE
            model.isEditSelectionMode = true
            itemTouchHelper!!.attachToRecyclerView(null)
            model.dismissFirstTimeTooltip()
            val p = holder.bindingAdapterPosition
            val item = model.displayedVisibleItems[p]
            model.setVisibleItemSelected(item, true)
            notifyDataSetChanged()
        }

        private fun markItem(currencyItem: CurrencyItem, selected: Boolean) {
            val holder = binding.modifyCurrenciesList.findViewHolderForAdapterPosition(
                model.displayedVisibleItems.indexOf(currencyItem)
            ) as Holder? ?: return
            holder.checkBox?.isChecked = selected
        }

        inner class Holder private constructor(
            root: View,
            val code: TextView,
            val name: TextView,
            val checkBox: CheckBox?,
        ) : RecyclerView.ViewHolder(root) {

            constructor(binding: ItemAddCurrenciesListBinding) : this(
                binding.root,
                binding.currencyCode, binding.currencyName,
                binding.checkboxAdd,
            ) {
                binding.checkboxAdd.apply {
                    setOnClickListener {
                        val item = model.displayedVisibleItems[bindingAdapterPosition]
                        if (isChecked != model.isVisibleItemSelected(item)) {
                            model.setVisibleItemSelected(item, isChecked)
                        }
                    }
                }

                itemView.setOnClickListener { binding.checkboxAdd.performClick() }
            }

            @SuppressLint("ClickableViewAccessibility")
            constructor(binding: ItemEditCurrenciesListBinding) : this(
                binding.root,
                binding.currencyCode, binding.currencyName,
                null,
            ) {
                binding.handle.setOnTouchListener { _, event ->
                    if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                        itemTouchHelper!!.startDrag(this)
                    }
                    false
                }
                binding.root.setOnLongClickListener {
                    setSelectionMode(this)
                    false
                }
            }

            fun bind(currencyItem: CurrencyItem) {
                code.text = currencyItem.code
                name.setText(currencyItem.getNameIdentifier(requireContext()))
                checkBox?.isChecked = model.isVisibleItemSelected(currencyItem)
            }
        }
    }
}