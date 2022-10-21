package ru.art2000.calculator.view.currency

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.datepicker.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.CurrencyLayoutBinding
import ru.art2000.calculator.model.currency.LoadingState
import ru.art2000.calculator.view.MainScreenFragment
import ru.art2000.calculator.view_model.currency.CurrencyConverterModel
import ru.art2000.extensions.fragments.IReplaceableFragment
import ru.art2000.extensions.views.ListenerSubscription
import ru.art2000.extensions.views.addImeVisibilityListener
import ru.art2000.extensions.views.createTextEmptyView
import ru.art2000.extensions.views.createThemedSnackbar
import ru.art2000.helpers.getColorAttribute
import java.util.*

@AndroidEntryPoint
class CurrencyConverterFragment : MainScreenFragment() {

    private val model by viewModels<CurrencyConverterModel>()
    private val binding by viewBinding<CurrencyLayoutBinding>(CreateMethod.INFLATE)
    private var currenciesAdapter: CurrencyListAdapter? = null
    private var keyboardListenerSubscription: ListenerSubscription<Boolean>? = null
    private var updateSnackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding.toolbar.inflateMenu(R.menu.currencies_converter_menu)
        currenciesAdapter = CurrencyListAdapter(requireContext(), model)

        binding.currencyList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = currenciesAdapter
            emptyViewGenerator = { _: Context, _: ViewGroup, _: Int ->
                createTextEmptyView(
                    requireContext(), R.string.empty_text_no_currencies_added
                )
            }
        }

        binding.refresher.apply {
            val colorAccent =
                requireContext().getColorAttribute(com.google.android.material.R.attr.colorSecondary)
            val circleBackground =
                requireContext().getColorAttribute(R.attr.floatingViewBackground)
            setProgressBackgroundColorSchemeColor(circleBackground)
            setColorSchemeColors(colorAccent)
            setProgressViewEndTarget(true, progressViewEndOffset)
            setOnRefreshListener { model.loadData() }
        }

        val editMenuItem =
            binding.root.findViewById<ActionMenuItemView>(R.id.edit_currencies)
        editMenuItem.setOnClickListener {
            val intent = Intent(activity, CurrenciesSettingsActivity::class.java)
            startActivity(intent)
        }
        val selectDateMenuItem =
            binding.root.findViewById<ActionMenuItemView>(R.id.select_date)
        selectDateMenuItem.setOnClickListener {
            model.loadTimeIntervals { minDateMillis, maxDateMillis ->
                val minValidator = DateValidatorPointForward.from(minDateMillis)
                val maxValidator = DateValidatorPointBackward.before(maxDateMillis)

                val picker = MaterialDatePicker.Builder
                    .datePicker()
                    .setCalendarConstraints(
                        CalendarConstraints.Builder()
                            .setStart(minDateMillis)
                            .setEnd(maxDateMillis)
                            .setValidator(
                                CompositeDateValidator.allOf(
                                    listOf(minValidator, maxValidator)
                                )
                            ).build()
                    )
                    .build()
                picker.addOnPositiveButtonClickListener { selection ->
                    model.loadData(selection)
                }
                picker.show(requireActivity().supportFragmentManager, "CurrencyDatePicker")
            }
        }
        selectDateMenuItem.setOnLongClickListener {
            model.loadData()
            true
        }

        model.listenDateUpdate()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.loadingState.observe(viewLifecycleOwner) {
            applyLoadingState(it)
        }
        model.updateDate.observe(viewLifecycleOwner) {
            setCurrenciesUpdateDate(it)
        }
        model.visibleList.observe(viewLifecycleOwner) {
            currenciesAdapter?.setNewData(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        model.stopListeningDateUpdate()
        currenciesAdapter = null
    }

    override fun onResume() {
        super.onResume()
        keyboardListenerSubscription =
            binding.root.addImeVisibilityListener { isVisible: Boolean ->
                if (!isVisible) {
                    currenciesAdapter?.removeEditText()
                }
            }
    }

    override fun onPause() {
        super.onPause()
        keyboardListenerSubscription?.apply {
            invoke(false)
            keyboardListenerSubscription = null
        }
    }

    override fun onReselected() {
        binding.currencyList.smoothScrollToPosition(0)
    }

    override fun onShown(previousReplaceable: IReplaceableFragment?) {
        if (!model.isFirstUpdateDone) {
            if (model.isUpdateOnFirstTabOpenEnabled()) {
                model.loadData()
            } else {
                updateSnackbar = binding.currencyList.createThemedSnackbar(
                    R.string.message_manually_update,
                    Snackbar.LENGTH_INDEFINITE
                ).apply {
                    setAction(R.string.action_update) { model.loadData() }
                    show()
                }
            }
        }
    }

    override fun onReplaced(nextReplaceable: IReplaceableFragment?) {
        if (updateSnackbar != null) {
            updateSnackbar!!.dismiss()
            updateSnackbar = null
        }
    }

    override fun getReplaceableId(): Int {
        return R.id.navigation_currency
    }

    override fun getIcon(): Int {
        return R.drawable.ic_currency
    }

    override fun getTitle(): Int {
        return R.string.title_currency
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun applyLoadingState(loadingState: LoadingState) {
        setRefreshStatus(loadingState === LoadingState.LOADING_STARTED)
        if (loadingState === LoadingState.UNINITIALISED) return
        if (loadingState === LoadingState.LOADING_ENDED) {
            currenciesAdapter?.notifyDataSetChanged()
            return
        }
        val messageId = when (loadingState) {
            LoadingState.LOADING_STARTED -> R.string.currencies_update_toast
            LoadingState.NETWORK_ERROR -> R.string.currencies_no_internet
            else -> R.string.currencies_update_failed
        }
        Toast.makeText(requireContext(), messageId, Toast.LENGTH_SHORT).show()
    }

    private fun setCurrenciesUpdateDate(date: String) {
        binding.toolbar.title = model.titleUpdatedString + " " + date
    }

    private fun setRefreshStatus(status: Boolean) {
        if (status && binding.refresher.isRefreshing) return
        binding.refresher.isRefreshing = status
    }
}