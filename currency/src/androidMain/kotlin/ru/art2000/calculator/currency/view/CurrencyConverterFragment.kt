package ru.art2000.calculator.currency.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import ru.art2000.calculator.common.view.MainScreenFragment
import ru.art2000.calculator.common.view.createThemedSnackbar
import ru.art2000.calculator.currency.R
import ru.art2000.calculator.currency.databinding.CurrencyLayoutBinding
import ru.art2000.calculator.currency.model.LoadingState
import ru.art2000.calculator.currency.vm.CurrencyConverterModel
import ru.art2000.extensions.LocalDate
import ru.art2000.extensions.arch.launchRepeatOnStarted
import ru.art2000.extensions.getColorFromAttribute
import ru.art2000.extensions.kt.launchAndCollect
import ru.art2000.extensions.timeInMillis
import ru.art2000.extensions.views.*
import ru.art2000.calculator.common.R as CommonR

@AndroidEntryPoint
internal class CurrencyConverterFragment : MainScreenFragment(R.layout.currency_layout) {

    private val model by viewModels<CurrencyConverterModel>()
    private val binding by viewBinding(CurrencyLayoutBinding::bind)
    private var currenciesAdapter: CurrencyListAdapter? = null
    private var keyboardListenerSubscription: ListenerSubscription<Boolean>? = null
    private var updateSnackbar: Snackbar? = null

    override val topViews: List<View>
        get() = listOf(binding.root)

    override val bottomViews: List<View>
        get() = if (requireContext().isLandscape) listOf(binding.currencyList) else emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.inflateMenu(R.menu.currencies_converter_menu)
        currenciesAdapter = CurrencyListAdapter(requireContext(), model)

        binding.currencyList.apply {
            layoutManager = OrientationManger(requireContext()) {
                currenciesAdapter?.itemCount.let { it == null || it == 0 }
            }
            adapter = currenciesAdapter
            emptyViewGenerator = { _, _, _ ->
                createTextEmptyView(
                    requireContext(), R.string.empty_text_no_currencies_added
                )
            }
            addOrientationItemDecoration()
        }

        binding.refresher.apply {
            val colorAccent =
                requireContext().getColorFromAttribute(com.google.android.material.R.attr.colorSecondary)
            val circleBackground =
                requireContext().getColorFromAttribute(CommonR.attr.floatingViewBackground)
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
            model.loadTimeIntervals { minDate, maxDate ->

                val minDateMillis = minDate.timeInMillis
                val maxDateMillis = maxDate.timeInMillis

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
                    model.loadData(LocalDate(selection))
                }
                picker.show(requireActivity().supportFragmentManager, "CurrencyDatePicker")
            }
        }
        selectDateMenuItem.setOnLongClickListener {
            model.loadData()
            true
        }

        model.listenDateUpdate()

        launchRepeatOnStarted {
            launchAndCollect(model.loadingState) { applyLoadingState(it) }
            launchAndCollect(model.updateDate) { setCurrenciesUpdateDate(it) }
            launchAndCollect(model.visibleList) { currenciesAdapter?.setNewData(it) }
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

    override fun onShown(previousReplaceable: ru.art2000.extensions.fragments.IReplaceableFragment?) {
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

    override fun onReplaced(nextReplaceable: ru.art2000.extensions.fragments.IReplaceableFragment?) {
        if (updateSnackbar != null) {
            updateSnackbar!!.dismiss()
            updateSnackbar = null
        }
    }

    private fun applyLoadingState(loadingState: LoadingState) {
        setRefreshStatus(loadingState === LoadingState.LOADING_STARTED)
        val messageId = when (loadingState) {
            LoadingState.UNINITIALISED, LoadingState.LOADING_ENDED -> return
            LoadingState.LOADING_STARTED -> R.string.currencies_update_toast
            LoadingState.NETWORK_ERROR -> R.string.currencies_no_internet
            else -> R.string.currencies_update_failed
        }
        Toast.makeText(requireContext(), messageId, Toast.LENGTH_SHORT).show()
    }

    private val titleUpdatedString by lazy { requireContext().getString(R.string.currency_date) }

    private fun setCurrenciesUpdateDate(date: String) {
        binding.toolbar.title = "$titleUpdatedString $date"
    }

    private fun setRefreshStatus(status: Boolean) {
        if (status && binding.refresher.isRefreshing) return
        binding.refresher.isRefreshing = status
    }
}