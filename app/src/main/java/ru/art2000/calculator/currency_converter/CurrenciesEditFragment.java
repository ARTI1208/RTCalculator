package ru.art2000.calculator.currency_converter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import ru.art2000.calculator.R;
import ru.art2000.extensions.CurrencyItem;
import ru.art2000.extensions.CurrencyValues;

public class CurrenciesEditFragment extends Fragment {

    private View v = null;
    private RecyclerView list;
    private Context mContext;
    EditCurrenciesAdapter adapter;
    private EditShownCurrencies parent;

    public CurrenciesEditFragment() {}

    void scrollToTop(){
        list.smoothScrollToPosition(0);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (v == null) {
            mContext = getActivity();
            parent = (EditShownCurrencies) getActivity();
            v = inflater.inflate(R.layout.modify_currencies_layout, null);
            list = v.findViewById(R.id.modify_currencies_list);
            LinearLayoutManager llm = new LinearLayoutManager(mContext);
            llm.setOrientation(RecyclerView.VERTICAL);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.DOWN | ItemTouchHelper.UP,
                    ItemTouchHelper.LEFT) {

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
                    xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                    xMarkMargin = (int) mContext.getResources().getDimension(R.dimen.activity_horizontal_margin);
                    initiated = true;
                }

                @Override
                public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                    if (dY == 0) {
                        View itemView = viewHolder.itemView;

                        // not sure why, but this method get's called for viewholder that are already swiped away
                        if (viewHolder.getAdapterPosition() == -1) {
                            // not interested in those
                            return;
                        }

                        if (!initiated) {
                            init();
                        }

                        TypedValue itemBackground = new TypedValue();
                        mContext.getTheme().resolveAttribute(android.R.attr.windowBackground, itemBackground, true);
                        int windowBackgroundColor = ContextCompat.getColor(mContext, itemBackground.resourceId);
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

                        // draw x mark
                        xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                        xMark.draw(c);
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }

                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    parent.changeDone = true;
                    CurrencyValues.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    return true;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    parent.changeDone = true;
                    CurrencyValues.hideItems(viewHolder.getAdapterPosition());
                    parent.add.adapter.setNewData();
                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    adapter.size = CurrencyValues.visibleList.size();
                }
            });
            adapter = new EditCurrenciesAdapter(itemTouchHelper);
            itemTouchHelper.attachToRecyclerView(list);
            list.setLayoutManager(llm);
            list.setAdapter(adapter);

        }
        return v;
    }

    class EditCurrenciesAdapter extends RecyclerView.Adapter {

        int size = CurrencyValues.visibleList.size();
        ArrayList<CurrencyItem> itemsToRemove = new ArrayList<>();
        ItemTouchHelper touchHelper;
        final int REORDER_MODE = 0;
        final int SELECTION_MODE = 1;
        int curMode = 0;
        @LayoutRes int selectionItem = R.layout.add_currencies_list_item;
        @LayoutRes int reorderItem = R.layout.edit_currencies_list_item;

        EditCurrenciesAdapter(ItemTouchHelper touchHelper) {
            this.touchHelper = touchHelper;
        }

        void deselectAll(){
            itemsToRemove.clear();
            notifyModeChanged(null);
        }

        void selectAll(){
            itemsToRemove.clear();
            itemsToRemove.addAll(CurrencyValues.visibleList);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return curMode;
        }

        void setNewData(){
            size = CurrencyValues.visibleList.size();
            notifyDataSetChanged();
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

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            TextView code = ((Holder) holder).code;
            TextView name = ((Holder) holder).name;
            ImageView handle = ((Holder) holder).handle;
            CheckBox check = ((Holder) holder).check;
            CurrencyItem item = CurrencyValues.visibleList.get(position);
            code.setText(item.code);
            name.setText(item.nameResourceId);
            if (handle != null) {
                handle.setOnTouchListener((v, event) -> {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        touchHelper.startDrag(holder);
                    }
                    return false;
                });
                holder.itemView.setOnLongClickListener(v -> {
                    notifyModeChanged(holder);
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
                        parent.setFABVisibility();
                });
                holder.itemView.setOnClickListener(v ->
                    check.performClick());
                check.setChecked(itemsToRemove.contains(item));
            }
        }

        boolean isSomethingSelected(){
            return itemsToRemove.size() != 0;
        }

        boolean isAllSelected(){
            return itemsToRemove.size() == CurrencyValues.visibleList.size();
        }

        boolean isSelectionMode(){
            return curMode == SELECTION_MODE;
        }

        @Override
        public int getItemCount() {
            return size;
        }

        void notifyModeChanged(RecyclerView.ViewHolder holder){
            if (curMode == SELECTION_MODE) {
                curMode = REORDER_MODE;
                itemsToRemove.clear();
                parent.setFABVisibility();
            } else {
                curMode = SELECTION_MODE;
                itemsToRemove.add(CurrencyValues.visibleList.get(holder.getAdapterPosition()));
            }
            notifyDataSetChanged();
        }

    }

}
