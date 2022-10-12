package ru.art2000.calculator.view.currency

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.util.Consumer
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.ItemAddCurrenciesListBinding
import ru.art2000.calculator.databinding.ModifyCurrenciesLayoutBinding
import ru.art2000.calculator.model.currency.CurrencyItem
import ru.art2000.calculator.view_model.currency.CurrenciesAddModel
import ru.art2000.calculator.view_model.currency.CurrenciesSettingsModel
import ru.art2000.calculator.view_model.currency.CurrencyDependencies.getNameIdentifierForCode
import ru.art2000.extensions.collections.LiveList
import ru.art2000.extensions.collections.LiveList.LiveListObserver
import ru.art2000.extensions.collections.calculateDiff
import ru.art2000.extensions.fragments.UniqueReplaceableFragment
import ru.art2000.extensions.views.createTextEmptyView

class CurrenciesAddFragment : UniqueReplaceableFragment() {

    private var viewBinding: ModifyCurrenciesLayoutBinding? = null
    private val model: CurrenciesAddModel by activityViewModels<CurrenciesSettingsModel>()

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (viewBinding == null) {
            val binding = ModifyCurrenciesLayoutBinding.inflate(inflater)
            viewBinding = binding
            model.recyclerViewBottomPadding.observe(viewLifecycleOwner) { bottomPadding ->
                binding.modifyCurrenciesList.setPadding(
                    0,
                    0,
                    0,
                    bottomPadding
                )
            }
            val llm = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
            binding.modifyCurrenciesList.layoutManager = llm
            binding.modifyCurrenciesList.emptyViewGenerator = { ctx, _, _ ->
                createTextEmptyView(ctx, emptyTextRes)
            }
            binding.modifyCurrenciesList.emptyViewHolderBinder = Consumer { view: View? ->
                val emptyView = view as TextView?
                emptyView?.setText(emptyTextRes)
            }

            binding.modifyCurrenciesList.adapter = AddCurrenciesAdapter()
        }
        return viewBinding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    override fun onReselected() {
        if (viewBinding != null) {
            viewBinding!!.modifyCurrenciesList.smoothScrollToPosition(0)
        }
    }

    override fun getTitle(): Int {
        return R.string.currencies_add
    }

    @get:StringRes
    private val emptyTextRes: Int
        get() = if (model.currentQuery.isNotEmpty() && model.hiddenItems.value!!.isNotEmpty()) {
            R.string.empty_text_no_currencies_found
        } else {
            R.string.empty_text_all_currencies_added
        }

    private inner class AddCurrenciesAdapter : RecyclerView.Adapter<AddCurrenciesAdapter.Holder>() {

        init {
            model.displayedHiddenItems.observe(
                viewLifecycleOwner,
                object : LiveListObserver<CurrencyItem>() {
                    override fun onAnyChanged(previousList: List<CurrencyItem>) {
                        dispatchListUpdate(previousList, model.displayedHiddenItems)
                    }
                })
            model.selectedHiddenItems.observe(
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
                        for (i in removedIndices) {
                            markItem(previousList[i], false)
                        }
                    }
                })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val binding = ItemAddCurrenciesListBinding.inflate(
                LayoutInflater.from(requireContext()), parent, false
            )
            return Holder(binding)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val currencyItem = model.displayedHiddenItems[position]
            holder.bind(currencyItem)
        }

        override fun getItemCount() = model.displayedHiddenItems.size

        private fun markItem(currencyItem: CurrencyItem, selected: Boolean) {
            val holder = viewBinding!!.modifyCurrenciesList.findViewHolderForAdapterPosition(
                model.displayedHiddenItems.indexOf(currencyItem)
            ) as Holder? ?: return
            holder.checkBox.isChecked = selected
        }

        private fun dispatchListUpdate(oldData: List<CurrencyItem>, newData: List<CurrencyItem>) {
            val result = calculateDiff(oldData, newData)
            result.dispatchUpdatesTo(this)
        }

        private inner class Holder(viewBinding: ItemAddCurrenciesListBinding) :
            RecyclerView.ViewHolder(viewBinding.root) {

            val code = viewBinding.currencyCode
            val name = viewBinding.currencyName
            val checkBox = viewBinding.checkboxAdd

            init {
                itemView.setOnClickListener { checkBox.performClick() }
                checkBox.setOnClickListener {
                    val pos = bindingAdapterPosition
                    val item = model.displayedHiddenItems[pos]
                    if (checkBox.isChecked != model.isHiddenItemSelected(item)) {
                        model.setHiddenItemSelected(item, checkBox.isChecked)
                    }
                }
            }

            fun bind(currencyItem: CurrencyItem) {
                code.text = currencyItem.code
                name.setText(getNameIdentifierForCode(name.context, currencyItem.code))
                checkBox.isChecked = model.isHiddenItemSelected(currencyItem)
            }
        }
    }
}