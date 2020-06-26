package ru.art2000.calculator.view.currency;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import ru.art2000.calculator.R;
import ru.art2000.calculator.model.currency.CurrencyItem;
import ru.art2000.calculator.view_model.currency.CurrenciesAddModel;
import ru.art2000.calculator.view_model.currency.CurrenciesSettingsModel;
import ru.art2000.calculator.view_model.currency.CurrencyDependencies;
import ru.art2000.extensions.CollectionsKt;
import ru.art2000.extensions.LiveList;
import ru.art2000.extensions.ReplaceableFragment;

public class CurrenciesAddFragment extends ReplaceableFragment {

    AddCurrenciesAdapter adapter;
    @Px
    int recyclerViewBottomPadding;
    private View v = null;
    private RecyclerView recycler;
    private TextView emptyView;
    private Context mContext;
    private CurrenciesSettingsActivity parent;
    private CurrenciesAddModel model;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (v == null) {
            mContext = getActivity();
            parent = (CurrenciesSettingsActivity) requireActivity();
            model = new ViewModelProvider(parent).get(CurrenciesSettingsModel.class);
            v = inflater.inflate(R.layout.modify_currencies_layout, null);
            emptyView = v.findViewById(R.id.empty_tv);
            recycler = v.findViewById(R.id.modify_currencies_list);
            recycler.setPadding(0, 0, 0, recyclerViewBottomPadding);
            LinearLayoutManager llm = new LinearLayoutManager(mContext);
            llm.setOrientation(RecyclerView.VERTICAL);
            recycler.setLayoutManager(llm);
            adapter = new AddCurrenciesAdapter();
            recycler.setAdapter(adapter);
        }
        return v;
    }

    private void toggleEmptyView() {
        if (adapter == null || adapter.getItemCount() == 0) {
            emptyView.setText(getEmptyText());
            emptyView.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);

        } else {
            emptyView.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        }
    }

    private String getEmptyText() {
        if (parent.barSearchView.getQuery().length() > 0 && model.getHiddenItems().getValue().size() > 0) {
            return getString(R.string.empty_text_no_currencies_found);
        } else {
            return getString(R.string.empty_text_all_currencies_added);
        }
    }

    @Override
    public void onReselected() {
        recycler.smoothScrollToPosition(0);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public int getTitle() {
        return R.string.currencies_add;
    }

    class AddCurrenciesAdapter extends RecyclerView.Adapter<AddCurrenciesAdapter.Holder> {

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
                        Holder holder = (Holder)
                                recycler.findViewHolderForAdapterPosition(model.getDisplayedHiddenItems().indexOf(item));

                        if (holder == null || holder.checkBox == null)
                            continue;

                        holder.checkBox.setChecked(true);
                    }
                }

                @Override
                public void onItemsRemoved(@NotNull List<? extends CurrencyItem> previousList,
                                           @NotNull List<Integer> removedItems) {
                    super.onItemsRemoved(previousList, removedItems);

                    for (int i : removedItems) {
                        Holder holder = (Holder)
                                recycler.findViewHolderForAdapterPosition(
                                        model.getDisplayedHiddenItems().indexOf(previousList.get(i)));

                        if (holder == null || holder.checkBox == null)
                            continue;

                        holder.checkBox.setChecked(false);
                    }
                }
            });
        }

        @NonNull
        @Override
        @SuppressLint("InflateParams")
        public AddCurrenciesAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent,
                                                              int viewType) {
            View item = LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.item_add_currencies_list, null);
            return new Holder(item);
        }

        @Override
        public void onBindViewHolder(@NonNull AddCurrenciesAdapter.Holder holder, int position) {
            CurrencyItem currencyItem = model.getDisplayedHiddenItems().get(position);
            holder.code.setText(currencyItem.code);
            holder.name.setText(CurrencyDependencies.getNameIdentifierForCode(mContext, currencyItem.code));

            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(model.isHiddenItemSelected(currencyItem));

            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int pos = holder.getBindingAdapterPosition();
                CurrencyItem item = model.getDisplayedHiddenItems().get(pos);

                if (isChecked != model.isHiddenItemSelected(item)) {
                    model.setHiddenItemSelected(item, isChecked);
                }
            });

            holder.itemView.setOnClickListener(v ->
                    holder.checkBox.performClick());
        }

        @Override
        public int getItemCount() {
            return model.getDisplayedHiddenItems().size();
        }

        void dispatchListUpdate(List<? extends CurrencyItem> oldData, List<? extends CurrencyItem> newData) {
            toggleEmptyView();
            DiffUtil.DiffResult result = CollectionsKt.calculateDiff(oldData, newData);
            result.dispatchUpdatesTo(this);
        }

        class Holder extends RecyclerView.ViewHolder {

            TextView code;
            TextView name;
            CheckBox checkBox;

            Holder(final View itemView) {
                super(itemView);
                code = itemView.findViewById(R.id.currency_code);
                checkBox = itemView.findViewById(R.id.checkbox_add);
                name = itemView.findViewById(R.id.currency_name);
            }

        }
    }

}
