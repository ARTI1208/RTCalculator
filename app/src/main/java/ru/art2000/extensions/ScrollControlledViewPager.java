package ru.art2000.extensions;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

@SuppressWarnings("unused")
public class ScrollControlledViewPager extends ViewPager {

    private boolean isScrollable = true;

    public ScrollControlledViewPager(@NonNull Context context) {
        super(context);
    }

    public ScrollControlledViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isScrollable() {
        return isScrollable;
    }

    public void setScrollable(boolean scrollable) {
        isScrollable = scrollable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isScrollable;
    }

}
