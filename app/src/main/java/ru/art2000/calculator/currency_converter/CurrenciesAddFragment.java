package ru.art2000.calculator.currency_converter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.ArrayList;
import ru.art2000.calculator.R;
import ru.art2000.extensions.CurrencyItem;
import ru.art2000.extensions.CurrencyValues;

public class CurrenciesAddFragment extends Fragment {

    private View v = null;
    private RecyclerView list;
    private Context mContext;
    AddCurrenciesAdapter adapter;
    private EditShownCurrencies parent;
    public int selectionState = 0;
    public int previousSelectionState = -1;

    public CurrenciesAddFragment() {}

    void scrollToTop(){
        list.smoothScrollToPosition(0);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (v == null) {
            mContext = getActivity();
            parent = (EditShownCurrencies) getActivity();
            adapter = new AddCurrenciesAdapter();
            v = inflater.inflate(R.layout.modify_currencies_layout, null);
            list = v.findViewById(R.id.modify_currencies_list);
            LinearLayoutManager llm = new LinearLayoutManager(mContext);
            llm.setOrientation(RecyclerView.VERTICAL);
            list.setLayoutManager(llm);
            list.setAdapter(adapter);
        }
        return v;
    }

    class AddCurrenciesAdapter extends RecyclerView.Adapter {

        ArrayList<CurrencyItem> itemsToAdd = new ArrayList<>();
        int size = CurrencyValues.hiddenList.size();

        AddCurrenciesAdapter() {
            super();
            setNewData();
        }

        @NonNull
        @Override
        @SuppressLint("InflateParams")
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(mContext).inflate(R.layout.add_currencies_list_item, null);
            return new Holder(item);
        }

        void deselectAll(){
            itemsToAdd.clear();
//            parent.setFABVisibility();
            notifyDataSetChanged();
        }

        void selectAll(){
            itemsToAdd.clear();
            itemsToAdd.addAll(CurrencyValues.hiddenList);
//            parent.setFABVisibility();
            notifyDataSetChanged();
        }

        boolean isSomethingSelected(){
            return itemsToAdd.size() != 0;
        }

        boolean isAllSelected(){
            return itemsToAdd.size() == CurrencyValues.hiddenList.size();
        }

        void setNewData(){
            size = CurrencyValues.hiddenList.size();
            for (CurrencyItem it: itemsToAdd) {
                Log.d("Codeddeded", it.code);
            }
            notifyDataSetChanged();
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
                checkBox.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            View item = holder.itemView;
            TextView code = ((Holder) holder).code;
            TextView name = ((Holder) holder).name;
            CurrencyItem currencyItem = CurrencyValues.hiddenList.get(position);
            code.setText(currencyItem.code);
            name.setText(currencyItem.nameResourceId);
            CheckBox check = ((Holder) holder).checkBox;
            check.setOnCheckedChangeListener((buttonView, isChecked) -> {

                    if (isChecked && !itemsToAdd.contains(currencyItem))
                        itemsToAdd.add(currencyItem);
                    else if (!isChecked)
                        itemsToAdd.remove(currencyItem);
                    if (isAllSelected()) {
                        previousSelectionState = selectionState;
                        selectionState = 2;
                    } else if (isSomethingSelected()) {
                        previousSelectionState = selectionState;
                        selectionState = 1;
                    } else {
                        previousSelectionState = selectionState;
                        selectionState = 0;
                    }
//                    if (previousSelectionState != selectionState)
                        parent.setFABVisibility();
//                }

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
    }

}
