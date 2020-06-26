package ru.art2000.calculator.view.currency;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.art2000.calculator.R;
import ru.art2000.calculator.model.currency.CurrencyItem;
import ru.art2000.calculator.view_model.currency.CurrenciesEditModel;
import ru.art2000.calculator.view_model.currency.CurrenciesSettingsModel;
import ru.art2000.calculator.view_model.currency.CurrencyDependencies;
import ru.art2000.extensions.CollectionsKt;
import ru.art2000.extensions.LiveList;
import ru.art2000.extensions.ReplaceableFragment;
import ru.art2000.helpers.AndroidHelper;

public class CurrenciesEditFragment extends ReplaceableFragment {

    EditCurrenciesAdapter adapter;
    private View v = null;
    private RecyclerView recycler;
    private TextView emptyView;
    private Context mContext;
    private CurrenciesSettingsActivity parent;
    private ItemTouchHelper itemTouchHelper;

    private CurrenciesEditModel model;


    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (v == null) {
            mContext = getActivity();
            parent = (CurrenciesSettingsActivity) requireActivity();

            model = new ViewModelProvider(parent).get(CurrenciesSettingsModel.class);

            v = inflater.inflate(R.layout.modify_currencies_layout, null);
            recycler = v.findViewById(R.id.modify_currencies_list);
            recycler.setPadding(0, 0, 0, AndroidHelper.dip2px(mContext, 20));
            emptyView = v.findViewById(R.id.empty_tv);
            LinearLayoutManager llm = new LinearLayoutManager(mContext);
            llm.setOrientation(RecyclerView.VERTICAL);

            int startOrLeft = parent.useViewPager2 ? ItemTouchHelper.START : ItemTouchHelper.LEFT;

            itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.DOWN | ItemTouchHelper.UP,
                    startOrLeft) {

                Drawable background;
                Drawable xMark;
                int xMarkMargin;
                boolean initiated;

                @Override
                public boolean isLongPressDragEnabled() {
                    return false;
                }

                private void init() {
                    background = new ColorDrawable(Color.RED);
                    xMark = ContextCompat.getDrawable(mContext, R.drawable.ic_clear_history);
                    ColorFilter filter = new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                    xMark.setColorFilter(filter);
                    xMarkMargin = (int) mContext.getResources().getDimension(R.dimen.activity_horizontal_margin);
                    initiated = true;
                }

                @Override
                public void onChildDraw(@NonNull Canvas c,
                                        @NonNull RecyclerView recyclerView,
                                        @NonNull RecyclerView.ViewHolder viewHolder,
                                        float dX, float dY,
                                        int actionState, boolean isCurrentlyActive) {

                    if (dY == 0) {
                        View itemView = viewHolder.itemView;

                        if (viewHolder.getBindingAdapterPosition() == -1) {
                            return;
                        }

                        if (!initiated) {
                            init();
                        }

                        int windowBackgroundColor = AndroidHelper.getColorAttribute(mContext, android.R.attr.windowBackground);
                        itemView.setBackgroundColor(windowBackgroundColor);

                        int itemHeight = itemView.getBottom() - itemView.getTop();
                        int intrinsicWidth = xMark.getIntrinsicWidth();
                        int intrinsicHeight = xMark.getIntrinsicWidth();
                        int xMarkLeft;
                        int xMarkRight;
                        int xMarkTop;
                        int xMarkBottom;
                        if (dX > 0) {
                            background.setBounds(itemView.getLeft(), itemView.getTop(),
                                    itemView.getLeft() + (int) dX, itemView.getBottom());
                            xMarkLeft = itemView.getLeft() + xMarkMargin;
                            xMarkRight = itemView.getLeft() + xMarkMargin + intrinsicWidth;
                        } else {
                            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(),
                                    itemView.getRight(), itemView.getBottom());
                            xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                            xMarkRight = itemView.getRight() - xMarkMargin;
                        }
                        xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                        xMarkBottom = xMarkTop + intrinsicHeight;
                        background.draw(c);

                        xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                        xMark.draw(c);
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView,
                                      @NonNull RecyclerView.ViewHolder viewHolder,
                                      @NonNull RecyclerView.ViewHolder target) {

                    EditCurrenciesAdapter.Holder editViewHolder = (EditCurrenciesAdapter.Holder) viewHolder;
                    EditCurrenciesAdapter.Holder editTarget = (EditCurrenciesAdapter.Holder) target;

//                    String firstCode = editViewHolder.code.getText().toString();
//                    String secondCode = editTarget.code.getText().toString();

                    adapter.swap(viewHolder.getBindingAdapterPosition(), target.getBindingAdapterPosition());

//                    new Thread(() -> {
//                        CurrencyDependencies
//                                .getCurrencyDatabase(mContext)
//                                .currencyDao()
//                                .swapPositions(firstCode, secondCode);
//                    }).start();

                    return true;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    int position = viewHolder.getBindingAdapterPosition();
                    CurrencyItem removedItem = model.getDisplayedVisibleItems().get(position);
                    String code = removedItem.code;

                    model.getDisplayedVisibleItems().remove(position);

                    Maybe
                            .fromRunnable(() ->
                                    CurrencyDependencies
                                            .getCurrencyDatabase(mContext)
                                            .currencyDao()
                                            .removeFromVisible(code)
                            ).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnComplete(() ->
                                    parent.generateUndoSnackbar(Collections.singletonList(removedItem), false))
                            .subscribe();
                }
            });
            adapter = new EditCurrenciesAdapter();
            itemTouchHelper.attachToRecyclerView(recycler);
            recycler.setLayoutManager(llm);
            recycler.setAdapter(adapter);
