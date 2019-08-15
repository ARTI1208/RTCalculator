package ru.art2000.calculator.unit_converter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.recyclerview.widget.RecyclerView;

import ru.art2000.calculator.R;
import ru.art2000.helpers.AndroidHelper;
import ru.art2000.helpers.GeneralHelper;

public class UnitListAdapter extends RecyclerView.Adapter {

    double inp = 1;
    int curDim = 0;
    UnitPageFragment fragment;
    private int colorAccent;
    private int colorDefault;
    private String[] dimensions;
    private Context mContext;
    private RecyclerView recycler;
    private boolean powerfulConverter;
    private int pagePosition;
    private Formulas formulas = new Formulas();

    UnitListAdapter(Context ctx, String[] dims, int page, boolean isPowerfulConverter) {
        dimensions = dims;
        pagePosition = page;
        mContext = ctx;
        powerfulConverter = isPowerfulConverter;
        colorAccent = AndroidHelper.getColorAttribute(mContext, R.attr.colorAccent);
        colorDefault = AndroidHelper.getColorAttribute(mContext, android.R.attr.textColorSecondary);
    }

    UnitListAdapter(Context ctx, String[] dims, int page, int pos, double value) {
        dimensions = dims;
        pagePosition = page;
        curDim = pos;
        inp = value;
        mContext = ctx;
        formulas.calc(pagePosition, curDim, inp);
        colorAccent = AndroidHelper.getColorAttribute(mContext, R.attr.colorAccent);
        colorDefault = AndroidHelper.getColorAttribute(mContext, android.R.attr.textColorSecondary);
    }

    void setCurDim(int pos) {
        int previousDimension = curDim;
        curDim = pos;
        Log.d(createTag("ChngDim"), "2||was " + previousDimension + ", now " + curDim);
        formulas.calc(pagePosition, pos, inp);
        notifyDataSetChanged();
    }

    void setInputValue(String val) {
        inp = Double.valueOf(val.replace(',', '.'));
        formulas.calc(pagePosition, curDim, inp);
        for (int i = 0; i < getItemCount(); i++) {
            if (!recycler.isComputingLayout() && (!powerfulConverter || i != curDim))
                notifyItemChanged(i);
        }
    }

    private String createTag(String text) {
        return text + "_" + pagePosition;
    }

    void setValueAndDimension(double value, int dimension, boolean redraw) {

        int previousDimension = curDim;
        inp = value;
        curDim = dimension;
        Log.d(createTag("ChngDim"), "3||was " + previousDimension + ", now " + curDim);
        if (redraw) {
            formulas.calc(pagePosition, curDim, inp);
            for (int i = 0; i < getItemCount(); i++) {
                if (!recycler.isComputingLayout() && (!powerfulConverter || i != curDim))
                    notifyItemChanged(i);
            }
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
                inflate(R.layout.item_unit_converter_list, parent, false);
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
        }
        return new UnitItemHolder(viewGroup);
    }

    private void setTextColors(TextView name, TextView value, boolean isSelected) {
        if (isSelected) {
            name.setTextColor(colorAccent);
            value.setTextColor(colorAccent);
        } else {
            name.setTextColor(colorDefault);
            value.setTextColor(colorDefault);
        }
    }

    void requestFocusForCurrent() {
        if (!powerfulConverter) {
            return;
        }

        RecyclerView.ViewHolder previousHolder = recycler.findViewHolderForLayoutPosition(curDim);
        if (previousHolder != null) {
            ViewGroup previousView = (ViewGroup) previousHolder.itemView;
            EditText previousValue = (EditText) previousView.getChildAt(0);
            previousValue.requestFocus();
            Log.d(createTag("curReq"), String.valueOf(curDim));
        }
    }

    private void switchFocus(int fromPosition, int toPosition) {

        if (fromPosition == toPosition)
            return;

        Log.d("FOCSWitch", "from " + fromPosition + " to " + toPosition);

        RecyclerView.ViewHolder previousHolder = recycler.findViewHolderForLayoutPosition(fromPosition);
        RecyclerView.ViewHolder newHolder = recycler.findViewHolderForLayoutPosition(toPosition);

        if (previousHolder != null) {

            ViewGroup previousView = (ViewGroup)
                    previousHolder.itemView;
            TextView previousName = previousView.findViewById(R.id.type);
            TextView previousValue = (TextView) previousView.getChildAt(0);
            setTextColors(previousName, previousValue, false);

        }
        if (newHolder != null) {
            ViewGroup newView = (ViewGroup)
                    newHolder.itemView;
            TextView newName = newView.findViewById(R.id.type);
            TextView newValue = (TextView) newView.getChildAt(0);
            setTextColors(newName, newValue, true);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final ViewGroup item = (ViewGroup) holder.itemView;
        TextView dimensionNameView = item.findViewById(R.id.type);
        TextView dimensionValueView = (TextView) item.getChildAt(0);

        dimensionNameView.setText(dimensions[position]);

        if (powerfulConverter) {
            EditText editValueView = (EditText) dimensionValueView;

            editValueView.setOnFocusChangeListener((view, isFocused) -> {
                int previousDimension = curDim;
                Log.d(createTag("editFocus"), "Changed to " + isFocused + " for " + position);
                if (isFocused) {

                    curDim = holder.getAdapterPosition();
                    Log.d(createTag("ChngDim"), "4||was " + previousDimension + ", now " + curDim);
                    String value;
                    if (editValueView.getText().length() > 0)
                        value = editValueView.getText().toString();
                    else
                        value = "1";

                    setValueAndDimension(
                            Double.valueOf(value.replace(',', '.')),
                            position,
                            false);

                    editValueView.setSelection(editValueView.getText().length());
                }

                if (fragment != null && fragment.isCurrentPage) {
                    switchFocus(previousDimension, curDim);
                }
            });

            if (holder.getAdapterPosition() == curDim) {
                Log.d(createTag("firstFoc"), String.valueOf(curDim));
                //editValueView.requestFocus();
            }

            editValueView.setText(GeneralHelper.resultNumberFormat.format(
                    formulas.getResult(pagePosition, position)));
            editValueView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (editValueView.hasFocus()) {
                        int previousDimension = curDim;
                        curDim = holder.getAdapterPosition();
                        Log.d(createTag("ChngDim"), "1||was " + previousDimension + ", now " + curDim);
                        if (s.toString().length() > 0)
                            setInputValue(s.toString());
                        else
                            setInputValue("1");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } else {
            dimensionValueView.setText(GeneralHelper.resultNumberFormat.format(
                    formulas.getResult(pagePosition, position)));
        }
        setTextColors(dimensionNameView, dimensionValueView, position == curDim);
    }

    @Override
    public int getItemCount() {
        return dimensions.length;
    }

    public class UnitItemHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        UnitItemHolder(final View itemView) {
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(mContext.getString(R.string.you_can));
            menu.add(Menu.NONE, 0, Menu.NONE, mContext.getString(R.string.context_menu_copy));
        }
    }
}
