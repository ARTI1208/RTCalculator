package ru.art2000.calculator.unit_converter;

import android.content.Context;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import ru.art2000.calculator.R;

public class UnitListAdapter extends RecyclerView.Adapter {

    private String[] dimensions;
//    private ArrayList<Float> multipliers;
    private Context mContext;
    private double inp = 1;
    private int curDim = 0;
    private RecyclerView recycler;
    private boolean powerfulConverter;
    private int posit;
    private ViewGroup item;
    private Formulas formulas = new Formulas();
    NumberFormat nf = new DecimalFormat("#.#######");
    @ColorRes int colorAccent;
    int colorDefault;

    UnitListAdapter(Context ctx, String[] dims, int page, boolean isPowerfulConverter) {
        dimensions = dims;
        posit = page;
        mContext = ctx;
        powerfulConverter = isPowerfulConverter;
        TypedValue attr = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.colorAccent, attr, true);
        colorAccent = attr.resourceId;
        mContext.getTheme().resolveAttribute(android.R.attr.textColorPrimary, attr, true);
        colorDefault = attr.resourceId;
    }

    UnitListAdapter(Context ctx, String[] dims, int page, int pos, double value, int accent) {
        dimensions = dims;
        posit = page;
        curDim = pos;
        inp = value;
        mContext = ctx;
        formulas.calc(posit, curDim, inp);
        colorAccent = accent;
    }

    void setCurDim(int pos){
        curDim = pos;
        formulas.calc(posit, pos, inp);
        notifyDataSetChanged();
    }

    void setInputValue(String val){
        inp = Double.valueOf(val.replace(',', '.'));
        formulas.calc(posit, curDim, inp);
        for (int i = 0; i < getItemCount(); i++) {
            if (!recycler.isComputingLayout() && (!powerfulConverter || (powerfulConverter && i != curDim)))
                notifyItemChanged(i);
        }
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

        Holder(final View itemView) {
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            item = (ViewGroup) v;
            menu.setHeaderTitle(mContext.getString(R.string.you_can));
            menu.add(Menu.NONE, 0, Menu.NONE, mContext.getString(R.string.copy));
        }

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recycler = recyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).
                inflate(R.layout.unit_list_item, parent, false);
        if (!powerfulConverter) {
            TextView value = new TextView(mContext);
            value.setTextSize(25);
            value.setGravity(Gravity.START);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.weight = 1;

            value.setLayoutParams(params);

            viewGroup.removeViewAt(0);
            viewGroup.addView(value, 0);
        } else {
//           Log.d ("kjhg", String.valueOf(((EditText)viewGroup.getChildAt(0)).getImeOptions()));
        }
        return new Holder(viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final ViewGroup item = (ViewGroup) holder.itemView;
        TextView tv = item.findViewById(R.id.type);
//        float mult = (getMultItem(curDim) / getMultItem(position));

        TextView zero = (TextView) item.getChildAt(0);

//        if (powerfulConverter) {
//
//
//        } else
//            value.setText(String.valueOf(inp * mult));

        tv.setText(dimensions[position]);
        try {
            if (position == curDim)
                tv.setTextColor(ContextCompat.getColor(mContext, colorAccent));
        } catch (Exception e){}
        if (powerfulConverter) {
            EditText et = (EditText) zero;
            if (position == curDim)
                et.setTextColor(ContextCompat.getColor(mContext, colorAccent));
//            Log.d("Calling from page " + String.valueOf(posit), "with position " + position);
            et.setText(String.valueOf(nf.format(formulas.getResult(posit, position))));
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    curDim = holder.getAdapterPosition();
                    if (s.toString().length() > 0)
                        setInputValue(s.toString());
                    else
                        setInputValue("1");
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } else {
            TextView value = zero;
            value.setText(nf.format(formulas.getResult(posit, position)));
            try {
                if (position == curDim)
                    value.setTextColor(ContextCompat.getColor(mContext, colorAccent));
                else if (colorDefault != 0)
                    value.setTextColor(ContextCompat.getColor(mContext, colorDefault));
            } catch (Exception e){}
        }
    }

    @Override
    public int getItemCount() {
        return dimensions.length;
    }

}
