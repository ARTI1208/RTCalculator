package ru.art2000.calculator.currency_converter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import ru.art2000.calculator.R;
import ru.art2000.extensions.CurrencyItem;
import ru.art2000.helpers.AndroidHelper;
import ru.art2000.helpers.CurrencyValuesHelper;
import ru.art2000.helpers.PrefsHelper;

public class CurrencyListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private int size = 0;
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

    CurrencyListAdapter(Context ctx) {
        mContext = ctx;
        colorAccent = AndroidHelper.getColorAttribute(mContext, R.attr.colorAccent);
        codeTextSizeNormal =
                mContext.getResources().getDimension(R.dimen.currency_list_item_code_normal);
        codeTextSizeHighlighted =
                mContext.getResources().getDimension(R.dimen.currency_list_item_code_highlight);
        if (PrefsHelper.isShouldSaveCurrencyConversion()) {
            highlighted = CurrencyValuesHelper.findByCode(PrefsHelper.getConversionCode());
            inputItemVal = PrefsHelper.getConversionValue();
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recycler = recyclerView;
    }

    void getDataFromDB() {
        size = CurrencyValuesHelper.visibleList.size();
        if (inputItemPos == -1) {
            notifyDataSetChanged();
        } else {
            for (int i = 0; i < size; i++) {
                if (i != inputItemPos)
                    notifyItemChanged(i);
            }
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
        if (inp == null || inp.getAdapterPosition() < 0)
            return;
        inputItemPos = -1;
        inp.value.setText(dot2dig.format(inputItemVal *
                CurrencyValuesHelper.visibleList.get(inp.getAdapterPosition()).rate));
        inp.input.setEnabled(false);
        inp.input.setVisibility(View.GONE);
        inp.value.setTextColor(colorAccent);
        InputMethodManager imm =
                (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(recycler.getWindowToken(), 0);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final ViewGroup item = (ViewGroup) holder.itemView;
        TextView value = ((Holder) holder).value;
        EditText input = ((Holder) holder).input;
        input.setText("");
        input.clearFocus();
        input.setEnabled(false);
        input.setVisibility(View.GONE);
        TextView code = ((Holder) holder).codeView;
        TextView name = ((Holder) holder).nameView;

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

        CurrencyItem currencyItem = CurrencyValuesHelper.visibleList.get(position);

        value.setText(dot2dig.format(inputItemVal * currencyItem.rate));
        code.setText(currencyItem.code);
        name.setText(currencyItem.nameResourceId);

        input.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (highlighted != holder.getAdapterPosition())
                    if (!recycler.isComputingLayout()) {
                        notifyItemChanged(highlighted);
                    } else {
                        return;
                    }
                value.setText("");
                inp = (Holder) holder;
                code.setTextColor(colorAccent);
                input.setTextColor(colorAccent);
                name.setTextColor(colorAccent);
                code.setTextSize(TypedValue.COMPLEX_UNIT_PX, codeTextSizeHighlighted);
                code.setTypeface(null, Typeface.BOLD);
                inputItemPos = holder.getAdapterPosition();
                highlighted = holder.getAdapterPosition();
                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0) {
                            inputItemVal = Double.parseDouble(s.toString()) /
                                    CurrencyValuesHelper.visibleList.get(inputItemPos).rate;
                            for (int i = 0; i < getItemCount(); i++) {
                                if (i != holder.getAdapterPosition()) {
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
        return size;
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
        }
    }
}
