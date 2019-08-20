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

import ru.art2000.calculator.R;
import ru.art2000.extensions.CurrencyItem;
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
                    CurrencyValuesHelper.hideItems(viewHolder.getAdapterPosition());
                    parent.add.filterList();
                    parent.add.adapter.setNewData();
                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    adapter.size = CurrencyValuesHelper.visibleList.size();
                    toggleEmptyView();
                    CurrencyValuesHelper.writeValuesToDB(mContext);
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

    class EditCurrenciesAdapter extends RecyclerView.Adapter {

        final int REORDER_MODE = 0;
        final int SELECTION_MODE = 1;
        int size = CurrencyValuesHelper.visibleList.size();
        ArrayList<CurrencyItem> itemsToRemove = new ArrayList<>();
        int curMode = REORDER_MODE;
        @LayoutRes
        int selectionItem = R.layout.item_add_currencies_list;
        @LayoutRes
        int reorderItem = R.layout.item_edit_currencies_list;

        void deselectAll() {
            itemsToRemove.clear();
            notifyModeChanged(null);
        }

        void selectAll() {
            itemsToRemove.clear();
            itemsToRemove.addAll(CurrencyValuesHelper.visibleList);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return curMode;
        }

        void setNewData() {
            size = CurrencyValuesHelper.visibleList.size();
            notifyDataSetChanged();
            toggleEmptyView();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View item;
            if (viewType == SELECTION_MODE)
                item = LayoutInflater.from(mContext).inflate(selectionItem, null);
            else
                item = LayoutInflater.from(mContext).inflate(reorderItem, null);
            return new Holder(item);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            TextView code = ((Holder) holder).code;
            TextView name = ((Holder) holder).name;
            ImageView handle = ((Holder) holder).handle;
            CheckBox check = ((Holder) holder).check;
            CurrencyItem item = CurrencyValuesHelper.visibleList.get(position);
            code.setText(item.code);
            name.setText(item.nameResourceId);
            if (handle != null) {
                handle.setOnTouchListener((v, event) -> {
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
                check.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked && !itemsToRemove.contains(item))
                        itemsToRemove.add(item);
                    else if (!isChecked)
                        itemsToRemove.remove(item);
                    if (!isSomethingSelected())
                        notifyModeChanged(holder);
                    parent.toggleElementsVisibility();
                });
                holder.itemView.setOnClickListener(v ->
                        check.performClick());
                check.setChecked(itemsToRemove.contains(item));
            }
        }

        boolean isSomethingSelected() {
            return itemsToRemove.size() != 0;
        }

        boolean isAllSelected() {
            return itemsToRemove.size() == CurrencyValuesHelper.visibleList.size();
        }

        boolean isSelectionMode() {
            return curMode == SELECTION_MODE;
        }

        @Override
        public int getItemCount() {
            return size;
        }

        void notifyModeChanged(RecyclerView.ViewHolder holder) {
            if (curMode == SELECTION_MODE) {
                curMode = REORDER_MODE;
                itemTouchHelper.attachToRecyclerView(recycler);
                itemsToRemove.clear();
            } else {
                curMode = SELECTION_MODE;
                itemTouchHelper.attachToRecyclerView(null);
                if (parent.deleteTooltip != null) {
                    parent.isFirstTimeTooltipShown = false;
                    parent.deleteTooltip.dismiss();
                }
                itemsToRemove.add(CurrencyValuesHelper.visibleList.get(holder.getAdapterPosition()));
            }
            parent.toggleElementsVisibility();
            notifyDataSetChanged();
        }

        class Holder extends RecyclerView.ViewHolder {

            TextView code;
            ImageView handle;
            CheckBox check;
            TextView name;

            Holder(final View itemView) {
                super(itemView);
                code = itemView.findViewById(R.id.currency_code);
                name = itemView.findViewById(R.id.currency_name);
                handle = itemView.findViewById(R.id.handle);
                check = itemView.findViewById(R.id.checkbox_add);
            }

        }

    }

}
