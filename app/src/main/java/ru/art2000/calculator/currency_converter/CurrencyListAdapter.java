package ru.art2000.calculator.currency_converter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import ru.art2000.calculator.Helper;
import ru.art2000.calculator.R;
import ru.art2000.calculator.settings.PrefsHelper;
import ru.art2000.extensions.CurrencyItem;
import ru.art2000.extensions.CurrencyValues;

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
    @ColorRes private int colorAccent;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recycler = recyclerView;
    }

    CurrencyListAdapter(Context ctx) {
        mContext = ctx;
        TypedValue accentValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.colorAccent, accentValue, true);
        colorAccent = accentValue.resourceId;
        if (PrefsHelper.isShouldSaveCurrencyConversion()) {
            highlighted = CurrencyValues.findByCode(PrefsHelper.getConversionCode());
            inputItemVal = PrefsHelper.getConversionValue();
        }
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

    void getDataFromDB() {
        size = CurrencyValues.visibleList.size();
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
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).
                inflate(R.layout.currency_list_item, parent, false);
        return new Holder(viewGroup);
    }

    void removeEditText() {
        int t = inputItemPos;
        inputItemPos = -1;
//        if (!recycler.isComputingLayout()) {
//            notifyItemChanged(t);
//        }
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(recycler.getWindowToken(), 0);
    }

    public void removeEditText2() {
        if (inp == null || inp.getAdapterPosition() < 0)
            return;
        Log.d("ffffff", "remoo");
        int t = inputItemPos;
        inputItemPos = -1;
        inp.value.setText(dot2dig.format(inputItemVal *
                CurrencyValues.visibleList.get(inp.getAdapterPosition()).rate));
        inp.input.setEnabled(false);
        inp.input.setVisibility(View.GONE);
        inp.value.setTextColor(ContextCompat.getColor(mContext, colorAccent));
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
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
        TextView code = ((Holder) holder).codeView;
        TextView name = ((Holder) holder).nameView;
//        code.clearFocus();

//        if ()


        if (csl == null)
            csl = code.getTextColors();

        if (inputItemPos == position) {
            inputItemPos = -1;
        }

Log.d("binnnd", String.valueOf(position));

        if (highlighted == position) {
            Log.d("t2", String.valueOf(highlighted));
            Log.d("pos", String.valueOf(position));
            code.setTextColor(ContextCompat.getColor(mContext, colorAccent));
            name.setTextColor(ContextCompat.getColor(mContext, colorAccent));
            value.setTextColor(ContextCompat.getColor(mContext, colorAccent));
            code.setTypeface(null, Typeface.BOLD);
            code.setTextSize(Helper.sp2px(mContext, 8f));
        } else {
            Log.d("high", String.valueOf(highlighted));
            Log.d("pos", String.valueOf(position));
            code.setTextSize(Helper.sp2px(mContext, 7f));
            code.setTextColor(csl);
            name.setTextColor(csl);
            value.setTextColor(csl);
            code.setTypeface(null, Typeface.NORMAL);
        }

        CurrencyItem currencyItem = CurrencyValues.visibleList.get(position);

            value.setText(dot2dig.format(inputItemVal * currencyItem.rate));
            code.setText(currencyItem.code);
            name.setText(currencyItem.nameResourceId);

        input.setOnFocusChangeListener((v, hasFocus) -> {
//            if (inputItemPos * holder.getAdapterPosition() > 0)
                if (hasFocus) {
                    if (highlighted != holder.getAdapterPosition())
                        if (!recycler.isComputingLayout()) {
                            Log.d("NNNNN", "FFFFFFF");
                            Log.d("NNNNN", String.valueOf(inputItemPos));
                            Log.d("NNNNN", String.valueOf(holder.getAdapterPosition()));
                            notifyItemChanged(highlighted);
                        }
                    else
                        return;
                    value.setText("");
                    inp = (Holder) holder;
                    code.setTextColor(ContextCompat.getColor(mContext, colorAccent));
                    name.setTextColor(ContextCompat.getColor(mContext, colorAccent));
                    code.setTypeface(null, Typeface.BOLD);
                    code.setTextSize(Helper.sp2px(mContext, 8f));
                    inputItemPos = holder.getAdapterPosition();
                    highlighted = holder.getAdapterPosition();
                    input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (!s.toString().equals("")) {
                                inputItemVal = Double.parseDouble(s.toString()) / CurrencyValues.visibleList.get(inputItemPos).rate;
                                for (int i = 0; i < getItemCount(); i++) {
                                    if (i != holder.getAdapterPosition()) {
                                        Log.d("OOOO", "FFFFFFF");
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
                    InputMethodManager keyboard = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (keyboard != null) {
                        keyboard.showSoftInput(input, 0);
                    }
                } else {
                    if (!recycler.isComputingLayout())
                        notifyItemChanged(position);
//                    onBindViewHolder(holder, position);
//                    code.setTextSize(Helper.sp2px(mContext, 7f));
//                    code.setTextColor(csl);
//                    name.setTextColor(csl);
//                    code.setTypeface(null, Typeface.NORMAL);
                }
        });

        item.setOnClickListener(v ->{
            input.setVisibility(View.VISIBLE);
            input.setEnabled(true);
            input.requestFocus();
    });
    }

    @Override
    public int getItemCount() {
        return size;
    }

}
