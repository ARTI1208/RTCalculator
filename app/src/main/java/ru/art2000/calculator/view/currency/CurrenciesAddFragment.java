package ru.art2000.calculator.view.currency;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import ru.art2000.calculator.R;
import ru.art2000.calculator.databinding.ItemAddCurrenciesListBinding;
import ru.art2000.calculator.databinding.ModifyCurrenciesLayoutBinding;
import ru.art2000.calculator.model.currency.CurrencyItem;
import ru.art2000.calculator.view_model.currency.CurrenciesAddModel;
import ru.art2000.calculator.view_model.currency.CurrenciesSettingsModel;
import ru.art2000.calculator.view_model.currency.CurrencyDependencies;
import ru.art2000.extensions.collections.CollectionsKt;
import ru.art2000.extensions.collections.LiveList;
import ru.art2000.extensions.fragments.UniqueReplaceableFragment;
import ru.art2000.extensions.views.ViewsKt;

public class CurrenciesAddFragment extends UniqueReplaceableFragment {

    private ModifyCurrenciesLayoutBinding viewBinding;
    private CurrenciesAddModel model;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (viewBinding == null) {
            viewBinding = ModifyCurrenciesLayoutBinding.inflate(inflater);
            model = new ViewModelProvider(requireActivity()).get(CurrenciesSettingsModel.class);

            model.getRecyclerViewBottomPadding().observe(getViewLifecycleOwner(), bottomPadding ->
                    viewBinding.modifyCurrenciesList.setPadding(0, 0, 0, bottomPadding));

            LinearLayoutManager llm = new LinearLayoutManagerWrapper(requireContext());
            llm.setOrientation(RecyclerView.VERTICAL);
            viewBinding.modifyCurrenciesList.setLayoutManager(llm);

            viewBinding.modifyCurrenciesList.setEmptyViewGenerator((context, viewGroup, integer) ->
                    ViewsKt.createTextEmptyView(context, getEmptyTextRes()));

            viewBinding.modifyCurrenciesList.setEmptyViewHolderBinder(view -> {
                TextView emptyView = (TextView) view;
                emptyView.setText(getEmptyTextRes());
            });

            AddCurrenciesAdapter adapter = new AddCurrenciesAdapter();
            viewBinding.modifyCurrenciesList.setAdapter(adapter);
        }

        return viewBinding.getRoot();
    }

    @Override
    public void onReselected() {
        viewBinding.modifyCurrenciesList.smoothScrollToPosition(0);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public int getTitle() {
        return R.string.currencies_add;
    }

    @StringRes
    private int getEmptyTextRes() {
        if (model.getCurrentQuery().length() > 0
                && Objects.requireNonNull(model.getHiddenItems().getValue()).size() > 0) {
            return R.string.empty_text_no_currencies_found;
        } else {
            return R.string.empty_text_all_currencies_added;
        }
    }

    private static class LinearLayoutManagerWrapper extends LinearLayoutManager {

        public LinearLayoutManagerWrapper(Context context) {
            super(context);
        }

        /*
         * TODO investigate why app crashes when deleting query characters and how this prevents it
         * Thanks to https://stackoverflow.com/a/40177879
         */
        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    private class AddCurrenciesAdapter extends RecyclerView.Adapter<AddCurrenciesAdapter.Holder> {

        AddCurrenciesAdapter() {

            model.getDisplayedHiddenItems().observe(getViewLifecycleOwner(), new LiveList.LiveListObserver<CurrencyItem>() {
                @Override
                public void onAnyChanged(@NotNull List<? extends CurrencyItem> previousList) {
                    dispatchListUpdate(previousList, model.getDisplayedHiddenItems());
                }
            });

            model.getSelectedHiddenItems().observe(getViewLifecycleOwner(), new LiveList.LiveListObserver<CurrencyItem>() {

                @Override
                public void onItemsInserted(@NotNull List<? extends CurrencyItem> previousList,
                                            @NotNull List<? extends CurrencyItem> insertedItems,
                                            int position) {
                    super.onItemsInserted(previousList, insertedItems, position);

                    for (CurrencyItem item : insertedItems) {
                        markItem(item, true);
                    }
                }

                @Override
                public void onItemsRemoved(@NotNull List<? extends CurrencyItem> previousList,
                                           @NotNull List<Integer> removedItems) {
                    super.onItemsRemoved(previousList, removedItems);

                    for (int i : removedItems) {
                        markItem(previousList.get(i), false);
                    }
                }
            });
        }

        @NonNull
        @Override
        @SuppressLint("InflateParams")
        public AddCurrenciesAdapter.Holder onCreateViewHolder(
                @NonNull ViewGroup parent, int viewType
        ) {

            ItemAddCurrenciesListBinding binding = ItemAddCurrenciesListBinding.inflate(
                    LayoutInflater.from(requireContext())
            );

            return new Holder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull AddCurrenciesAdapter.Holder holder, int position) {
            CurrencyItem currencyItem = model.getDisplayedHiddenItems().get(position);
            holder.bind(currencyItem);
        }

        @Override
        public int getItemCount() {
            return model.getDisplayedHiddenItems().size();
        }

        private void markItem(CurrencyItem currencyItem, boolean selected) {
            Holder holder = (Holder)
                    viewBinding.modifyCurrenciesList.findViewHolderForAdapterPosition(
                            model.getDisplayedHiddenItems().indexOf(currencyItem));

            if (holder == null || holder.checkBox == null)
                return;

            holder.checkBox.setChecked(selected);
        }

        private void dispatchListUpdate(List<? extends CurrencyItem> oldData, List<? extends CurrencyItem> newData) {
            DiffUtil.DiffResult result = CollectionsKt.calculateDiff(oldData, newData);
            result.dispatchUpdatesTo(this);
        }

        private class Holder extends RecyclerView.ViewHolder {

            TextView code;
            TextView name;
            CheckBox checkBox;

            Holder(final ItemAddCurrenciesListBinding viewBinding) {
                super(viewBinding.getRoot());

                code = viewBinding.currencyCode;
                name = viewBinding.currencyName;
                checkBox = viewBinding.checkboxAdd;
            }

            void bind(CurrencyItem currencyItem) {
                code.setText(currencyItem.code);
                name.setText(CurrencyDependencies.getNameIdentifierForCode(name.getContext(), currencyItem.code));

                checkBox.setOnCheckedChangeListener(null);
                checkBox.setChecked(model.isHiddenItemSelected(currencyItem));

                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    int pos = getBindingAdapterPosition();
                    CurrencyItem item = model.getDisplayedHiddenItems().get(pos);

                    if (isChecked != model.isHiddenItemSelected(item)) {
                        model.setHiddenItemSelected(item, isChecked);
                    }
                });

                itemView.setOnClickListener(v -> checkBox.performClick());
            }
        }
    }

}
