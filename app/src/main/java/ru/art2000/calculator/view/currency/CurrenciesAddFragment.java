package ru.art2000.calculator.view.currency;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.MergeAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.art2000.calculator.R;
import ru.art2000.calculator.model.currency.CurrencyItem;
import ru.art2000.calculator.view_model.currency.CurrenciesAddModel;
import ru.art2000.calculator.view_model.currency.CurrencyDependencies;
import ru.art2000.helpers.CurrencyValuesHelper;

public class CurrenciesAddFragment extends Fragment {

    AddCurrenciesAdapter adapter;
    @Px
    int recyclerViewBottomPadding;
    private View v = null;
    private RecyclerView recycler;
    private TextView emptyView;
    private Context mContext;
    private CurrenciesSettingsActivity parent;
    private CurrenciesAddModel model;

    void scrollToTop() {
        recycler.smoothScrollToPosition(0);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (v == null) {
            mContext = getActivity();
            parent = (CurrenciesSettingsActivity) requireActivity();
            model = new CurrenciesAddModel(parent.getApplication());
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model.getHiddenItems().observe(getViewLifecycleOwner(), currencyItems -> {
            adapter.setNewData(currencyItems);
        });
    }

    private List<CurrencyItem> searchByQuery(String query) {
        return CurrencyValuesHelper.findAllItems(mContext, adapter.filteredData, query);
    }

    void filterList(String query) {
        adapter.filterData(query);
    }

//    private void setNewList(ArrayList<CurrencyItem> list) {
//        if (adapter != null) {
//            adapter.setNewData(list);
//        } else {
//            adapter = new AddCurrenciesAdapter(list);
//        }
//        if (recycler.getAdapter() == null) {
//            recycler.setAdapter(adapter);
//        }
//        toggleEmptyView();
//    }

    private void toggleEmptyView() {
        if (adapter == null)
            return;
        if (adapter.getItemCount() == 0) {
            if (emptyView.getVisibility() == View.GONE) {
                emptyView.setText(getEmptyText());
                emptyView.setVisibility(View.VISIBLE);
            }
        } else {
            if (emptyView.getVisibility() == View.VISIBLE) {
                emptyView.setVisibility(View.GONE);
            }
        }
    }

    private String getEmptyText() {
        if (parent.barSearchView.getQuery().length() > 0 && adapter.fullData.size() > 0) {
            return getString(R.string.empty_text_no_currencies_found);
        } else {
            return getString(R.string.empty_text_all_currencies_added);
        }
    }

    private void updateCheckState(List<CurrencyItem> list) {
        for (int i = 0; i < list.size(); ++i) {
            RecyclerView.ViewHolder holder = recycler.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                ((AddCurrenciesAdapter.Holder) holder).checkBox.setChecked(list.get(i).isSelected);
            }
        }
    }

    class AddCurrenciesAdapter extends RecyclerView.Adapter<AddCurrenciesAdapter.Holder> {

        List<CurrencyItem> filteredData = new ArrayList<>();
        List<CurrencyItem> fullData = new ArrayList<>();
        String currentQuery = "";

        private MutableLiveData<Pair<Integer, Integer>> mSelectedItemsCount = new MutableLiveData<>(new Pair<>(0, 0));
        LiveData<Pair<Integer, Integer>> selectedItemsCount = mSelectedItemsCount;

        AddCurrenciesAdapter() {
            countSelectedItems();
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
        public void onViewAttachedToWindow(@NonNull Holder holder) {
            int pos = holder.getBindingAdapterPosition();
            holder.checkBox.setChecked(filteredData.get(pos).isSelected);
        }

        @Override
        public void onBindViewHolder(@NonNull AddCurrenciesAdapter.Holder holder, int position) {
            CurrencyItem currencyItem = filteredData.get(position);
            holder.code.setText(currencyItem.code);
            holder.name.setText(CurrencyDependencies.getNameIdentifierForCode(mContext, currencyItem.code));

            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(currencyItem.isSelected);
            Log.d("itemBind", currencyItem.code + "||" + currencyItem.isSelected);
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                CurrencyItem item = filteredData.get(holder.getBindingAdapterPosition());
                if (isChecked != item.isSelected) {
                    item.isSelected = !item.isSelected;
                    mSelectedItemsCount.setValue(new Pair<>(getSelectedCount() + (item.isSelected ? 1 : -1), getItemCount()));
                }
            });

