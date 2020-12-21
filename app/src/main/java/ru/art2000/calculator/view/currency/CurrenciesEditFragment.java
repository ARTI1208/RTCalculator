package ru.art2000.calculator.view.currency;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import ru.art2000.calculator.R;
import ru.art2000.calculator.databinding.ModifyCurrenciesLayoutBinding;
import ru.art2000.calculator.model.currency.CurrencyItem;
import ru.art2000.calculator.view_model.currency.CurrenciesEditModel;
import ru.art2000.calculator.view_model.currency.CurrenciesSettingsModel;
import ru.art2000.calculator.view_model.currency.CurrencyDependencies;
import ru.art2000.extensions.CollectionsKt;
import ru.art2000.extensions.LiveList;
import ru.art2000.extensions.ReplaceableFragment;
import ru.art2000.helpers.AndroidHelper;

public class CurrenciesEditFragment extends ReplaceableFragment {

    private EditCurrenciesAdapter adapter;

    private ItemTouchHelper itemTouchHelper;

    private ModifyCurrenciesLayoutBinding viewBinding;
    private CurrenciesEditModel model;


    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (viewBinding == null) {

            model = new ViewModelProvider(requireActivity()).get(CurrenciesSettingsModel.class);

            viewBinding = ModifyCurrenciesLayoutBinding.inflate(inflater);

            viewBinding.modifyCurrenciesList.setPadding(0, 0, 0, AndroidHelper.dip2px(requireContext(), 20));

            LinearLayoutManager llm = new LinearLayoutManager(requireContext());
            llm.setOrientation(RecyclerView.VERTICAL);

            itemTouchHelper = new ItemTouchHelper(new CurrenciesEditRecyclerTouchCallback(
                    requireContext(),
                    position -> {
                        CurrencyItem removedItem = model.getDisplayedVisibleItems().get(position);

//                        model.getDisplayedVisibleItems().remove(position.intValue());
                        model.databaseMarkHidden(removedItem);

                        return Unit.INSTANCE;
                    },
                    (firstPosition, secondPosition) -> {
                        adapter.swap(firstPosition, secondPosition);
                        return Unit.INSTANCE;
                    }
            ));

            adapter = new EditCurrenciesAdapter();
            itemTouchHelper.attachToRecyclerView(viewBinding.modifyCurrenciesList);
            viewBinding.modifyCurrenciesList.setLayoutManager(llm);
            viewBinding.modifyCurrenciesList.setAdapter(adapter);
            toggleEmptyView();
        }
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        model.getDisplayedVisibleItems().observe(getViewLifecycleOwner(),
                new LiveList.LiveListObserver<CurrencyItem>() {

                    @Override
                    public void onAnyChanged(@NotNull List<? extends CurrencyItem> previousList) {
                        toggleEmptyView();
                    }
                });

