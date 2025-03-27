package ru.art2000.calculator.calculator.view

import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sothree.slidinguppanel.PanelSlideListener
import com.sothree.slidinguppanel.PanelState
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.datetime.toJavaLocalDate
import ru.art2000.calculator.calculator.R
import ru.art2000.calculator.calculator.databinding.CalculatorLayoutBinding
import ru.art2000.calculator.calculator.databinding.HistoryLayoutBinding
import ru.art2000.calculator.calculator.model.HistoryDateItem
import ru.art2000.calculator.calculator.model.HistoryValueItem
import ru.art2000.calculator.calculator.vm.CalculatorModel
import ru.art2000.calculator.common.view.MainScreenFragment
import ru.art2000.extensions.arch.launchRepeatOnStarted
import ru.art2000.extensions.kt.launchAndCollect
import ru.art2000.extensions.views.CustomInputEditText.OnSelectionChangedListener
import ru.art2000.extensions.views.addOrientationItemDecoration
import ru.art2000.extensions.views.autoScrollOnInput
import ru.art2000.extensions.views.isLandscape
import ru.art2000.extensions.views.toViewString
import java.util.*
import ru.art2000.calculator.common.R as CommonR

@AndroidEntryPoint
internal class CalculatorFragment : MainScreenFragment() {

