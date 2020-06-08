package ru.art2000.calculator.currency_converter.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import ru.art2000.calculator.R;
import ru.art2000.calculator.currency_converter.model.CurrencyItem;
import ru.art2000.calculator.currency_converter.view_model.CurrencyDependencies;
import ru.art2000.helpers.AndroidHelper;
import ru.art2000.helpers.CurrencyValuesHelper;
import ru.art2000.helpers.PrefsHelper;

public class CurrencyListAdapter extends RecyclerView.Adapter<CurrencyListAdapter.Holder> {

    private Context mContext;
    private int inputItemPos = -1;
    private int highlighted = 0;
    private double inputItemVal = 1;
    private NumberFormat dot2dig = new DecimalFormat("#.##");
    private RecyclerView recycler;
    private ColorStateList csl = null;
    private Holder inp;
    @ColorInt
    private int colorAccent;
    private float codeTextSizeNormal;
    private float codeTextSizeHighlighted;

    List<CurrencyItem> data = new ArrayList<>();

    CurrencyListAdapter(Context ctx) {
        mContext = ctx;
        colorAccent = AndroidHelper.getColorAttribute(mContext, R.attr.colorAccent);
        codeTextSizeNormal =
                mContext.getResources().getDimension(R.dimen.currency_list_item_code_normal);
        codeTextSizeHighlighted =
                mContext.getResources().getDimension(R.dimen.currency_list_item_code_highlight);
        if (PrefsHelper.isShouldSaveCurrencyConversion()) {
//            highlighted = CurrencyValuesHelper.findByCode(PrefsHelper.getConversionCode());
            inputItemVal = PrefsHelper.getConversionValue();
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recycler = recyclerView;
    }

    public void setNewData(@NonNull List<CurrencyItem> newData) {

        for (int i = 0; i < newData.size(); i++) {
            CurrencyItem newItem = newData.get(i);
            if (newItem.code.equals(PrefsHelper.getConversionCode())) {
                highlighted = i;
                break;
            }
        }

        if (data == null || data.isEmpty()) {
            data = newData;
            notifyItemRangeInserted(0, newData.size());
        } else if (data.size() != newData.size() || !data.containsAll(newData)) {
            DiffUtil.DiffResult result =
                    DiffUtil.calculateDiff(CurrencyDependencies.getDiffCallback(data, newData));

            data = newData;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewGroup = LayoutInflater.from(mContext).
                inflate(R.layout.item_currency_converter_list, parent, false);
        return new Holder(viewGroup);
    }

    void removeEditText() {
        int t = inputItemPos;
        inputItemPos = -1;
        if (!recycler.isComputingLayout()) {
            notifyItemChanged(t);
        }
        InputMethodManager imm =
                (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(recycler.getWindowToken(), 0);
    }

    public void removeEditText2() {
        if (inp == null || inp.getBindingAdapterPosition() < 0)
            return;
        inputItemPos = -1;
        inp.value.setText(dot2dig.format(inputItemVal *
                data.get(inp.getBindingAdapterPosition()).rate));
        inp.input.setEnabled(false);
        inp.input.setVisibility(View.GONE);
        inp.value.setTextColor(colorAccent);
        InputMethodManager imm =
                (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(recycler.getWindowToken(), 0);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        final ViewGroup item = (ViewGroup) holder.itemView;
        TextView value = holder.value;
        EditText input = holder.input;
        input.setText("");
        input.clearFocus();
        input.setEnabled(false);
        input.setVisibility(View.GONE);
        TextView code = holder.codeView;
        TextView name = holder.nameView;

        if (csl == null)
            csl = code.getTextColors();

        if (inputItemPos == position) {
            inputItemPos = -1;
        }



        if (highlighted == position) {
            code.setTextColor(colorAccent);
            name.setTextColor(colorAccent);
            value.setTextColor(colorAccent);
            code.setTextSize(TypedValue.COMPLEX_UNIT_PX, codeTextSizeHighlighted);
            code.setTypeface(null, Typeface.BOLD);
        } else {
            code.setTextColor(csl);
            name.setTextColor(csl);
            value.setTextColor(csl);
            code.setTextSize(TypedValue.COMPLEX_UNIT_PX, codeTextSizeNormal);
            code.setTypeface(null, Typeface.NORMAL);
        }

        CurrencyItem currencyItem = data.get(position);

        value.setText(dot2dig.format(inputItemVal * currencyItem.rate));
        code.setText(currencyItem.code);

        name.setText(CurrencyDependencies.getNameIdentifierForCode(mContext, currencyItem.code));

        input.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (highlighted != holder.getBindingAdapterPosition())
                    if (!recycler.isComputingLayout()) {
                        notifyItemChanged(highlighted);
                    } else {
                        return;
                    }
                value.setText("");
                inp = holder;
                code.setTextColor(colorAccent);
                input.setTextColor(colorAccent);
                name.setTextColor(colorAccent);
                code.setTextSize(TypedValue.COMPLEX_UNIT_PX, codeTextSizeHighlighted);
                code.setTypeface(null, Typeface.BOLD);
                inputItemPos = holder.getBindingAdapterPosition();
                highlighted = holder.getBindingAdapterPosition();
                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0) {
                            inputItemVal = Double.parseDouble(s.toString()) /
                                    data.get(inputItemPos).rate;
                            for (int i = 0; i < getItemCount(); i++) {
                                if (i != holder.getBindingAdapterPosition()) {
                                    notifyItemChanged(i);
                                }
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (PrefsHelper.isShouldSaveCurrencyConversion())
                            PrefsHelper.putConversionValues(code.getText().toString(),
                                    inputItemVal);
                    }
                });
                InputMethodManager keyboard =
                        (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (keyboard != null) {
                    keyboard.showSoftInput(input, 0);
                }
            } else {
                if (!recycler.isComputingLayout()) {
                    notifyItemChanged(position);
                }
            }
        });

        item.setOnClickListener(v -> {
            input.setVisibility(View.VISIBLE);
            input.setEnabled(true);
            input.requestFocus();
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        TextView codeView, nameView, value;
        EditText input;

        Holder(final View itemView) {
            super(itemView);
            codeView = itemView.findViewById(R.id.currency_code);
            nameView = itemView.findViewById(R.id.currency_name);
            value = itemView.findViewById(R.id.currency_value);
            input = itemView.findViewById(R.id.currency_input_value);
        }
    }
}
