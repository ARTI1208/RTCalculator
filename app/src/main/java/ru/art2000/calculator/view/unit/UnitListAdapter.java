package ru.art2000.calculator.view.unit;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import ru.art2000.calculator.R;
import ru.art2000.calculator.databinding.ItemUnitConverterListBinding;
import ru.art2000.calculator.databinding.ItemUnitConverterListPowerfulBinding;
import ru.art2000.calculator.model.unit.UnitConverterItem;
import ru.art2000.calculator.view_model.calculator.CalculationClass;
import ru.art2000.extensions.SimpleTextWatcher;
import ru.art2000.helpers.AndroidHelper;
import ru.art2000.helpers.GeneralHelper;

public class UnitListAdapter extends RecyclerView.Adapter<UnitListAdapter.UnitItemHolder> {

    private final MutableLiveData<Pair<Integer, Integer>> selectedPosition =
            new MutableLiveData<>(new Pair<>(0, 0));
    private final Context mContext;
    private final UnitConverterItem[] data;
    private final LifecycleOwner lifecycleOwner;
    private int colorAccent;
    private int colorDefault;
    private RecyclerView recycler;
    private boolean powerfulConverter = false;

    UnitListAdapter(Context ctx, LifecycleOwner lifecycleOwner, UnitConverterItem[] items, boolean isPowerfulConverter) {
        data = items;
        mContext = ctx;
        powerfulConverter = isPowerfulConverter;
        this.lifecycleOwner = lifecycleOwner;

        if (data != null && data.length > 0 && data[0].getCurrentValue() == 0.0)
            setValue(0, 1);

        init();
    }

    UnitListAdapter(Context ctx, LifecycleOwner lifecycleOwner, UnitConverterItem[] items, int pos) {
        data = items;
        mContext = ctx;
        this.lifecycleOwner = lifecycleOwner;

        setCurrentDimension(pos);

        init();
    }

    private void init() {
        colorAccent = AndroidHelper.getColorAttribute(mContext, R.attr.colorAccent);
        colorDefault = AndroidHelper.getColorAttribute(mContext, android.R.attr.textColorSecondary);
    }

    void setValue(int position, double value) {

        setCurrentDimension(position);

        UnitConverterItem from = data[position];
        from.setValue(value);

        for (int i = 0; i < getItemCount(); i++) {

            if (powerfulConverter && i == position)
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
        Double result = CalculationClass.calculate(value);
        double doubleValue = result == null ? 1 : result;
        setValue(position, doubleValue);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recycler = recyclerView;
        selectedPosition.observe(lifecycleOwner, pair -> {
            if (pair.first.equals(pair.second)) return;

            setTextColors((UnitItemHolder) recycler.findViewHolderForAdapterPosition(pair.first), false);
            setTextColors((UnitItemHolder) recycler.findViewHolderForAdapterPosition(pair.second), true);
        });
    }

    @NonNull
    @Override
    public UnitItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (powerfulConverter) {
            return new UnitItemHolder(
                    ItemUnitConverterListPowerfulBinding.inflate(inflater, parent, false));
        }

        return new UnitItemHolder(ItemUnitConverterListBinding.inflate(inflater, parent, false));
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
        if (!powerfulConverter) return;

        UnitItemHolder previousHolder = (UnitItemHolder) recycler.findViewHolderForLayoutPosition(getCurrentDimension());
        if (previousHolder != null) {
            previousHolder.dimensionValueView.requestFocus();
            previousHolder.dimensionValueView.postDelayed(previousHolder.dimensionValueView::requestFocus, 150L);
        }
    }

    private int getCurrentDimension() {
        return selectedPosition.getValue().second;
    }

    private void setCurrentDimension(int dimension) {
        Pair<Integer, Integer> newPair = new Pair<>(selectedPosition.getValue().second, dimension);
        selectedPosition.setValue(newPair);
    }

    private String doubleToString(double d) {
        return GeneralHelper.resultNumberFormat.format(d);
    }

    @Override
    public void onBindViewHolder(@NonNull final UnitItemHolder holder, final int position) {
        holder.bind(data[position], position == getCurrentDimension());
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class UnitItemHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        final TextView dimensionValueView;
        final TextView dimensionNameView;

        UnitItemHolder(final ItemUnitConverterListBinding binding) {
            super(binding.getRoot());

            dimensionValueView = binding.value;
            dimensionNameView = binding.type;

            init();
        }

        UnitItemHolder(final ItemUnitConverterListPowerfulBinding binding) {
            super(binding.getRoot());

            dimensionValueView = binding.value;
            dimensionNameView = binding.type;

            TextWatcher textWatcher = new SimpleTextWatcher() {
                @Override
                public void onTextChanged(@NotNull CharSequence s, int start, int before, int count) {
                    if (!binding.value.hasFocus()) return;

                    if (s.length() == 0) {
                        setValue(getBindingAdapterPosition(), 1);
                    } else {
                        setValue(getBindingAdapterPosition(), s.toString());
                    }
                }
            };
            binding.value.addTextChangedListener(textWatcher);

            init();
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(mContext.getString(R.string.you_can));
            menu.add(Menu.NONE, 0, Menu.NONE, mContext.getString(R.string.context_menu_copy)).setOnMenuItemClickListener(item -> {
                ClipboardManager cmg =
                        (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);

                if (cmg == null) return true;

                CharSequence copiedText = dimensionValueView.getText() + " " + dimensionNameView.getText();
                ClipData clipData = ClipData.newPlainText("unitConvertResult", copiedText);
                cmg.setPrimaryClip(clipData);

                CharSequence toastText = mContext.getString(R.string.copied) + " " + copiedText;
                Toast.makeText(mContext, toastText, Toast.LENGTH_SHORT).show();

                return true;
            });
        }

        void bind(UnitConverterItem item, boolean isSelected) {

            dimensionNameView.setText(item.getNameResourceId());
            dimensionValueView.setText(doubleToString(item.getCurrentValue()));

            setTextColors(this, isSelected);
        }

        private void init() {
            itemView.setOnCreateContextMenuListener(this);
        }
    }
}
