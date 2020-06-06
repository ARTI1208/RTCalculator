package ru.art2000.calculator.currency_converter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.art2000.calculator.R;
import ru.art2000.extensions.CurrencyItemWrapper;
import ru.art2000.extensions.OnListChangeListener;
import ru.art2000.helpers.CurrencyValuesHelper;

public class CurrenciesAddFragment extends Fragment {

    AddCurrenciesAdapter adapter;
    @Px
    int recyclerViewBottomPadding;
    private View v = null;
    private RecyclerView recycler;
    private TextView emptyView;
    private Context mContext;
    private EditCurrenciesActivity parent;

    void scrollToTop() {
        recycler.smoothScrollToPosition(0);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (v == null) {
            mContext = getActivity();
            parent = (EditCurrenciesActivity) getActivity();
            v = inflater.inflate(R.layout.modify_currencies_layout, null);
            emptyView = v.findViewById(R.id.empty_tv);
            recycler = v.findViewById(R.id.modify_currencies_list);
            recycler.setPadding(0, 0, 0, recyclerViewBottomPadding);
            LinearLayoutManager llm = new LinearLayoutManager(mContext);
            llm.setOrientation(RecyclerView.VERTICAL);
            recycler.setLayoutManager(llm);
            setNewList(CurrencyValuesHelper.hiddenList);
        }
        return v;
    }

    private ArrayList<CurrencyItemWrapper> searchByQuery(String query) {
        return CurrencyValuesHelper.findAllItems(mContext, CurrencyValuesHelper.hiddenList, query);
    }

    void filterList(String query) {
        adapter.filterData(query);
    }

    private void setNewList(ArrayList<CurrencyItemWrapper> list) {
        if (adapter != null) {
            adapter.setNewData(list);
        } else {
            adapter = new AddCurrenciesAdapter(list);
        }
        if (recycler.getAdapter() == null) {
            recycler.setAdapter(adapter);
        }
        toggleEmptyView();
    }

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
        if (parent.barSearchView.getQuery().length() > 0 && CurrencyValuesHelper.hiddenList.size() > 0) {
            return getString(R.string.empty_text_no_currencies_found);
        } else {
            return getString(R.string.empty_text_all_currencies_added);
        }
    }

    void removeFromCurrentList(ArrayList<CurrencyItemWrapper> list) {
        adapter.currentList.removeAll(list);
    }

    private void updateCheckState(List<CurrencyItemWrapper> list) {
        for (int i = 0; i < list.size(); ++i) {
            RecyclerView.ViewHolder holder = recycler.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                ((AddCurrenciesAdapter.Holder) holder).checkBox.setChecked(list.get(i).isSelected);
            }
        }
    }

    class AddCurrenciesAdapter extends RecyclerView.Adapter<AddCurrenciesAdapter.Holder> {

        List<CurrencyItemWrapper> currentList;
        String currentQuery = "";

        int size;
        int selectedCount;

        boolean shouldUpdateVisibility = true;

        AddCurrenciesAdapter(@NonNull ArrayList<CurrencyItemWrapper> list) {
            currentList = list;
            size = currentList.size();
            countSelectedItems();
            CurrencyValuesHelper.hiddenListChangeListener = new OnListChangeListener<CurrencyItemWrapper>() {
                @Override
                public void onItemsAdded(ArrayList<CurrencyItemWrapper> addedItems) {
                }

                @Override
                public void onItemsRemoved(ArrayList<CurrencyItemWrapper> removedItems) {
                    selectedCount = 0;
                }
            };
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
            holder.checkBox.setChecked(currentList.get(pos).isSelected);
        }

        @Override
        public void onBindViewHolder(@NonNull AddCurrenciesAdapter.Holder holder, int position) {
            CurrencyItemWrapper currencyItem = currentList.get(position);
            holder.code.setText(currencyItem.code);
            holder.name.setText(currencyItem.nameResourceId);

            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(currencyItem.isSelected);
            Log.d("itemBind", currencyItem.code + "||" + currencyItem.isSelected);
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!currencyItem.isSelected) {
                        currencyItem.isSelected = true;
                        ++selectedCount;
                    }
                } else {
                    if (currencyItem.isSelected) {
                        currencyItem.isSelected = false;
                        --selectedCount;
                    }
                }
                if (shouldUpdateVisibility) {
                    parent.toggleElementsVisibility();
                }
            });

            holder.itemView.setOnClickListener(v ->
                    holder.checkBox.performClick());
        }

        @Override
        public int getItemCount() {
            return size;
        }

        ArrayList<CurrencyItemWrapper> getSelectedItems() {
            ArrayList<CurrencyItemWrapper> selectedItems = new ArrayList<>();
            for (CurrencyItemWrapper itemWrapper : currentList) {
                if (itemWrapper.isSelected) {
                    selectedItems.add(itemWrapper);
                }
            }
            return selectedItems;
        }

        private void countSelectedItems() {
            selectedCount = 0;
            for (CurrencyItemWrapper currencyItemWrapper : currentList) {
                if (currencyItemWrapper.isSelected) {
                    ++selectedCount;
                }
            }
        }

        void deselectAll() {
            for (CurrencyItemWrapper currencyItemWrapper : currentList) {
                currencyItemWrapper.isSelected = false;
            }
            shouldUpdateVisibility = false;
            updateCheckState(currentList);
            shouldUpdateVisibility = true;
            selectedCount = 0;
        }

        void selectAll() {
            for (CurrencyItemWrapper currencyItemWrapper : currentList) {
                currencyItemWrapper.isSelected = true;
            }
            shouldUpdateVisibility = false;
            updateCheckState(currentList);
            shouldUpdateVisibility = true;
            selectedCount = size;
        }

        boolean isSomethingSelected() {
            return selectedCount != 0;
        }

        boolean isAllSelected() {
            return selectedCount == size;
        }

        void setNewData() {
            currentList = CurrencyValuesHelper.hiddenList;
            size = currentList.size();
            countSelectedItems();
            notifyDataSetChanged();
            toggleEmptyView();
        }

        void setNewData(List<CurrencyItemWrapper> newList) {
            currentList = newList;
            size = currentList.size();
            countSelectedItems();
            notifyDataSetChanged();
            toggleEmptyView();
            parent.toggleElementsVisibility();
        }

        void reFilterData() {
            filterData(currentQuery);
        }

        void filterData(String filterString) {
            new Thread(() -> {
                ArrayList<CurrencyItemWrapper> list = searchByQuery(filterString);
                currentQuery = filterString;
                parent.runOnUiThread(() -> setNewData(list));
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
