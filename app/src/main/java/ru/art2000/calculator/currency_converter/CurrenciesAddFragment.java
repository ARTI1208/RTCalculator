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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.art2000.calculator.R;
import ru.art2000.extensions.CurrencyItem;
import ru.art2000.helpers.CurrencyValuesHelper;

public class CurrenciesAddFragment extends Fragment {

    AddCurrenciesAdapter adapter;

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
            recycler = v.findViewById(R.id.modify_currencies_list);
            emptyView = v.findViewById(R.id.empty_tv);

            LinearLayoutManager llm = new LinearLayoutManager(mContext);
            llm.setOrientation(RecyclerView.VERTICAL);
            recycler.setLayoutManager(llm);
            Log.d("Addi", "1");
            setNewList(CurrencyValuesHelper.hiddenList);
        }
        return v;
    }

    ArrayList<CurrencyItem> searchByQuery(String query) {
        return CurrencyValuesHelper.findAllItems(mContext, CurrencyValuesHelper.hiddenList, query);
    }

    void filterList() {
        new Thread(() -> {
            ArrayList<CurrencyItem> list = searchByQuery(parent.barSearchView.getQuery().toString());
            parent.runOnUiThread(() -> setNewList(list));
        }).start();
    }

    void setNewList(ArrayList<CurrencyItem> list) {
        if (adapter != null) {
            if (list == null || adapter.currentList == null || adapter.currentList.hashCode() == list.hashCode()) {
                return;
            }
            ArrayList<CurrencyItem> selected = adapter.itemsToAdd;
            adapter.currentList = list;
//            adapter = new AddCurrenciesAdapter(list);
            adapter.itemsToAdd = selected;
            adapter.setNewData();
        } else {
            adapter = new AddCurrenciesAdapter(list);
        }
        recycler.setAdapter(adapter);
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

    public void removeFromCurrentList(ArrayList<CurrencyItem> list) {
        adapter.currentList.removeAll(list);
    }

    class AddCurrenciesAdapter extends RecyclerView.Adapter {

        ArrayList<CurrencyItem> currentList;
        ArrayList<CurrencyItem> itemsToAdd = new ArrayList<>();
        int size;

        AddCurrenciesAdapter(ArrayList<CurrencyItem> list) {
            super();
            Log.d("Addi", "Init with " + list.hashCode());
            currentList = list;
            size = currentList.size();
            setNewData();
        }

        @NonNull
        @Override
        @SuppressLint("InflateParams")
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(mContext).inflate(R.layout.item_add_currencies_list, null);
            return new Holder(item);
        }

        void deselectAll() {
            itemsToAdd.clear();
            notifyDataSetChanged();
        }

        void selectAll() {
            itemsToAdd.clear();
            itemsToAdd.addAll(currentList);
            notifyDataSetChanged();
        }

        boolean isSomethingSelected() {
            return itemsToAdd.size() != 0;
        }

        boolean isAllSelected() {
            return itemsToAdd.size() == currentList.size();
        }

        void setNewData() {
            size = currentList.size();
            notifyDataSetChanged();
            toggleEmptyView();
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            View item = holder.itemView;
            TextView code = ((Holder) holder).code;
            TextView name = ((Holder) holder).name;
            CurrencyItem currencyItem = currentList.get(position);
            code.setText(currencyItem.code);
            name.setText(currencyItem.nameResourceId);
            CheckBox check = ((Holder) holder).checkBox;
            check.setOnCheckedChangeListener((buttonView, isChecked) -> {

                if (isChecked && !itemsToAdd.contains(currencyItem))
                    itemsToAdd.add(currencyItem);
                else if (!isChecked)
                    itemsToAdd.remove(currencyItem);
                parent.toggleElementsVisibility();
            });
            if (itemsToAdd.contains(currencyItem))
                check.setChecked(true);

            if (!itemsToAdd.contains(currencyItem))
                check.setChecked(false);

            item.setOnClickListener(v ->
                    check.performClick());
        }

        @Override
        public int getItemCount() {
            return size;
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
