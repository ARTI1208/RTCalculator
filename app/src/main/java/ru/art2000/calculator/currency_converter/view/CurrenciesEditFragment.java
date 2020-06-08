package ru.art2000.calculator.currency_converter.view;

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
import android.util.Pair;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.art2000.calculator.R;
import ru.art2000.calculator.currency_converter.model.CurrencyItem;
import ru.art2000.calculator.currency_converter.view_model.CurrenciesEditModel;
import ru.art2000.calculator.currency_converter.view_model.CurrencyDependencies;
import ru.art2000.helpers.AndroidHelper;

public class CurrenciesEditFragment extends Fragment {

    EditCurrenciesAdapter adapter;
    private View v = null;
    private RecyclerView recycler;
    private TextView emptyView;
    private Context mContext;
    private CurrenciesSettingsActivity parent;
    private ItemTouchHelper itemTouchHelper;

    private CurrenciesEditModel model;

    void scrollToTop() {
        recycler.smoothScrollToPosition(0);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (v == null) {
            mContext = getActivity();
            parent = (CurrenciesSettingsActivity) requireActivity();

            model = new CurrenciesEditModel(parent.getApplication());

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
                    parent.changeDone = true;

                    EditCurrenciesAdapter.Holder editViewHolder = (EditCurrenciesAdapter.Holder) viewHolder;
                    EditCurrenciesAdapter.Holder editTarget = (EditCurrenciesAdapter.Holder) target;

                    String firstCode = editViewHolder.code.getText().toString();
                    String secondCode = editTarget.code.getText().toString();

//                    CurrencyValuesHelper.swap(viewHolder.getBindingAdapterPosition(), target.getBindingAdapterPosition());
//                    adapter.notifyItemMoved(viewHolder.getBindingAdapterPosition(), target.getBindingAdapterPosition());

                    adapter.swap(viewHolder.getBindingAdapterPosition(), target.getBindingAdapterPosition());
//                    CurrencyValuesHelper.writeValuesToDB(mContext);

                    new Thread(() -> {
                        CurrencyDependencies
                                .getCurrencyDatabase(mContext)
                                .currencyDao()
                                .swapPositions(firstCode, secondCode);
                    }).start();

                    return true;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    parent.changeDone = true;
                    int position = viewHolder.getBindingAdapterPosition();
                    CurrencyItem removedItem = adapter.data.get(position);
                    String code = removedItem.code;

//                    parent.add.adapter.reFilterData();
                    adapter.data.remove(position);
                    adapter.notifyItemRemoved(position);
//                    adapter.size = CurrencyValuesHelper.visibleList.size();
                    toggleEmptyView();

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
            toggleEmptyView();
        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LiveData<List<CurrencyItem>> items = model.getVisibleItems();
        items.observe(getViewLifecycleOwner(), currencyItems -> {
            if (adapter != null) {
                adapter.setNewData(currencyItems);
            }
        });
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

    private void updateCheckState(List<CurrencyItem> list) {
        for (int i = 0; i < list.size(); ++i) {
            RecyclerView.ViewHolder holder = recycler.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                CheckBox checkBox = ((EditCurrenciesAdapter.Holder) holder).checkBox;
                if (checkBox != null) {
                    checkBox.setChecked(list.get(i).isSelected);
                }
            }
        }
    }

    class EditCurrenciesAdapter extends RecyclerView.Adapter<EditCurrenciesAdapter.Holder> {

        final int REORDER_MODE = 0;
        final int SELECTION_MODE = 1;

        int curMode = REORDER_MODE;

        private MutableLiveData<Integer> mCurrentEditMode = new MutableLiveData<>(REORDER_MODE);
        LiveData<Integer> currentEditMode = mCurrentEditMode;


        @LayoutRes
        int selectionItem = R.layout.item_add_currencies_list;
        @LayoutRes
        int reorderItem = R.layout.item_edit_currencies_list;

        List<CurrencyItem> data = new ArrayList<>();
        private MutableLiveData<Pair<Integer, Integer>> mSelectedItemsCount = new MutableLiveData<>(new Pair<>(0, 0));
        LiveData<Pair<Integer, Integer>> selectedItemsCount = mSelectedItemsCount;


        public void swap(int position, int anotherPosition) {
            if (position < 0
                    || anotherPosition < 0
                    || position >= getItemCount()
                    || anotherPosition >= getItemCount()) {

                return;
            }

            CurrencyItem prev = data.get(position);
            data.set(position, data.get(anotherPosition));
            data.set(anotherPosition, prev);
            data.get(position).position = position;
            data.get(anotherPosition).position = anotherPosition;


            notifyItemMoved(position, anotherPosition);
        }


        public void setNewData(@NonNull List<CurrencyItem> newData) {
            Log.d("NewData", "aaaaaa");
            if (data == null || data.isEmpty()) {
                data = newData;
                toggleEmptyView();
                notifyItemRangeInserted(0, newData.size());
            } else if (data.size() != newData.size() || !data.containsAll(newData)) {
                DiffUtil.DiffResult result =
                        DiffUtil.calculateDiff(CurrencyDependencies.getDiffCallback(data, newData));

                result.dispatchUpdatesTo(new ListUpdateCallback() {

                    List<CurrencyItem> oldData = data;

                    @Override
                    public void onInserted(int position, int count) {

                    }

                    @Override
                    public void onRemoved(int position, int count) {

                    }

                    @Override
                    public void onMoved(int fromPosition, int toPosition) {
                        if (mCurrentEditMode.getValue() == SELECTION_MODE)
                            newData.get(toPosition).isSelected = oldData.get(fromPosition).isSelected;
                    }

                    @Override
                    public void onChanged(int position, int count, @Nullable Object payload) {

                    }
                });

                data = newData;
                toggleEmptyView();
                result.dispatchUpdatesTo(this);
            }
        }

        EditCurrenciesAdapter() {
            int selectedCount = 0;
            for (CurrencyItem itemWrapper : data) {
                if (itemWrapper.isSelected) {
                    ++selectedCount;
                }
            }
            if (selectedCount > 0) {
                curMode = SELECTION_MODE;
                mCurrentEditMode.setValue(SELECTION_MODE);
            }
            mSelectedItemsCount.setValue(new Pair<>(selectedCount, getItemCount()));
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

        @Override
        public void onViewAttachedToWindow(@NonNull Holder holder) {
            int pos = holder.getBindingAdapterPosition();
            if (holder.checkBox != null) {
                holder.checkBox.setChecked(data.get(pos).isSelected);
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull EditCurrenciesAdapter.Holder holder, int position) {
            CurrencyItem currencyItem = data.get(position);
            holder.code.setText(currencyItem.code);

            holder.name.setText(CurrencyDependencies.getNameIdentifierForCode(mContext, currencyItem.code));

            if (holder.handle != null) {
                Log.d("EditSelCount", "nohand");
                holder.handle.setOnTouchListener((v, event) -> {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        itemTouchHelper.startDrag(holder);
                    }
                    return false;
                });
                holder.itemView.setOnLongClickListener(v -> {
                    notifyModeChanged(holder);
//                    parent.toggleElementsVisibility();
                    return false;
                });
            } else {
                Log.d("EditSelCount", "setting");
                holder.checkBox.setOnCheckedChangeListener(null);
                holder.checkBox.setChecked(currencyItem.isSelected);
                holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    CurrencyItem item = data.get(holder.getBindingAdapterPosition());

                    if (isChecked != item.isSelected) {
                        item.isSelected = !item.isSelected;
                        int selectedCount = mSelectedItemsCount.getValue().first + (item.isSelected ? 1 : -1);
                        mSelectedItemsCount.setValue(new Pair<>(selectedCount, getItemCount()));
                        if (selectedCount == 0) {
                            notifyModeChanged(null);
                        }
                    }
                });
                holder.itemView.setOnClickListener(v -> {
                    holder.checkBox.performClick();

                });
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        ArrayList<CurrencyItem> getSelectedItems() {
            ArrayList<CurrencyItem> selectedItems = new ArrayList<>();
            for (CurrencyItem itemWrapper : data) {
                Log.d("GetSel", itemWrapper.code + "||" + itemWrapper.isSelected);
                if (itemWrapper.isSelected) {
                    selectedItems.add(itemWrapper);
                }
            }
            return selectedItems;
        }

        void notifyModeChanged(RecyclerView.ViewHolder holder) {
//            size = CurrencyValuesHelper.visibleList.size();
            if (curMode == SELECTION_MODE) {
                curMode = REORDER_MODE;
                itemTouchHelper.attachToRecyclerView(recycler);
//                selectedCount = 0;
                mCurrentEditMode.setValue(REORDER_MODE);
                mSelectedItemsCount.setValue(new Pair<>(0, getItemCount()));
                for (CurrencyItem itemWrapper : data) {
                    itemWrapper.isSelected = false;
                }
            } else {
                curMode = SELECTION_MODE;
                mCurrentEditMode.setValue(SELECTION_MODE);
                itemTouchHelper.attachToRecyclerView(null);
                if (parent.deleteTooltip != null) {
                    parent.isFirstTimeTooltipShown = false;
                    parent.deleteTooltip.dismiss();
                }
//                ++selectedCount;
                mSelectedItemsCount.setValue(new Pair<>(1, getItemCount()));
                data.get(holder.getBindingAdapterPosition()).isSelected = true;
            }
//            parent.toggleElementsVisibility();
            toggleEmptyView();
            notifyDataSetChanged();
        }

        void deselectAll() {
            for (CurrencyItem CurrencyItem : data) {
                CurrencyItem.isSelected = false;
            }
            mSelectedItemsCount.setValue(new Pair<>(0, getItemCount()));
            notifyModeChanged(null);
        }

        void selectAll() {
            for (CurrencyItem CurrencyItem : data) {
                CurrencyItem.isSelected = true;
            }
            updateCheckState(data);
            mSelectedItemsCount.setValue(new Pair<>(getItemCount(), getItemCount()));
        }

        boolean isSomethingSelected() {
            return mSelectedItemsCount.getValue().first != 0;
        }

        boolean isAllSelected() {
            return mSelectedItemsCount.getValue().first == getItemCount();
        }

        boolean isSelectionMode() {
            return curMode == SELECTION_MODE;
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
