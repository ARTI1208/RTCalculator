package ru.art2000.calculator.currency.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.util.Consumer
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import dev.androidbroadcast.vbpd.viewBinding
import ru.art2000.calculator.currency.R
import ru.art2000.calculator.currency.databinding.ItemAddCurrenciesListBinding
import ru.art2000.calculator.currency.databinding.ModifyCurrenciesLayoutBinding
import ru.art2000.calculator.currency.model.CurrencyItem
import ru.art2000.calculator.currency.model.getNameIdentifier
import ru.art2000.calculator.currency.vm.CurrenciesAddModel
import ru.art2000.calculator.currency.vm.CurrenciesSettingsModel
import ru.art2000.extensions.activities.IEdgeToEdgeFragment
import ru.art2000.extensions.collections.LiveList
import ru.art2000.extensions.collections.LiveList.LiveListObserver
import ru.art2000.extensions.collections.calculateDiff
import ru.art2000.extensions.fragments.CommonReplaceableFragment
import ru.art2000.extensions.views.addOrientationItemDecoration

internal class CurrenciesAddFragment :
    CommonReplaceableFragment(R.layout.modify_currencies_layout), IEdgeToEdgeFragment {

    private val binding by viewBinding(ModifyCurrenciesLayoutBinding::bind)
    private val model: CurrenciesAddModel by activityViewModels<CurrenciesSettingsModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addCurrenciesAdapter = AddCurrenciesAdapter()
        binding.modifyCurrenciesList.apply {
            layoutManager = ru.art2000.extensions.views.OrientationManger(requireContext()) {
                addCurrenciesAdapter.itemCount == 0
            }
            addOrientationItemDecoration()
            emptyViewGenerator = { ctx, _, _ ->
                ru.art2000.extensions.views.createTextEmptyView(ctx, emptyTextRes)
            }
            emptyViewHolderBinder = Consumer { view: View? ->
                val emptyView = view as TextView?
                emptyView?.setText(emptyTextRes)
            }

            adapter = addCurrenciesAdapter
        }
    }

    override val bottomViews: List<View>
        get() = listOf(binding.modifyCurrenciesList)

    override fun onReselected() {
        binding.modifyCurrenciesList.smoothScrollToPosition(0)
    }

    @get:StringRes
    private val emptyTextRes: Int
        get() = if (model.currentQuery.isNotEmpty() && model.hiddenItems.value.isNotEmpty()) {
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
            val holder = binding.modifyCurrenciesList.findViewHolderForAdapterPosition(
                model.displayedHiddenItems.indexOf(currencyItem)
            ) as Holder? ?: return
            holder.checkBox.isChecked = selected
        }

        private fun dispatchListUpdate(oldData: List<CurrencyItem>, newData: List<CurrencyItem>) {
            val result = calculateDiff(oldData, newData,
                { code == it.code }, { position == it.position })
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
                name.setText(currencyItem.getNameIdentifier(name.context))
                checkBox.isChecked = model.isHiddenItemSelected(currencyItem)
            }
        }
    }
}