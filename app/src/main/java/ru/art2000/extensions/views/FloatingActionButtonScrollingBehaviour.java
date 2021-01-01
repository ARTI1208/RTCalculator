package ru.art2000.extensions.views;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FloatingActionButtonScrollingBehaviour extends FloatingActionButton.Behavior {

    private static final int SHOWN = 0;
    private static final int HIDDEN = 1;
    private static final int ANIMATING_DOWN = 2;
    private static final int ANIMATING_UP = 3;
    private static final long ANIMATION_DURATION = 100;
    private static final int SCROLL_OFFSET = 1;
    private int state = SHOWN;

    private boolean isListenerAdded = false;

    public FloatingActionButtonScrollingBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                       @NonNull FloatingActionButton child,
                                       @NonNull View directTargetChild,
                                       @NonNull View target,
                                       int axes,
                                       int type) {
        if (!isListenerAdded) {
            isListenerAdded = true;
            child.addOnShowAnimationListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    child.setTranslationY(0);
                    state = SHOWN;
                }

                @Override
                public void onAnimationEnd(Animator animator) {

                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                               @NonNull FloatingActionButton child,
                               @NonNull View target,
                               int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed,
                               int type,
                               @NonNull int[] consumed) {
        setupAnimation(child, dyConsumed);
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, type, consumed);
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                  @NonNull FloatingActionButton child,
                                  @NonNull View target,
                                  int dx, int dy,
                                  @NonNull int[] consumed,
                                  int type) {
        setupAnimation(child, dy);
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
    }

    private void setupAnimation(FloatingActionButton child, int dy) {
        if (dy > SCROLL_OFFSET && state == SHOWN) {
            state = ANIMATING_DOWN;
            CoordinatorLayout.MarginLayoutParams layoutParams =
                    (CoordinatorLayout.MarginLayoutParams) child.getLayoutParams();
            child.animate()
                    .translationY(child.getHeight() + layoutParams.bottomMargin)
                    .setInterpolator(new LinearInterpolator())
                    .setDuration(ANIMATION_DURATION)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            state = HIDDEN;
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    }).start();
        } else if (dy < -SCROLL_OFFSET && state == HIDDEN) {
            state = ANIMATING_UP;
            child.animate()
                    .translationY(0)
                    .setInterpolator(new LinearInterpolator())
                    .setDuration(ANIMATION_DURATION)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            state = SHOWN;
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    }).start();
        }
    }
}