//            toggleEmptyView();
        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        model.getDisplayedVisibleItems().observe(getViewLifecycleOwner(),
                new LiveList.LiveListObserver<CurrencyItem>() {
                    @Override
                    public void onAnyChanged(@NotNull List<? extends CurrencyItem> previousList) {
                        if (model.getDisplayedVisibleItems().isEmpty()) {
                            toggleEmptyView();
                        }
                    }

//                    @Override
//                    public void onItemsReplaced(@NotNull List<? extends CurrencyItem> previousList, @NotNull List<? extends CurrencyItem> addedItems, int position) {
//                        super.onItemsReplaced(previousList, addedItems, position);
//                        DiffUtil.DiffResult diffResult = CollectionsKt.calculateDiff(previousList, model.getDisplayedVisibleItems());
//                        diffResult.dispatchUpdatesTo(adapter);
//                    }
//
//                    @Override
//                    public void onItemsInserted(@NotNull List<? extends CurrencyItem> previousList, @NotNull List<? extends CurrencyItem> insertedItems, int position) {
//                        super.onItemsInserted(previousList, insertedItems, position);
//                        adapter.notifyItemRangeInserted(position, insertedItems.size());
//                    }
//
//                    @Override
//                    public void onItemsRemoved(@NotNull List<? extends CurrencyItem> previousList, @NotNull List<Integer> removedItems) {
//                        super.onItemsRemoved(previousList, removedItems);
//                        for (int i : removedItems) {
//                            adapter.notifyItemRemoved(i);
//                        }
//                    }
                });

        model.getSelectedVisibleItems().observe(getViewLifecycleOwner(), new LiveList.LiveListObserver<CurrencyItem>() {

            @Override
            public void onItemsInserted(@NotNull List<? extends CurrencyItem> previousList,
                                        @NotNull List<? extends CurrencyItem> insertedItems,
                                        int position) {
                super.onItemsInserted(previousList, insertedItems, position);

                for (CurrencyItem item : insertedItems) {
                    EditCurrenciesAdapter.Holder holder = (EditCurrenciesAdapter.Holder)
                            recycler.findViewHolderForAdapterPosition(model.getDisplayedVisibleItems().indexOf(item));

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
                            recycler.findViewHolderForAdapterPosition(
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
        recycler.smoothScrollToPosition(0);
    }

    private void toggleEmptyView() {
        if (adapter == null)
            return;

        if (adapter.getItemCount() == 0) {
            emptyView.setText(getEmptyText());
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
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

        public void swap(int position, int anotherPosition) {
            if (position < 0
                    || anotherPosition < 0
                    || position >= getItemCount()
                    || anotherPosition >= getItemCount()) {

                return;
            }

            CurrencyItem item = model.getDisplayedVisibleItems().get(position);
            CurrencyItem anotherItem = model.getDisplayedVisibleItems().get(anotherPosition);

            Map<CurrencyItem, CurrencyItem> map = new HashMap<>();
            map.put(item, anotherItem);
            map.put(anotherItem, item);

            item.position = anotherPosition;
            anotherItem.position = position;

            model.getDisplayedVisibleItems().replaceAll(map);

//            CurrencyItem prev = model.getDisplayedVisibleItems().get(position);
//            model.getDisplayedVisibleItems().set(position, model.getDisplayedVisibleItems().get(anotherPosition));
//            model.getDisplayedVisibleItems().set(anotherPosition, prev);
//            model.getDisplayedVisibleItems().get(position).position = position;
//            model.getDisplayedVisibleItems().get(anotherPosition).position = anotherPosition;


//            notifyItemMoved(position, anotherPosition);
        }

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

        void dispatchListUpdate(List<? extends CurrencyItem> oldData, List<? extends CurrencyItem> newData) {
            Log.d("Edit", "newDaat");
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
                item = LayoutInflater.from(mContext).inflate(selectionItem, null);
            else
                item = LayoutInflater.from(mContext).inflate(reorderItem, null);
            return new Holder(item);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull EditCurrenciesAdapter.Holder holder, int position) {
            CurrencyItem currencyItem = model.getDisplayedVisibleItems().get(position);
            holder.code.setText(currencyItem.code);

            holder.name.setText(CurrencyDependencies.getNameIdentifierForCode(mContext, currencyItem.code));

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


            itemTouchHelper.attachToRecyclerView(recycler);
            model.getSelectedVisibleItems().clear();
        }

        void setSelectionMode(RecyclerView.ViewHolder holder) {
            curMode = SELECTION_MODE;
            model.setEditSelectionMode(true);

            itemTouchHelper.attachToRecyclerView(null);
            if (parent.deleteTooltip != null) {
                parent.isFirstTimeTooltipShown = false;
                parent.deleteTooltip.dismiss();
            }

            int p = holder.getBindingAdapterPosition();
            CurrencyItem item = model.getDisplayedVisibleItems().get(p);
            model.setVisibleItemSelected(item, true);
            ;
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