    private val model by viewModels<CalculatorModel>()
    private val binding by viewBinding<CalculatorLayoutBinding>(CreateMethod.INFLATE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        model.updateLocaleSpecific()

        buttonsPager.adapter = CalculatorButtonsPagerAdapter(requireContext(), model)
        inputTv.isSaveEnabled = false
        inputTv.addTextChangedListener(object : ru.art2000.extensions.views.SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable) {
                if (model.expression != s.toString()) {
                    model.clearResult()
                }
                model.liveExpression.value = s.toString()
            }
        })
        binding.calculatorIo.inputScrollView.autoScrollOnInput(viewLifecycleOwner.lifecycle)
        // Set text before listener so model.selection is not wrongly updated
        inputTv.setText(model.expression)
        inputTv.onSelectionChangedListener =
            OnSelectionChangedListener { selStart: Int, selEnd: Int ->
                model.inputSelection = Pair(selStart, selEnd)
            }

        launchRepeatOnStarted {
            launchAndCollect(model.liveExpression) { expression ->
                if (expression == inputTv.text?.toString()) return@launchAndCollect
                inputTv.setText(expression)
            }
            launchAndCollect(model.liveInputSelection) { (first, second) ->
                inputTv.setSelection(first, second)
            }
            launchAndCollect(model.liveResult) { result ->

                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    binding.calculatorIo.resultHsv.visibility = if (result == null)
                        View.GONE
                    else
                        View.VISIBLE
                }

                if (result == null) {
                    resultTV.visibility = View.INVISIBLE
                    resultTV.text = null
                    return@launchAndCollect
                }
                resultTV.text = result
                resultTV.visibility = View.VISIBLE
            }
            launchAndCollect(model.liveMemory) { memoryValue ->
                if (memoryValue.isEmpty() || model.calculations.field.isZeroOrClose(memoryValue)) {
                    binding.calculatorIo.memory.visibility = View.INVISIBLE
                    binding.calculatorIo.infoDivider.visibility = View.INVISIBLE
                    return@launchAndCollect
                }
                binding.calculatorIo.infoDivider.visibility = View.VISIBLE
                val newMemoryText = "M$memoryValue"
                binding.calculatorIo.memory.text = newMemoryText
                binding.calculatorIo.memory.visibility = View.VISIBLE
            }
            launchAndCollect(model.liveAngleType) { angleType ->
                binding.calculatorIo.degRadTv.text =
                    angleType.toString().uppercase(Locale.getDefault())
            }
        }
        setupHistoryPart()

        return binding.root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        // Fix handle becoming visible in collapsed state after theme change
        if (slidingPanel.state != PanelState.COLLAPSED) {
            historyPanelHandle.visibility = View.GONE
        }
    }

    override val topViews: List<View>
        get() = listOf(binding.calculatorIoWrappper)

    override val bottomViews: List<View>
        get() = if (requireContext().isLandscape) listOf(binding.root) else emptyList()

    /**
     * @return true if panel was already closed, false otherwise
     */
    private fun ensureHistoryPanelClosed(): Boolean {
        if (slidingPanel.state == PanelState.EXPANDED ||
            slidingPanel.state == PanelState.ANCHORED
        ) {
            slidingPanel.state = PanelState.COLLAPSED
            return false
        }
        return true
    }

    // Private methods
    //=========================================
    private val historyPanel: HistoryLayoutBinding
        get() = binding.calculatorPanel.historyPart
    private val historyPanelHandle: RelativeLayout
        get() = binding.calculatorPanel.historyPart.historyHandle
    private val historyPanelHeader: RelativeLayout
        get() = binding.calculatorPanel.historyPart.header
    private val inputTv: ru.art2000.extensions.views.CustomInputEditText
        get() = binding.calculatorIo.tvInput
    private val resultTV: TextView
        get() = binding.calculatorIo.tvResult
    private val historyRecyclerView: ru.art2000.extensions.views.RecyclerWithEmptyView
        get() = binding.calculatorPanel.historyPart.historyList
    private val historyFloatingDate: TextView
        get() = binding.calculatorPanel.historyPart.floatingDateLayout.date
    private val slidingPanel: SlidingUpPanelLayout
        get() = binding.calculatorPanel.slidingPanel
    private val buttonsPager: ViewPager
        get() = binding.calculatorPanel.buttonPager

    private fun clearHistory() {
        model.clearHistoryDatabase()
        Toast.makeText(requireContext(), getString(R.string.history_cleared), Toast.LENGTH_SHORT)
            .show()
    }

    private fun setupHistoryPanel(adapter: HistoryListAdapter) {
        historyPanelHandle.setOnClickListener {
            slidingPanel.state = PanelState.EXPANDED
        }
        historyPanelHeader.setOnClickListener {
            slidingPanel.state = PanelState.COLLAPSED
        }
        slidingPanel.addPanelSlideListener(object : PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {
                if (slideOffset > 0) historyPanelHandle.visibility =
                    View.GONE else historyPanelHandle.visibility = View.VISIBLE
            }

            override fun onPanelStateChanged(
                panel: View,
                previousState: PanelState,
                newState: PanelState
            ) {
                if (newState == PanelState.DRAGGING &&
                    previousState == PanelState.COLLAPSED
                ) {
                    historyRecyclerView.scrollToPosition(adapter.itemCount - 1)
                }
                if (newState == PanelState.ANCHORED) {
                    slidingPanel.state = PanelState.COLLAPSED
                }
                if (newState == PanelState.COLLAPSED) {
                    slidingPanel.setDragView(historyPanelHandle)
                }
            }
        })
    }

    private fun setupHistoryHeader(adapter: HistoryListAdapter) {
        historyPanel.clearHistory.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.history_clear)
                .setMessage(R.string.history_clear_confirm)
                .setCancelable(true)
                .setNegativeButton(CommonR.string.cancel) { _, _ -> }
                .setPositiveButton(R.string.history_clear) { _, _ -> clearHistory() }
                .create()
                .show()
        }
        historyPanel.scrollUp.setOnClickListener {
            historyRecyclerView.smoothScrollToPosition(0)
        }
        historyPanel.scrollBottom.setOnClickListener {
            historyRecyclerView.smoothScrollToPosition(adapter.itemCount)
        }
        launchRepeatOnStarted {
            launchAndCollect(model.historyListItems) { data ->
                val visibility = if (data.isEmpty()) View.GONE else View.VISIBLE
                historyPanel.clearHistory.visibility = visibility
                historyPanel.scrollUp.visibility = visibility
                historyPanel.scrollBottom.visibility = visibility
            }
        }
    }

    private fun setupHistoryRecyclerView(): HistoryListAdapter {
        historyRecyclerView.emptyViewGenerator = { ctx, _, _ ->
            ru.art2000.extensions.views.createTextEmptyView(ctx, R.string.no_history)
        }
        val adapter = HistoryListAdapter(
            requireContext(),
            viewLifecycleOwner,
            model,
            model.historyListItems
        )
        historyRecyclerView.adapter = adapter
        val linearLayoutManager =
            ru.art2000.extensions.views.OrientationManger(requireContext()) { position ->
                adapter.itemCount == 0 || adapter.isDateItem(position)
            }
        historyRecyclerView.layoutManager = linearLayoutManager
        val itemTouchHelper = ItemTouchHelper(
            HistoryItemTouchHelperCallback(
                requireContext(),
                { position ->
                    val historyListItem = adapter.historyList[position]
                    historyListItem is HistoryValueItem
                }
            ) { position ->
                // Cast is safe due to filtering in previous lambda
                val historyListItem = adapter.historyList[position] as HistoryValueItem
                model.removeHistoryItem(historyListItem)
            }
        )
        itemTouchHelper.attachToRecyclerView(historyRecyclerView)
        historyRecyclerView.addItemDecoration(HistoryItemDecoration())
        historyRecyclerView.addOrientationItemDecoration()
        historyFloatingDate.visibility = View.GONE
        historyRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            private val timer = Timer()
            private var task: TimerTask? = null

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    task = object : TimerTask() {
                        override fun run() {
                            requireActivity().runOnUiThread {
                                historyFloatingDate.visibility = View.GONE
                            }
                        }
                    }
                    timer.schedule(task, 1000)
                } else {
                    if (task != null) {
                        task!!.cancel()
                        task = null
                    }
                    showFloatingDate()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) return
                showFloatingDate()
            }

            private fun showFloatingDate() {
                val pos = linearLayoutManager.findFirstVisibleItemPosition()
                val items = adapter.historyList
                if (pos >= items.size) return
                for (i in pos downTo 0) {
                    val item = items[i]
                    if (item is HistoryDateItem) {
                        val newText = item.date.toJavaLocalDate().toViewString()
                        historyFloatingDate.text = newText
                        historyFloatingDate.visibility = View.VISIBLE
                        break
                    }
                }
            }
        })
        return adapter
    }

    private fun setupHistoryPart() {
        val adapter = setupHistoryRecyclerView()
        setupHistoryPanel(adapter)
        setupHistoryHeader(adapter)
    }

    override fun onReselected() {
        ensureHistoryPanelClosed()
    }
}