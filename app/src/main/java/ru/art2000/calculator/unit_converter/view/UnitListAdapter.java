package ru.art2000.calculator.unit_converter.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import ru.art2000.calculator.R;
import ru.art2000.calculator.unit_converter.model.UnitConverterItem;
import ru.art2000.helpers.AndroidHelper;
import ru.art2000.helpers.GeneralHelper;

public class UnitListAdapter extends RecyclerView.Adapter<UnitListAdapter.UnitItemHolder> {

    double inp = 1;
    int curDim = 0;

    MutableLiveData<Pair<Integer, Integer>> selectedPosition = new MutableLiveData<>(new Pair<>(0, 0));

    UnitPageFragment fragment;
    private int colorAccent;
    private int colorDefault;
    private Context mContext;
    private RecyclerView recycler;
    private boolean powerfulConverter;
    private UnitConverterItem[] data;

    UnitListAdapter(Context ctx, UnitConverterItem[] items, boolean isPowerfulConverter) {
        data = items;
        mContext = ctx;
        powerfulConverter = isPowerfulConverter;
        if (data[0].getCurrentValue() == 0.0)
            setValue(0, 1);
        init();
    }

    UnitListAdapter(Context ctx, UnitConverterItem[] items, int pos) {
        data = items;
        curDim = pos;
        mContext = ctx;
        init();
    }

    private void init() {
        colorAccent = AndroidHelper.getColorAttribute(mContext, R.attr.colorAccent);
        colorDefault = AndroidHelper.getColorAttribute(mContext, android.R.attr.textColorSecondary);
    }

    void setValue(int position, double value) {
        curDim = position;
        UnitConverterItem from = data[position];
        from.setValue(value);

        for (int i = 0; i < getItemCount(); i++) {

            if (i == position)
                continue;

            double convertedValue = data[i].convert(from);

            if (recycler == null)
                continue;

            UnitItemHolder holder = (UnitItemHolder) recycler.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                String v = doubleToString(convertedValue);
                holder.dimensionValueView.setText(v);
            }
        }
    }

    void setValue(int position, String value) {
        double doubleValue = Double.parseDouble(value.replace(',', '.'));
        setValue(position, doubleValue);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recycler = recyclerView;
        selectedPosition.observeForever(pair -> {
            setTextColors((UnitItemHolder) recycler.findViewHolderForAdapterPosition(pair.first), false);
            setTextColors((UnitItemHolder) recycler.findViewHolderForAdapterPosition(pair.second), true);
        });
    }

    @NonNull
    @Override
    public UnitItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).
                inflate(R.layout.item_unit_converter_list, parent, false);
        if (!powerfulConverter) {
            TextView value = new TextView(mContext);
            value.setId(R.id.value);
            value.setTextSize(25);
            value.setGravity(Gravity.START);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.weight = 1;

            value.setLayoutParams(params);

            viewGroup.removeViewAt(0);
            viewGroup.addView(value, 0);
        }
        return new UnitItemHolder(viewGroup);
    }

    private void setTextColors(UnitItemHolder holder, boolean isSelected) {
        if (holder == null)
            return;

        if (isSelected) {
            holder.dimensionNameView.setTextColor(colorAccent);
            holder.dimensionValueView.setTextColor(colorAccent);
        } else {
            holder.dimensionNameView.setTextColor(colorDefault);
            holder.dimensionValueView.setTextColor(colorDefault);
        }
    }

    void requestFocusForCurrent() {
        if (!powerfulConverter) {
            return;
        }

        UnitItemHolder previousHolder = (UnitItemHolder) recycler.findViewHolderForLayoutPosition(curDim);
        if (previousHolder != null) {
            previousHolder.dimensionValueView.requestFocus();
        }
    }

    private String doubleToString(double d) {
        return GeneralHelper.resultNumberFormat.format(d);
    }

    @Override
    public void onBindViewHolder(@NonNull final UnitItemHolder holder, final int position) {
        TextView dimensionNameView = holder.dimensionNameView;
        TextView dimensionValueView = holder.dimensionValueView;

        dimensionNameView.setText(data[position].getNameResourceId());
        dimensionValueView.setText(doubleToString(data[position].getCurrentValue()));

        if (powerfulConverter) {
            EditText editValueView = (EditText) dimensionValueView;

            editValueView.setOnFocusChangeListener((view, isFocused) -> {
                if (isFocused) {
                    editValueView.setSelection(editValueView.getText().length());
                    if (fragment != null && fragment.isCurrentPage) {
                        selectedPosition.setValue(new Pair<>(
                                Objects.requireNonNull(selectedPosition.getValue()).second,
                                holder.getBindingAdapterPosition()));
                    }
                }
            });

            editValueView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (editValueView.hasFocus()) {
                        String stringValue = s.toString();

                        if (stringValue.length() == 0)
                            stringValue = "1";

                        setValue(holder.getBindingAdapterPosition(), stringValue);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        setTextColors(holder, position == curDim);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class UnitItemHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView dimensionNameView;
        TextView dimensionValueView;

        UnitItemHolder(final View itemView) {
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);
            dimensionNameView = itemView.findViewById(R.id.type);
            dimensionValueView = itemView.findViewById(R.id.value);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(mContext.getString(R.string.you_can));
            menu.add(Menu.NONE, 0, Menu.NONE, mContext.getString(R.string.context_menu_copy));
        }
    }
}
