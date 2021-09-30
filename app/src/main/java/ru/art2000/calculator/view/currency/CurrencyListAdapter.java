package ru.art2000.calculator.view.currency;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import ru.art2000.calculator.R;
import ru.art2000.calculator.model.currency.CurrencyItem;
import ru.art2000.calculator.view_model.currency.CurrencyDependencies;
import ru.art2000.calculator.view_model.currency.CurrencyListAdapterModel;
import ru.art2000.extensions.collections.CollectionsKt;
import ru.art2000.extensions.views.SimpleTextWatcher;
import ru.art2000.helpers.AndroidHelper;
import ru.art2000.helpers.PrefsHelper;

public class CurrencyListAdapter extends RecyclerView.Adapter<CurrencyListAdapter.Holder> {

    private final static NumberFormat dot2dig = new DecimalFormat("#.##");

    private final Context mContext;
    private final CurrencyListAdapterModel adapterModel;

    @ColorInt
    private final int colorAccent;
    @ColorInt
    private final int colorDefaultBright;
    @ColorInt
    private final int colorDefaultDimmed;
    private final float codeTextSizeNormal, codeTextSizeHighlighted, valueTextSizeNormal;

    private List<CurrencyItem> data = new ArrayList<>();
    private RecyclerView recycler;

    CurrencyListAdapter(Context context, CurrencyListAdapterModel model) {
        adapterModel = model;
        mContext = context;
        colorAccent = AndroidHelper.getColorAttribute(mContext, com.google.android.material.R.attr.colorSecondary);
        colorDefaultBright = AndroidHelper.getColorAttribute(mContext, com.google.android.material.R.attr.colorOnBackground);
        colorDefaultDimmed = AndroidHelper.getColorAttribute(mContext, com.google.android.material.R.attr.colorOnSurface);

        codeTextSizeNormal =
                mContext.getResources().getDimension(R.dimen.currency_list_item_code_normal);
        codeTextSizeHighlighted =
                mContext.getResources().getDimension(R.dimen.currency_list_item_code_highlight);
        valueTextSizeNormal =
                mContext.getResources().getDimension(R.dimen.currency_list_item_value_normal);

        if (PrefsHelper.isShouldSaveCurrencyConversion()) {
            adapterModel.setLastInputItemValue(PrefsHelper.getConversionValue());
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recycler = recyclerView;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewGroup = LayoutInflater.from(mContext).
                inflate(R.layout.item_currency_converter_list, parent, false);
        return new Holder(viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {

        if (adapterModel.getLastInputItemPosition() == position) {
            highlightHolderElements(holder);
        } else {
            removeEditText(holder);
            removeHolderElementsHighlighting(holder);
        }

        CurrencyItem currencyItem = data.get(position);
        holder.bind(currencyItem);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    void setNewData(@NonNull List<CurrencyItem> newData) {

        if (adapterModel.getLastInputItemPosition() == -1) {
            for (int i = 0; i < newData.size(); i++) {
                CurrencyItem newItem = newData.get(i);
                if (newItem.code.equals(PrefsHelper.getConversionCode())) {
                    adapterModel.setLastInputItemPosition(i);
                    break;
                }
            }
        }

        if (data == null || data.isEmpty()) {
            data = newData;
            notifyItemRangeInserted(0, newData.size());
        } else {
            DiffUtil.DiffResult result = CollectionsKt.calculateDiff(data, newData);

            data = newData;
            result.dispatchUpdatesTo(this);
        }
    }

    void removeEditText() {
        if (data.isEmpty()) return;
        Holder holder = (Holder)
                recycler.findViewHolderForAdapterPosition(adapterModel.getLastInputItemPosition());

        removeEditText(holder);
    }

    private void removeEditText(Holder holder) {
        if (holder == null) {
            return;
        }

        holder.value.setVisibility(View.VISIBLE);
        holder.input.setVisibility(View.GONE);
        holder.input.setEnabled(false);
        holder.input.getText().clear();
        holder.input.clearFocus();

        if (holder.getBindingAdapterPosition() != adapterModel.getLastInputItemPosition())
            return;

        InputMethodManager imm =
                (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(recycler.getWindowToken(), 0);
        }
    }

    private void highlightHolderElements(Holder holder) {
        if (holder == null) return;

        holder.codeView.setTextColor(colorAccent);
        holder.nameView.setTextColor(colorAccent);
        holder.value.setTextColor(colorAccent);
        holder.codeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, codeTextSizeHighlighted);
        holder.codeView.setTypeface(null, Typeface.BOLD);
        holder.value.setTextSize(TypedValue.COMPLEX_UNIT_PX, codeTextSizeHighlighted);
        holder.value.setTypeface(null, Typeface.BOLD);
    }

    private void removeHolderElementsHighlighting(Holder holder) {
        if (holder == null)
            return;

        holder.codeView.setTextColor(colorDefaultBright);
        holder.nameView.setTextColor(colorDefaultDimmed);
        holder.value.setTextColor(colorDefaultDimmed);
        holder.codeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, codeTextSizeNormal);
        holder.codeView.setTypeface(null, Typeface.NORMAL);
        holder.value.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueTextSizeNormal);
        holder.value.setTypeface(null, Typeface.NORMAL);
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView codeView, nameView, value;
        EditText input;

        Holder(final View itemView) {
            super(itemView);
            codeView = itemView.findViewById(R.id.currency_code);
            nameView = itemView.findViewById(R.id.currency_name);
            value = itemView.findViewById(R.id.currency_value);
            input = itemView.findViewById(R.id.currency_input_value);

            itemView.setOnClickListener(v -> {

                int holderPosition = getBindingAdapterPosition();

                if (holderPosition != adapterModel.getLastInputItemPosition()) {
                    Holder previousHolder = (Holder)
                            recycler.findViewHolderForAdapterPosition(adapterModel.getLastInputItemPosition());
                    removeHolderElementsHighlighting(previousHolder);
                    adapterModel.setLastInputItemPosition(getBindingAdapterPosition());
                    highlightHolderElements(this);
                }
                value.setVisibility(View.GONE);
                input.setVisibility(View.VISIBLE);
                input.setEnabled(true);
                input.requestFocus();

                InputMethodManager keyboard =
                        (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

                if (keyboard != null) {

                    keyboard.showSoftInput(input, 0);
                }
            });

            input.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    removeEditText(this);
                }
            });

            input.addTextChangedListener(new SimpleTextWatcher() {

                @Override
                public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        int holderPosition = getBindingAdapterPosition();
                        adapterModel.setLastInputItemValue(Double.parseDouble(s.toString()) /
                                data.get(holderPosition).rate);

                        for (int i = 0; i < getItemCount(); i++) {
                            Holder holder = (Holder) recycler.findViewHolderForAdapterPosition(i);
                            if (holder != null) {
                                CurrencyItem item = data.get(i);
                                holder.value.setText(dot2dig.format(
                                        adapterModel.getLastInputItemValue() * item.rate));
                            }
                        }
                    }
                }

                @Override
                public void afterTextChanged(@NonNull Editable s) {
                    if (PrefsHelper.isShouldSaveCurrencyConversion())
                        PrefsHelper.putConversionValues(codeView.getText().toString(),
                                adapterModel.getLastInputItemValue());
                }
            });
        }

        void bind(CurrencyItem currencyItem) {
            value.setText(dot2dig.format(adapterModel.getLastInputItemValue() * currencyItem.rate));
            codeView.setText(currencyItem.code);
            nameView.setText(CurrencyDependencies.getNameIdentifierForCode(mContext, currencyItem.code));
        }
    }
}
