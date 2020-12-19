package ru.art2000.calculator.view.calculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import ru.art2000.calculator.R;
import ru.art2000.calculator.databinding.CalculatorLayoutBinding;
import ru.art2000.calculator.view_model.calculator.CalculatorModel;
import ru.art2000.extensions.NavigationFragment;
import ru.art2000.extensions.SimpleTextWatcher;
import ru.art2000.helpers.GeneralHelper;


public class CalculatorFragment extends NavigationFragment {

    private static final int PASTE = 200;
    private static final int PASTE_AFTER = 201;

    private HorizontalScrollView hsv;
    private HistoryListAdapter adapter;

    private CalculatorModel model;
    private CalculatorLayoutBinding binding = null;

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

            hsv = (HorizontalScrollView) getInputTv().getParent();
            hsv.setOnLongClickListener(view -> {
                hsv.showContextMenu();
                return true;
            });

            getButtonsPager().setAdapter(new CalculatorButtonsPagerAdapter(requireContext(), model));

            registerForContextMenu(getInputTv());
            getInputTv().addTextChangedListener(new SimpleTextWatcher() {

                @Override
                public void afterTextChanged(@NonNull Editable s) {
                    hsv.postDelayed(() ->
                            hsv.smoothScrollTo(getInputTv().getWidth(), 0), 100L);

                    model.getCurrentExpression().setValue(s.toString());
                }
            });

            model.getCurrentExpression().observe(getViewLifecycleOwner(), (expression) -> {

                if (expression.equals(getInputTv().getText().toString())) return;

                getInputTv().setText(expression);
            });

            model.getCurrentResult().observe(getViewLifecycleOwner(), (expression) -> {

                if (expression == null) {
                    getResultTV().setVisibility(View.INVISIBLE);
                    getResultTV().setText(null);
                    return;
                }

                getResultTV().setText(expression);
                getResultTV().setVisibility(View.VISIBLE);
            });

            model.getCurrentMemory().observe(getViewLifecycleOwner(), (memoryValue) -> {

                if (Math.abs(memoryValue) < 1e-5) {
                    binding.memory.setVisibility(View.INVISIBLE);
                    return;
                }

                String newMemoryText = "M" + GeneralHelper.resultNumberFormat.format(memoryValue);
                binding.memory.setText(newMemoryText);
                binding.memory.setVisibility(View.VISIBLE);
            });

            model.getCurrentAngleType().observe(getViewLifecycleOwner(), angleType ->
                    binding.degRadTv.setText(angleType.toString()));

            setupHistoryPart();
        }
        return binding.getRoot();
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu,
                                    @NonNull View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        menu.add(Menu.NONE, PASTE, Menu.NONE, requireContext().getString(R.string.paste_replace));
        menu.add(Menu.NONE, PASTE_AFTER, Menu.NONE, requireContext().getString(R.string.paste_after));

        ClipboardManager cmg =
                (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);

        if (cmg == null || !cmg.hasPrimaryClip()) {
            menu.getItem(0).setEnabled(false);
            menu.getItem(1).setEnabled(false);
        }

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem menuItem) {

        ClipboardManager cmg = (
                ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);

        int id = menuItem.getItemId();

        boolean isPasteItem = id >= PASTE && id <= PASTE_AFTER;

        if (isPasteItem && cmg.getPrimaryClip() == null) {
            Toast.makeText(
                    requireContext(), "Error getting access to clipboard", Toast.LENGTH_SHORT
            ).show();
            return false;
        }

        switch (id) {
            case PASTE:
                ClipData.Item clipItem = cmg.getPrimaryClip().getItemAt(0);
                getInputTv().setText(clipItem.getText().toString());
                break;
            case PASTE_AFTER:
                clipItem = cmg.getPrimaryClip().getItemAt(0);
                getInputTv().append(clipItem.getText().toString());
                break;
        }

        return true;
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

    private TextView getInputTv() {
        return binding.tvInput;
    }

    private TextView getResultTV() {
        return binding.tvResult;
    }

    private TextView getEmptyHistoryTextView() {
        return binding.calculatorPanel.historyPart.emptyTv;
    }

    private ViewGroup getHistoryRecyclerContainer() {
        return binding.calculatorPanel.historyPart.recyclerLayout;
    }

    private RecyclerView getHistoryRecyclerView() {
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
        showEmptyView();
        Toast.makeText(requireContext(), getString(R.string.history_cleared), Toast.LENGTH_SHORT).show();
    }

    private void setupHistoryPanel() {
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

    private void setupHistoryHeader() {
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

    private void setupHistoryRecyclerView() {

        adapter = new HistoryListAdapter(requireContext(), getViewLifecycleOwner(), model, model.getHistoryItems());
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
            if (data.isEmpty()) {
                showEmptyView();
            } else {
                showRecyclerView();
                getHistoryRecyclerView().scrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    private void setupHistoryPart() {
        setupHistoryPanel();
        setupHistoryHeader();
        setupHistoryRecyclerView();
    }

    private void showRecyclerView() {
        getHistoryRecyclerContainer().setVisibility(View.VISIBLE);
        getEmptyHistoryTextView().setVisibility(View.GONE);
    }

    private void showEmptyView() {
        getHistoryRecyclerContainer().setVisibility(View.INVISIBLE);
        getEmptyHistoryTextView().setVisibility(View.VISIBLE);
    }

    @Override
    public int getOrder() {
        return 1;
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

}