            holder.itemView.setOnClickListener(v ->
                    holder.checkBox.performClick());
        }

        @Override
        public int getItemCount() {
            return filteredData.size();
        }

        public int getSelectedCount() {
            Pair<Integer, Integer> value = mSelectedItemsCount.getValue();
            if (value == null) {
                return 0;
            } else {
                return value.first;
            }
        }

        ArrayList<CurrencyItem> getSelectedItems() {
            ArrayList<CurrencyItem> selectedItems = new ArrayList<>();
            for (CurrencyItem itemWrapper : filteredData) {
                if (itemWrapper.isSelected) {
                    selectedItems.add(itemWrapper);
                }
            }
            return selectedItems;
        }

        private void countSelectedItems() {
            int selectedCount = 0;
            for (CurrencyItem currencyItem : filteredData) {
                if (currencyItem.isSelected) {
                    ++selectedCount;
                }
            }
            mSelectedItemsCount.setValue(new Pair<>(selectedCount, getItemCount()));
        }

        void deselectAll() {
            for (CurrencyItem currencyItem : filteredData) {
                currencyItem.isSelected = false;
            }
            updateCheckState(filteredData);
            mSelectedItemsCount.setValue(new Pair<>(0, getItemCount()));
        }

        void selectAll() {
            for (CurrencyItem currencyItem : filteredData) {
                currencyItem.isSelected = true;
            }
            updateCheckState(filteredData);
            mSelectedItemsCount.setValue(new Pair<>(getItemCount(), getItemCount()));

            Log.d("Selec", String.valueOf(getSelectedCount()));

        }

        boolean isSomethingSelected() {
            return getSelectedCount() != 0;
        }

        boolean isAllSelected() {
            return getSelectedCount() == getItemCount();
        }

        public void setNewData(@NonNull List<CurrencyItem> newData) {
            Log.d("NewData", "oooooo" + newData.size());
            for (CurrencyItem item : newData) {
                item.isSelected = false;
            }

            fullData = newData;

            if (filteredData == null || filteredData.isEmpty()) {
                filteredData = CurrencyValuesHelper.findAllItems(mContext, newData, currentQuery);
                toggleEmptyView();
                notifyItemRangeInserted(0, newData.size());
                mSelectedItemsCount.setValue(new Pair<>(0, getItemCount()));
            } else {
                List<CurrencyItem> newFilteredData = CurrencyValuesHelper.findAllItems(mContext, newData, currentQuery);
                applyFilter(newFilteredData);
            }
            Log.d("NewCoun", String.valueOf(getItemCount()));
        }

        void reFilterData() {
            filterData(currentQuery);
        }

        void applyFilter(List<CurrencyItem> newFilteredData) {
            DiffUtil.DiffResult result =
                    DiffUtil.calculateDiff(CurrencyDependencies.getDiffCallback(filteredData, newFilteredData));
            result.dispatchUpdatesTo(new ListUpdateCallback() {

                List<CurrencyItem> oldData = filteredData;

                @Override
                public void onInserted(int position, int count) {

                }

                @Override
                public void onRemoved(int position, int count) {

                }

                @Override
                public void onMoved(int fromPosition, int toPosition) {
                    newFilteredData.get(toPosition).isSelected = oldData.get(fromPosition).isSelected;
                }

                @Override
                public void onChanged(int position, int count, @Nullable Object payload) {

                }
            });
            filteredData = newFilteredData;
            toggleEmptyView();
            result.dispatchUpdatesTo(this);
            countSelectedItems();
        }

        void filterData(String filterString) {
            new Thread(() -> {
                List<CurrencyItem> list = CurrencyValuesHelper.findAllItems(mContext, fullData, filterString);
                currentQuery = filterString;
                parent.runOnUiThread(() -> applyFilter(list));
            }).start();
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
