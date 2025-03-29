package ru.art2000.calculator.currency.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.art2000.calculator.currency.R
import ru.art2000.calculator.currency.databinding.ItemAddCurrenciesListBinding
import ru.art2000.calculator.currency.databinding.ItemEditCurrenciesListBinding
import ru.art2000.calculator.currency.databinding.ModifyCurrenciesLayoutBinding
import ru.art2000.calculator.currency.model.CurrencyItem
import ru.art2000.calculator.currency.model.getNameIdentifier
import ru.art2000.calculator.currency.vm.CurrenciesEditModel
import ru.art2000.calculator.currency.vm.CurrenciesSettingsModel
import ru.art2000.extensions.activities.IEdgeToEdgeFragment
import ru.art2000.extensions.activities.consumeInsetsForMargin
import ru.art2000.extensions.activities.getInt
import ru.art2000.extensions.collections.LiveList
import ru.art2000.extensions.collections.LiveList.LiveListObserver
import ru.art2000.extensions.collections.calculateDiff
import ru.art2000.extensions.fragments.CommonReplaceableFragment
import ru.art2000.extensions.layout.isLtr
import ru.art2000.extensions.views.*

internal class CurrenciesEditFragment : CommonReplaceableFragment(), IEdgeToEdgeFragment {

    private var itemTouchHelper: ItemTouchHelper? = null
    private val binding by viewBinding<ModifyCurrenciesLayoutBinding>(CreateMethod.INFLATE)
    private val model: CurrenciesEditModel by activityViewModels<CurrenciesSettingsModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding.modifyCurrenciesList.apply {
            emptyViewGenerator = { ctx, _, _ ->
                createTextEmptyView(ctx, emptyTextRes)
            }
            val editCurrenciesAdapter = EditCurrenciesAdapter(
                savedInstanceState.getInt(ADAPTER_MODE_KEY, REORDER_MODE)
            )
            adapter = editCurrenciesAdapter
            layoutManager = OrientationManger(requireContext()) {
                editCurrenciesAdapter.itemCount == 0
            }
            addOrientationItemDecoration()
            itemTouchHelper = ItemTouchHelper(CurrenciesEditRecyclerTouchCallback(
                requireContext(),
                requireContext().isLandscape,
                { position ->
                    val removedItem = model.displayedVisibleItems[position]
                    model.databaseMarkHidden(removedItem)
                }
            ) { firstPosition, secondPosition ->
                editCurrenciesAdapter.swap(firstPosition, secondPosition)
            }).also {
                it.attachToRecyclerView(this)
            }
        }

        return binding.root
    }

    override val bottomViews: List<View>
        get() = listOf(binding.modifyCurrenciesList)

    override fun onDestroyView() {
        super.onDestroyView()
        itemTouchHelper = null
    }

    override fun onReselected() {
        binding.modifyCurrenciesList.smoothScrollToPosition(0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ADAPTER_MODE_KEY, adapter.currentMode)
    }

    private val adapter: EditCurrenciesAdapter
        get() = binding.modifyCurrenciesList.actualAdapter as EditCurrenciesAdapter

    @get:StringRes
    private val emptyTextRes: Int
        get() = R.string.empty_text_no_currencies_added

    companion object {
        private const val ADAPTER_MODE_KEY = "adapterMode"
        private const val REORDER_MODE = 0
        private const val SELECTION_MODE = 1
    }

    private inner class EditCurrenciesAdapter(
        var currentMode: Int
    ) : RecyclerView.Adapter<EditCurrenciesAdapter.Holder>() {

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

        override fun getItemViewType(position: Int) = currentMode

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
            model.swapPositions(item, anotherItem)
        }

        fun dispatchListUpdate(oldData: List<CurrencyItem>, newData: List<CurrencyItem>) {
            val result = calculateDiff(oldData, newData, { code == it.code }, { true })
            result.dispatchUpdatesTo(this)
        }

        private fun setReorderMode() {
            if (currentMode == REORDER_MODE) return
            currentMode = REORDER_MODE
            model.isEditSelectionMode = false
            itemTouchHelper!!.attachToRecyclerView(binding.modifyCurrenciesList)
            model.selectedVisibleItems.clear()
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun setSelectionMode(holder: RecyclerView.ViewHolder) {
            currentMode = SELECTION_MODE
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

                binding.handle.consumeInsetsForMargin { windowInsetsCompat, left, _, right, _ ->
                    val gestureInsets =
                        windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemGestures())
                    updateLayoutParams<MarginLayoutParams> {
                        if (isLtr) {
                            updateMargins(
                                right = right + gestureInsets.right,
                            )
                        } else {
                            updateMargins(
                                left = left + gestureInsets.left,
                            )
                        }
                    }
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