        model.getSelectedVisibleItems().observe(getViewLifecycleOwner(), new LiveList.LiveListObserver<CurrencyItem>() {

            @Override
            public void onItemsInserted(@NotNull List<? extends CurrencyItem> previousList,
                                        @NotNull List<? extends CurrencyItem> insertedItems,
                                        int position) {
                super.onItemsInserted(previousList, insertedItems, position);

                for (CurrencyItem item : insertedItems) {
                    EditCurrenciesAdapter.Holder holder = (EditCurrenciesAdapter.Holder)
                            viewBinding.modifyCurrenciesList.findViewHolderForAdapterPosition(model.getDisplayedVisibleItems().indexOf(item));

                    if (holder == null || holder.checkBox == null)
                        continue;

                    holder.checkBox.setChecked(true);
                }
            }

            @Override
            public void onItemsRemoved(@NotNull List<? extends CurrencyItem> previousList,
                                       @NotNull List<Integer> removedItems) {
                super.onItemsRemoved(previousList, removedItems);

                for (int i : removedItems) {
                    EditCurrenciesAdapter.Holder holder = (EditCurrenciesAdapter.Holder)
                            viewBinding.modifyCurrenciesList.findViewHolderForAdapterPosition(
                                    model.getDisplayedVisibleItems().indexOf(previousList.get(i)));

                    if (holder == null || holder.checkBox == null)
                        continue;

                    holder.checkBox.setChecked(false);
                }
            }
        });
    }

    @Override
    public void onReselected() {
        viewBinding.modifyCurrenciesList.smoothScrollToPosition(0);
    }

    private void toggleEmptyView() {
        if (adapter == null)
            return;

        if (adapter.getItemCount() == 0) {
            viewBinding.emptyTv.setText(getEmptyText());
            viewBinding.emptyTv.setVisibility(View.VISIBLE);
        } else {
            viewBinding.emptyTv.setVisibility(View.GONE);
        }
    }

    private String getEmptyText() {
        return getString(R.string.empty_text_no_currencies_added);
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public int getTitle() {
        return R.string.currencies_edit;
    }

    class EditCurrenciesAdapter extends RecyclerView.Adapter<EditCurrenciesAdapter.Holder> {

        final int REORDER_MODE = 0;
        final int SELECTION_MODE = 1;

        int curMode = REORDER_MODE;

        @LayoutRes
        int selectionItem = R.layout.item_add_currencies_list;
        @LayoutRes
        int reorderItem = R.layout.item_edit_currencies_list;

        EditCurrenciesAdapter() {
            model.getSelectedVisibleItems().observe(getViewLifecycleOwner(), new LiveList.LiveListObserver<CurrencyItem>() {

                @Override
                public void onItemsRemoved(@NotNull List<? extends CurrencyItem> previousList,
                                           @NotNull List<Integer> removedItems) {
                    super.onItemsRemoved(previousList, removedItems);
                    if (model.getSelectedVisibleItems().isEmpty()) {
                        setReorderMode();
                    }
                }
            });

            model.getDisplayedVisibleItems().observe(getViewLifecycleOwner(), new LiveList.LiveListObserver<CurrencyItem>() {
                @Override
                public void onAnyChanged(@NotNull List<? extends CurrencyItem> previousList) {
                    dispatchListUpdate(previousList, model.getDisplayedVisibleItems());
                }
            });
        }

        public void swap(int position, int anotherPosition) {
            if (position < 0
                    || anotherPosition < 0
                    || position >= getItemCount()
                    || anotherPosition >= getItemCount()) {

                return;
            }

            CurrencyItem item = model.getDisplayedVisibleItems().get(position);
            CurrencyItem anotherItem = model.getDisplayedVisibleItems().get(anotherPosition);

            item.position = anotherPosition;
            anotherItem.position = position;

            Map<CurrencyItem, CurrencyItem> map = new HashMap<>();
            map.put(item, anotherItem);
            map.put(anotherItem, item);

            model.getDisplayedVisibleItems().replaceAll(map);
        }

        void dispatchListUpdate(List<? extends CurrencyItem> oldData, List<? extends CurrencyItem> newData) {
            DiffUtil.DiffResult result = CollectionsKt.calculateDiff(oldData, newData);
            result.dispatchUpdatesTo(this);
        }

        @Override
        public int getItemViewType(int position) {
            return curMode;
        }

        @NonNull
        @Override
        public EditCurrenciesAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View item;
            if (viewType == SELECTION_MODE)
                item = LayoutInflater.from(requireContext()).inflate(selectionItem, null);
            else
                item = LayoutInflater.from(requireContext()).inflate(reorderItem, null);
            return new Holder(item);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull EditCurrenciesAdapter.Holder holder, int position) {
            CurrencyItem currencyItem = model.getDisplayedVisibleItems().get(position);
            holder.code.setText(currencyItem.code);

            holder.name.setText(CurrencyDependencies.getNameIdentifierForCode(requireContext(), currencyItem.code));

            if (holder.handle != null) {
                holder.handle.setOnTouchListener((v, event) -> {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        itemTouchHelper.startDrag(holder);
                    }
                    return false;
                });
                holder.itemView.setOnLongClickListener(v -> {
                    setSelectionMode(holder);
                    return false;
                });
            } else {
                holder.checkBox.setOnCheckedChangeListener(null);
                holder.checkBox.setChecked(model.isVisibleItemSelected(currencyItem));
                holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    CurrencyItem item = model.getDisplayedVisibleItems().get(holder.getBindingAdapterPosition());
                    if (isChecked != model.isVisibleItemSelected(item)) {
                        model.setVisibleItemSelected(item, isChecked);
                    }
                });
                holder.itemView.setOnClickListener(v -> holder.checkBox.performClick());
            }
        }

        @Override
        public int getItemCount() {
            return model.getDisplayedVisibleItems().size();
        }

        void setReorderMode() {
            if (curMode == REORDER_MODE)
                return;

            curMode = REORDER_MODE;
            model.setEditSelectionMode(false);

            itemTouchHelper.attachToRecyclerView(viewBinding.modifyCurrenciesList);
            model.getSelectedVisibleItems().clear();
        }

        void setSelectionMode(RecyclerView.ViewHolder holder) {
            curMode = SELECTION_MODE;
            model.setEditSelectionMode(true);

            itemTouchHelper.attachToRecyclerView(null);
            model.dismissFirstTimeTooltip();

            int p = holder.getBindingAdapterPosition();
            CurrencyItem item = model.getDisplayedVisibleItems().get(p);
            model.setVisibleItemSelected(item, true);
            notifyDataSetChanged();
        }

        class Holder extends RecyclerView.ViewHolder {

            TextView code;
            ImageView handle;
            CheckBox checkBox;
            TextView name;

            Holder(final View itemView) {
                super(itemView);
                code = itemView.findViewById(R.id.currency_code);
                name = itemView.findViewById(R.id.currency_name);
                handle = itemView.findViewById(R.id.handle);
                checkBox = itemView.findViewById(R.id.checkbox_add);
            }
        }
    }
}
