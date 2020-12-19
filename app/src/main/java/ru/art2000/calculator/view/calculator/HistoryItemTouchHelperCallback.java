package ru.art2000.calculator.view.calculator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import ru.art2000.calculator.R;
import ru.art2000.extensions.Consumer;
import ru.art2000.helpers.AndroidHelper;

class HistoryItemTouchHelperCallback extends ItemTouchHelper.SimpleCallback {

    private Context context;
    private Consumer<Integer> onItemSwiped;

    private Drawable background;
    private Drawable xMark;
    private int xMarkMargin;
    private boolean initiated;

    public HistoryItemTouchHelperCallback(Context context, Consumer<Integer> onItemSwiped) {
        this(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.context = context;
        this.onItemSwiped = onItemSwiped;
    }

    /**
     * Creates a Callback for the given drag and swipe allowance. These values serve as
     * defaults
     * and if you want to customize behavior per ViewHolder, you can override
     * {@link #getSwipeDirs(RecyclerView, ViewHolder)}
     * and / or {@link #getDragDirs(RecyclerView, ViewHolder)}.
     *
     * @param dragDirs  Binary OR of direction flags in which the Views can be dragged. Must be
     *                  composed of {@link #LEFT}, {@link #RIGHT}, {@link #START}, {@link
     *                  #END},
     *                  {@link #UP} and {@link #DOWN}.
     * @param swipeDirs Binary OR of direction flags in which the Views can be swiped. Must be
     *                  composed of {@link #LEFT}, {@link #RIGHT}, {@link #START}, {@link
     *                  #END},
     *                  {@link #UP} and {@link #DOWN}.
     */
    private HistoryItemTouchHelperCallback(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    private void init() {
        background = new ColorDrawable(Color.RED);
        xMark = ContextCompat.getDrawable(context, R.drawable.ic_clear_history);
        ColorFilter whiteColorFilter =
                new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        xMark.setColorFilter(whiteColorFilter);
        xMarkMargin = (int) context.getResources().getDimension(R.dimen.activity_horizontal_margin);
        initiated = true;
    }


    @Override
    public void onChildDraw(@NonNull Canvas c,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState,
                            boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;

        // not sure why, but this method get's called for viewholder that are already swiped away
        if (viewHolder.getBindingAdapterPosition() == -1) {
            // not interested in those
            return;
        }

        if (!initiated) {
            init();
        }

        int windowBackgroundColor = AndroidHelper.getColorAttribute(context, android.R.attr.windowBackground);
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

        // draw x mark
        xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
        xMark.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
        onItemSwiped.consume(viewHolder.getBindingAdapterPosition());
    }
}
