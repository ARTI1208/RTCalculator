package ru.art2000.calculator.currency_converter;

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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.art2000.calculator.R;
import ru.art2000.extensions.CurrencyItemWrapper;
import ru.art2000.helpers.AndroidHelper;
import ru.art2000.helpers.CurrencyValuesHelper;

public class CurrenciesEditFragment extends Fragment {

    EditCurrenciesAdapter adapter;
    private View v = null;
    private RecyclerView recycler;
    private TextView emptyView;
    private Context mContext;
    private EditCurrenciesActivity parent;
    private ItemTouchHelper itemTouchHelper;

    void scrollToTop() {
        recycler.smoothScrollToPosition(0);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (v == null) {
            mContext = getActivity();
            parent = (EditCurrenciesActivity) getActivity();
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

                        if (viewHolder.getAdapterPosition() == -1) {
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
                            xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                            xMarkBottom = xMarkTop + intrinsicHeight;
                        } else {
                            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(),
                                    itemView.getRight(), itemView.getBottom());
                            xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                            xMarkRight = itemView.getRight() - xMarkMargin;
                            xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                            xMarkBottom = xMarkTop + intrinsicHeight;
                        }
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
                    CurrencyValuesHelper.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    CurrencyValuesHelper.writeValuesToDB(mContext);
                    return true;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    parent.changeDone = true;
                    int position = viewHolder.getAdapterPosition();
                    parent.lastModifiedItemCode = CurrencyValuesHelper.visibleList.get(position).code;
                    CurrencyValuesHelper.hideItems(position);
                    parent.add.adapter.reFilterData();
                    adapter.notifyItemRemoved(position);
                    adapter.size = CurrencyValuesHelper.visibleList.size();
                    toggleEmptyView();
                    CurrencyValuesHelper.writeValuesToDB(mContext);
                    parent.generateUndoSnackbar();
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

    private void toggleEmptyView() {
        if (adapter == null)
            return;
        if (adapter.getItemCount() == 0) {
            if (emptyView.getVisibility() == View.GONE) {
                emptyView.setText(getEmptyText());
                emptyView.setVisibility(View.VISIBLE);
            }
        } else {
            if (emptyView.getVisibility() == View.VISIBLE) {
                emptyView.setVisibility(View.GONE);
            }
        }
    }

    private String getEmptyText() {
        return getString(R.string.empty_text_no_currencies_added);
    }

    private void updateCheckState(List<CurrencyItemWrapper> list) {
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
        int size = CurrencyValuesHelper.visibleList.size();
        int selectedCount = 0;
        int curMode = REORDER_MODE;
        @LayoutRes
        int selectionItem = R.layout.item_add_currencies_list;
        @LayoutRes
        int reorderItem = R.layout.item_edit_currencies_list;

        boolean shouldUpdateVisibility = true;

        EditCurrenciesAdapter() {
            for (CurrencyItemWrapper itemWrapper : CurrencyValuesHelper.visibleList) {
                if (itemWrapper.isSelected) {
                    ++selectedCount;
                }
            }
            if (selectedCount > 0) {
                curMode = SELECTION_MODE;
            }
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
            int pos = holder.getAdapterPosition();
            if (holder.checkBox != null) {
                holder.checkBox.setChecked(CurrencyValuesHelper.visibleList.get(pos).isSelected);
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull EditCurrenciesAdapter.Holder holder, int position) {
            CurrencyItemWrapper currencyItem = CurrencyValuesHelper.visibleList.get(position);
            holder.code.setText(currencyItem.code);
            holder.name.setText(currencyItem.nameResourceId);
            if (holder.handle != null) {
                holder.handle.setOnTouchListener((v, event) -> {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        itemTouchHelper.startDrag(holder);
                    }
                    return false;
                });
                holder.itemView.setOnLongClickListener(v -> {
                    notifyModeChanged(holder);
                    parent.toggleElementsVisibility();
                    return false;
                });
            } else {
                holder.checkBox.setOnCheckedChangeListener(null);
                holder.checkBox.setChecked(currencyItem.isSelected);
                holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        if (!currencyItem.isSelected) {
                            currencyItem.isSelected = true;
                            ++selectedCount;
                        }
                    } else {
                        if (currencyItem.isSelected) {
                            currencyItem.isSelected = false;
                            --selectedCount;
                        }
                    }
                    if (shouldUpdateVisibility) {
                        parent.toggleElementsVisibility();
                    }
                    if (selectedCount == 0) {
                        notifyModeChanged(null);
                    }
                });
                holder.itemView.setOnClickListener(v ->
                        holder.checkBox.performClick());
            }
        }

        @Override
        public int getItemCount() {
            return size;
        }

        ArrayList<CurrencyItemWrapper> getSelectedItems() {
            ArrayList<CurrencyItemWrapper> selectedItems = new ArrayList<>();
            for (CurrencyItemWrapper itemWrapper : CurrencyValuesHelper.visibleList) {
                if (itemWrapper.isSelected) {
                    selectedItems.add(itemWrapper);
                }
            }
            return selectedItems;
        }

        void notifyModeChanged(RecyclerView.ViewHolder holder) {
            size = CurrencyValuesHelper.visibleList.size();
            if (curMode == SELECTION_MODE) {
                curMode = REORDER_MODE;
                itemTouchHelper.attachToRecyclerView(recycler);
                selectedCount = 0;
                for (CurrencyItemWrapper itemWrapper : CurrencyValuesHelper.visibleList) {
                    itemWrapper.isSelected = false;
                }
            } else {
                curMode = SELECTION_MODE;
                itemTouchHelper.attachToRecyclerView(null);
                if (parent.deleteTooltip != null) {
                    parent.isFirstTimeTooltipShown = false;
                    parent.deleteTooltip.dismiss();
                }
                ++selectedCount;
                CurrencyValuesHelper.visibleList.get(holder.getAdapterPosition()).isSelected = true;
            }
            parent.toggleElementsVisibility();
            toggleEmptyView();
            notifyDataSetChanged();
        }

        void deselectAll() {
            for (CurrencyItemWrapper currencyItemWrapper : CurrencyValuesHelper.visibleList) {
                currencyItemWrapper.isSelected = false;
            }
            selectedCount = 0;
            notifyModeChanged(null);
        }

        void selectAll() {
            for (CurrencyItemWrapper currencyItemWrapper : CurrencyValuesHelper.visibleList) {
                currencyItemWrapper.isSelected = true;
            }
            shouldUpdateVisibility = false;
            updateCheckState(CurrencyValuesHelper.visibleList);
            shouldUpdateVisibility = true;
            selectedCount = size;
        }

        boolean isSomethingSelected() {
            return selectedCount != 0;
        }

        boolean isAllSelected() {
            return selectedCount == CurrencyValuesHelper.visibleList.size();
        }

        boolean isSelectionMode() {
            return curMode == SELECTION_MODE;
        }

        void setNewData() {
            size = CurrencyValuesHelper.visibleList.size();
            notifyDataSetChanged();
            toggleEmptyView();
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
