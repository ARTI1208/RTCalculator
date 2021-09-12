package ru.art2000.calculator.view.calculator;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Objects;

import kotlin.Pair;
import ru.art2000.calculator.R;
import ru.art2000.calculator.databinding.CalculatorLayoutBinding;
import ru.art2000.calculator.view_model.calculator.CalculatorModel;
import ru.art2000.extensions.fragments.NavigationFragment;
import ru.art2000.extensions.views.CalculatorEditText;
import ru.art2000.extensions.views.RecyclerWithEmptyView;
import ru.art2000.extensions.views.SimpleTextWatcher;
import ru.art2000.extensions.views.ViewsKt;
import ru.art2000.helpers.GeneralHelper;


public class CalculatorFragment extends NavigationFragment {

    private CalculatorModel model;
    private CalculatorLayoutBinding binding;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        if (binding == null) {

            binding = CalculatorLayoutBinding.inflate(inflater, container, false);
            model = new CalculatorModel(requireActivity().getApplication());

            getButtonsPager().setAdapter(new CalculatorButtonsPagerAdapter(requireContext(), model));

            getInputTv().addTextChangedListener(new SimpleTextWatcher() {

                @Override
                public void afterTextChanged(@NonNull Editable s) {
                    if (!model.getExpression().equals(s.toString())) {
                        model.clearResult();
                    }

                    model.getLiveExpression().setValue(s.toString());
                }
            });

            getInputTv().setOnSelectionChangedListener((selStart, selEnd) ->
                    model.setInputSelection(new Pair<>(selStart, selEnd)));

            model.getLiveExpression().observe(getViewLifecycleOwner(), (expression) -> {

                if (expression.equals(Objects.requireNonNull(getInputTv().getText()).toString()))
                    return;

                getInputTv().setText(expression);
            });

            model.getLiveInputSelection().observe(getViewLifecycleOwner(), (selection) ->
                    getInputTv().setSelection(selection.getFirst(), selection.getSecond()));

            model.getLiveResult().observe(getViewLifecycleOwner(), (result) -> {

                if (result == null) {
                    getResultTV().setVisibility(View.INVISIBLE);
                    getResultTV().setText(null);
                    return;
                }

                getResultTV().setText(result);
                getResultTV().setVisibility(View.VISIBLE);
            });

            model.getLiveMemory().observe(getViewLifecycleOwner(), (memoryValue) -> {

                if (Math.abs(memoryValue) < 1e-5) {
                    binding.calculatorIo.memory.setVisibility(View.INVISIBLE);
                    binding.calculatorIo.infoDivider.setVisibility(View.INVISIBLE);
                    return;
                }

                binding.calculatorIo.infoDivider.setVisibility(View.VISIBLE);

                String newMemoryText = "M" + GeneralHelper.resultNumberFormat.format(memoryValue);
                binding.calculatorIo.memory.setText(newMemoryText);
                binding.calculatorIo.memory.setVisibility(View.VISIBLE);
            });

            model.getLiveAngleType().observe(getViewLifecycleOwner(), angleType ->
                    binding.calculatorIo.degRadTv.setText(angleType.toString().toUpperCase()));

            setupHistoryPart();
        }
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        model = null;
    }

    public boolean ensureHistoryPanelClosed() {
        if (getSlidingPanel().getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                getSlidingPanel().getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED) {
            getSlidingPanel().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return false;
        }

        return true;
    }


    // Private methods
    //=========================================

    private RelativeLayout getHistoryPanelHandle() {
        return binding.calculatorPanel.historyPart.historyHandle;
    }

    private RelativeLayout getHistoryPanelHeader() {
        return binding.calculatorPanel.historyPart.header;
    }

    private CalculatorEditText getInputTv() {
        return binding.calculatorIo.tvInput;
    }

    private TextView getResultTV() {
        return binding.calculatorIo.tvResult;
    }

    private RecyclerWithEmptyView getHistoryRecyclerView() {
        return binding.calculatorPanel.historyPart.historyList;
    }

    private SlidingUpPanelLayout getSlidingPanel() {
        return binding.calculatorPanel.slidingPanel;
    }

    private ViewPager getButtonsPager() {
        return binding.calculatorPanel.buttonPager;
    }

    private void clearHistory() {
        model.clearHistoryDatabase();
        Toast.makeText(requireContext(), getString(R.string.history_cleared), Toast.LENGTH_SHORT).show();
    }

    private void setupHistoryPanel(HistoryListAdapter adapter) {
        getHistoryPanelHandle().setOnClickListener(view ->
                getSlidingPanel().setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED));
        getHistoryPanelHeader().setOnClickListener(view ->
                getSlidingPanel().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED));

        getSlidingPanel().addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset > 0)
                    getHistoryPanelHandle().setVisibility(View.GONE);
                else
                    getHistoryPanelHandle().setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelStateChanged(View panelView, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.DRAGGING &&
                        previousState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    getHistoryRecyclerView().scrollToPosition(adapter.getItemCount() - 1);
                }
                if (newState == SlidingUpPanelLayout.PanelState.ANCHORED) {
                    getSlidingPanel().setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    getSlidingPanel().setDragView(getHistoryPanelHandle());
                }
            }
        });
    }

    private void setupHistoryHeader(HistoryListAdapter adapter) {
        binding.calculatorPanel.historyPart.clearHistory.setOnClickListener(clearBtn -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            builder.setTitle(R.string.history_clear)
                    .setMessage(R.string.history_clear_confirm)
                    .setCancelable(true)
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    })
                    .setPositiveButton(R.string.history_clear, (dialog, which) -> clearHistory())
                    .create()
                    .show();
        });

        binding.calculatorPanel.historyPart.scrollUp.setOnClickListener(scrollUp ->
                getHistoryRecyclerView().smoothScrollToPosition(0));
        binding.calculatorPanel.historyPart.scrollBottom.setOnClickListener(scrollDown ->
                getHistoryRecyclerView().smoothScrollToPosition(adapter.getItemCount()));
    }

    private HistoryListAdapter setupHistoryRecyclerView() {

        getHistoryRecyclerView().setEmptyViewGenerator((context, viewGroup, integer) ->
                ViewsKt.createTextEmptyView(context, R.string.no_history));

        HistoryListAdapter adapter = new HistoryListAdapter(requireContext(), getViewLifecycleOwner(), model, model.getHistoryItems());
        getHistoryRecyclerView().setAdapter(adapter);
        getHistoryRecyclerView().setLayoutManager(new LinearLayoutManager(requireContext()));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new HistoryItemTouchHelperCallback(requireContext(), (position) ->
                        model.removeHistoryItem(adapter.getHistoryList().get(position).getId())
                )
        );

        itemTouchHelper.attachToRecyclerView(getHistoryRecyclerView());
        getHistoryRecyclerView().addItemDecoration(new HistoryItemDecoration());
        getHistoryRecyclerView().addItemDecoration(
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        );

        model.getHistoryItems().observe(getViewLifecycleOwner(), data -> {
            if (!data.isEmpty()) {
                getHistoryRecyclerView().scrollToPosition(adapter.getItemCount() - 1);
            }
        });

        return adapter;
    }

    private void setupHistoryPart() {
        HistoryListAdapter adapter = setupHistoryRecyclerView();
        setupHistoryPanel(adapter);
        setupHistoryHeader(adapter);
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_calc;
    }

    @Override
    public int getReplaceableId() {
        return R.id.navigation_calc;
    }

    @Override
    public int getTitle() {
        return R.string.title_calc;
    }

    @Override
    public boolean onBackPressed() {
        return ensureHistoryPanelClosed();
    }

    @Override
    public void onReselected() {
        ensureHistoryPanelClosed();
    }
}
