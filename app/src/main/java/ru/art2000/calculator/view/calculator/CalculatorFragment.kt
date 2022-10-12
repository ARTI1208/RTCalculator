package ru.art2000.calculator.view.calculator

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sothree.slidinguppanel.PanelSlideListener
import com.sothree.slidinguppanel.PanelState
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.CalculatorLayoutBinding
import ru.art2000.calculator.databinding.HistoryLayoutBinding
import ru.art2000.calculator.model.calculator.AngleType
import ru.art2000.calculator.model.calculator.history.HistoryDateItem
import ru.art2000.calculator.model.calculator.history.HistoryValueItem
import ru.art2000.calculator.view.MainScreenFragment
import ru.art2000.calculator.view_model.calculator.CalculatorModel
import ru.art2000.extensions.views.*
import ru.art2000.helpers.GeneralHelper
import java.util.*
import kotlin.math.abs

class CalculatorFragment : MainScreenFragment() {

    private val model by viewModels<CalculatorModel>()
    private var binding: CalculatorLayoutBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null) {
            val viewBinding = CalculatorLayoutBinding.inflate(inflater, container, false)
            binding = viewBinding

            buttonsPager.adapter = CalculatorButtonsPagerAdapter(requireContext(), model)
            inputTv.addTextChangedListener(object : SimpleTextWatcher() {
                override fun afterTextChanged(s: Editable) {
                    if (model.expression != s.toString()) {
                        model.clearResult()
                    }
                    model.liveExpression.value = s.toString()
                }
            })
            viewBinding.calculatorIo.inputScrollView.autoScrollOnInput()
            inputTv.onSelectionChangedListener =
                CalculatorEditText.OnSelectionChangedListener { selStart: Int, selEnd: Int ->
                    model.inputSelection = Pair(selStart, selEnd)
                }
            model.liveExpression.observe(viewLifecycleOwner) { expression: String ->
                if (expression == Objects.requireNonNull(
                        inputTv.text
                    ).toString()
                ) return@observe
                inputTv.setText(expression)
            }
            model.liveInputSelection.observe(viewLifecycleOwner) { (first, second) ->
                inputTv.setSelection(first, second)
            }
            model.liveResult.observe(viewLifecycleOwner) { result: String? ->
                if (result == null) {
                    resultTV.visibility = View.INVISIBLE
                    resultTV.text = null
                    return@observe
                }
                resultTV.text = result
                resultTV.visibility = View.VISIBLE
            }
            model.liveMemory.observe(viewLifecycleOwner) { memoryValue ->
                if (abs(memoryValue) < 1e-5) {
                    viewBinding.calculatorIo.memory.visibility = View.INVISIBLE
                    viewBinding.calculatorIo.infoDivider.visibility = View.INVISIBLE
                    return@observe
                }
                viewBinding.calculatorIo.infoDivider.visibility = View.VISIBLE
                val newMemoryText = "M" + GeneralHelper.resultNumberFormat.format(memoryValue)
                viewBinding.calculatorIo.memory.text = newMemoryText
                viewBinding.calculatorIo.memory.visibility = View.VISIBLE
            }
            model.liveAngleType.observe(viewLifecycleOwner) { angleType: AngleType ->
                viewBinding.calculatorIo.degRadTv.text = angleType.toString().uppercase(
                    Locale.getDefault()
                )
            }
            setupHistoryPart()
        }
        return binding!!.root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        // Fix handle becoming visible in collapsed state after theme change
        if (slidingPanel.panelState != PanelState.COLLAPSED) {
            historyPanelHandle.visibility = View.GONE
        }
    }

    override fun updateViewOnCreated(createdView: View) {
        binding?.calculatorIoWrappper?.applyWindowTopInsets(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    /**
     * @return true if panel was already closed, false otherwise
     */
    private fun ensureHistoryPanelClosed(): Boolean {
        if (binding == null) return true
        if (slidingPanel.panelState == PanelState.EXPANDED ||
            slidingPanel.panelState == PanelState.ANCHORED
        ) {
            slidingPanel.panelState = PanelState.COLLAPSED
            return false
        }
        return true
    }

    // Private methods
    //=========================================
    private val historyPanel: HistoryLayoutBinding
        get() = binding!!.calculatorPanel.historyPart
    private val historyPanelHandle: RelativeLayout
        get() = binding!!.calculatorPanel.historyPart.historyHandle
    private val historyPanelHeader: RelativeLayout
        get() = binding!!.calculatorPanel.historyPart.header
    private val inputTv: CalculatorEditText
        get() = binding!!.calculatorIo.tvInput
    private val resultTV: TextView
        get() = binding!!.calculatorIo.tvResult
    private val historyRecyclerView: RecyclerWithEmptyView
        get() = binding!!.calculatorPanel.historyPart.historyList
    private val historyFloatingDate: TextView
        get() = binding!!.calculatorPanel.historyPart.floatingDateLayout.date
    private val slidingPanel: SlidingUpPanelLayout
        get() = binding!!.calculatorPanel.slidingPanel
    private val buttonsPager: ViewPager
        get() = binding!!.calculatorPanel.buttonPager

    private fun clearHistory() {
        model.clearHistoryDatabase()
        Toast.makeText(requireContext(), getString(R.string.history_cleared), Toast.LENGTH_SHORT)
            .show()
    }

    private fun setupHistoryPanel(adapter: HistoryListAdapter) {
        historyPanelHandle.setOnClickListener {
            slidingPanel.panelState = PanelState.EXPANDED
        }
        historyPanelHeader.setOnClickListener {
            slidingPanel.panelState = PanelState.COLLAPSED
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
                    slidingPanel.panelState = PanelState.COLLAPSED
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
                .setNegativeButton(R.string.cancel) { _, _ -> }
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
        model.historyListItems.observe(viewLifecycleOwner) { data ->
            val visibility = if (data.isEmpty()) View.GONE else View.VISIBLE
            historyPanel.clearHistory.visibility = visibility
            historyPanel.scrollUp.visibility = visibility
            historyPanel.scrollBottom.visibility = visibility
        }
    }

    private fun setupHistoryRecyclerView(): HistoryListAdapter {
        historyRecyclerView.emptyViewGenerator = { ctx, _, _ ->
            createTextEmptyView(ctx, R.string.no_history)
        }
        val adapter = HistoryListAdapter(
            requireContext(),
            viewLifecycleOwner,
            model,
            model.historyListItems
        )
        historyRecyclerView.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(requireContext())
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
                model.removeHistoryItem(historyListItem.dbItem.id)
            }
        )
        itemTouchHelper.attachToRecyclerView(historyRecyclerView)
        historyRecyclerView.addItemDecoration(HistoryItemDecoration())
        historyRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
        model.historyListItems.observe(viewLifecycleOwner) { data ->
            if (data.isNotEmpty()) {
                historyRecyclerView.scrollToPosition(adapter.itemCount - 1)
            }
        }
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
                        val newText = item.date.toViewString()
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

    override fun getIcon(): Int {
        return R.drawable.ic_calc
    }

    override fun getReplaceableId(): Int {
        return R.id.navigation_calc
    }

    override fun getTitle(): Int {
        return R.string.title_calc
    }

    override fun onBackPressed(): Boolean {
        return ensureHistoryPanelClosed()
    }

    override fun onReselected() {
        ensureHistoryPanelClosed()
    }
}