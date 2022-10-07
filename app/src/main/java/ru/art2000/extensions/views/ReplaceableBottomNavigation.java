package ru.art2000.extensions.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.shape.MaterialShapeDrawable;

import ru.art2000.extensions.fragments.INavigationFragment;
import ru.art2000.extensions.fragments.IReplaceableFragment;
import ru.art2000.helpers.AndroidHelper;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class ReplaceableBottomNavigation extends BottomNavigationView {

    private final SparseArray<IReplaceableFragment> replaceables = new SparseArray<>();
    private IReplaceableFragment currentReplaceable;
    private boolean firstReplaceDone;
    private boolean isTransitionRunning;
    private ViewPager2 attachedPager2;
    private FragmentManager mFragmentManager;

    public ReplaceableBottomNavigation(Context context) {
        super(context);
        init();
    }

    public ReplaceableBottomNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReplaceableBottomNavigation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Nullable
    public IReplaceableFragment getReplaceable(int position) {
        return replaceables.get(position, null);
    }

    private void init() {
        super.setOnItemSelectedListener(item -> {
            onNavigationItemSelected(item);
            return true;
        });
        setOnItemReselectedListener(this::onNavigationItemReselected);
    }

    private void onNavigationItemSelected(MenuItem item) {

        IReplaceableFragment replaceable = getReplaceable(item.getOrder());

        if (replaceable != null) {
            int position = replaceables.indexOfValue(replaceable);

            if (attachedPager2 != null) {
                sendReplaceCallback(replaceable);
                attachedPager2.setCurrentItem(position);
            }

            if (mFragmentManager != null) {
                beginFragmentReplace(replaceable);
            }
        }
    }

    private void onNavigationItemReselected(MenuItem item) {

        if (!firstReplaceDone) {
            onNavigationItemSelected(item);
            firstReplaceDone = true;
        } else {
            if (currentReplaceable != null) {
                currentReplaceable.onReselected();
            }
        }
    }

    @Override
    public void setOnItemReselectedListener(@Nullable OnItemReselectedListener listener) {
        super.setOnItemReselectedListener(item -> {
            onNavigationItemReselected(item);

            if (listener != null && firstReplaceDone) {
                listener.onNavigationItemReselected(item);
            }
        });
    }

    @Override
    public void setOnItemSelectedListener(@Nullable OnItemSelectedListener listener) {
        super.setOnItemSelectedListener(item -> {
            if (listener == null) {
                return false;
            }
            boolean value = listener.onNavigationItemSelected(item);
            onNavigationItemSelected(item);
            if (!firstReplaceDone) {
                firstReplaceDone = true;
            }
            return value && !isTransitionRunning;
        });
    }

    public void setupWithViewPager2(AppCompatActivity parentActivity,
                                    ViewPager2 pager2,
                                    INavigationFragment... replaceableFragments) {

        setReplaceableFragments(replaceableFragments);
        pager2.setOffscreenPageLimit(replaceableFragments.length - 1);
        pager2.setUserInputEnabled(false);
        pager2.setAdapter(new FragmentStateAdapter(parentActivity) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return (Fragment) replaceableFragments[position];
            }

            @Override
            public int getItemCount() {
                return replaceableFragments.length;
            }
        });
        attachedPager2 = pager2;
    }

    public void setupWithFragments(AppCompatActivity parentActivity,
                                   int containerId,
                                   INavigationFragment... replaceableFragments) {

        setReplaceableFragments(replaceableFragments);

        mFragmentManager = parentActivity.getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        for (INavigationFragment navigationFragment : replaceableFragments) {
            Fragment fragment = (Fragment) navigationFragment;
            String tag = fragment.getClass().getSimpleName();
            if (mFragmentManager.findFragmentByTag(tag) == null) {
                fragmentTransaction
                        .add(containerId, fragment, tag)
                        .hide(fragment);
            }
        }

        fragmentTransaction.commitNow();
    }

    private void beginFragmentReplace(IReplaceableFragment replaceable) {

        if (isTransitionRunning) {
            return;
        }

        Fragment previousFragment = (Fragment) currentReplaceable;
        Fragment nextFragment = (Fragment) replaceable;

        int previousPosition = replaceables.indexOfValue(currentReplaceable);
        int nextPosition = replaceables.indexOfValue(replaceable);

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        nextFragment.setEnterTransition(
                getEnterTransition(previousPosition, nextPosition)
                        .addTarget(nextFragment.requireView())
                        .addListener(new Transition.TransitionListener() {
                            @Override
                            public void onTransitionStart(@NonNull Transition transition) {
                                isTransitionRunning = true;
                            }

                            @Override
                            public void onTransitionEnd(@NonNull Transition transition) {
                                isTransitionRunning = false;
                            }

                            @Override
                            public void onTransitionCancel(@NonNull Transition transition) {
                                isTransitionRunning = false;
                            }

                            @Override
                            public void onTransitionPause(@NonNull Transition transition) {

                            }

                            @Override
                            public void onTransitionResume(@NonNull Transition transition) {

                            }
                        }));

        if (previousFragment != null) {
            previousFragment.setExitTransition(
                    getExitTransition(previousPosition, nextPosition)
                            .addTarget(previousFragment.requireView()));

            fragmentTransaction.hide(previousFragment);
        }

        fragmentTransaction
                .show(nextFragment)
                .runOnCommit(() -> sendReplaceCallback(nextPosition))
                .commitNow();
    }

    public void setReplaceableFragments(INavigationFragment... replaceableFragments) {
        Menu menu = getMenu();
        menu.clear();

        for (int order = 0; order < replaceableFragments.length; ++order) {
            INavigationFragment replaceableFragment = replaceableFragments[order];
            int id = replaceableFragment.getReplaceableId();
            id = id == -1 ? Menu.NONE : id;

            MenuItem item = menu.add(
                    Menu.NONE,
                    id,
                    order,
                    replaceableFragment.getTitle());
            int iconRes = replaceableFragment.getIcon();
            if (iconRes != -1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    item.setIcon(iconRes);
                } else {
                    Drawable iconDrawable = ResourcesCompat.getDrawable(
                            getContext().getResources(), iconRes, getContext().getTheme()
                    );
                    item.setIcon(iconDrawable);
                }
            }
            this.replaceables.put(order, replaceableFragment);
        }
    }

    private void sendReplaceCallback(IReplaceableFragment replaceable) {
        if (replaceable != null) {
            if (replaceable == currentReplaceable) {
                replaceable.onReselected();
                return;
            }

            replaceable.onReplace(currentReplaceable);
        }
        if (currentReplaceable != null) {
            currentReplaceable.onReplaced(replaceable);
        }

        currentReplaceable = replaceable;
    }

    private void sendReplaceCallback(int position) {
        IReplaceableFragment replaceable = replaceables.valueAt(position);
        sendReplaceCallback(replaceable);
    }

    private Transition getEnterTransition(int fromPosition, int toPosition) {
        Transition transition;
        if (fromPosition < toPosition) {
            transition = new Slide(Gravity.END);
        } else {
            transition = new Slide(Gravity.START);
        }
        return transition.setInterpolator(new AccelerateInterpolator());
    }

    private Transition getExitTransition(int fromPosition, int toPosition) {
        Transition transition;
        if (fromPosition < toPosition) {
            transition = new Slide(Gravity.START);
        } else {
            transition = new Slide(Gravity.END);
        }
        return transition.setInterpolator(new AccelerateInterpolator());
    }
}
