package ru.art2000.calculator.currency_converter.view;

import android.animation.Animator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.art2000.calculator.R;
import ru.art2000.calculator.currency_converter.model.CurrencyItem;
import ru.art2000.calculator.currency_converter.view_model.CurrenciesSettingsModel;
import ru.art2000.calculator.currency_converter.view_model.CurrencyDependencies;
import ru.art2000.extensions.DayNightActivity;
import ru.art2000.helpers.AndroidHelper;
import ru.art2000.helpers.CurrencyValuesHelper;
import ru.art2000.helpers.PrefsHelper;
import ru.art2000.helpers.SnackbarThemeHelper;

public class CurrenciesSettingsActivity extends DayNightActivity {

    public CurrenciesAddFragment add = new CurrenciesAddFragment();
    public CurrenciesEditFragment edit = new CurrenciesEditFragment();
    Context mContext;
    boolean changeDone = false;
    FloatingActionButton fab;
    MenuItem deselect;
    MenuItem select;

    TabLayout tabs;

    int selectedTab = 0;
    @DrawableRes
    int checkDrawable = R.drawable.ic_currencies_done;
    @DrawableRes
    int deleteDrawable = R.drawable.ic_clear_history;
    @DrawableRes
    int currentDrawable = checkDrawable;

    boolean isFirstTimeTooltipShown = !PrefsHelper.isDeleteTooltipShown();
    Snackbar deleteTooltip;

    CoordinatorLayout coordinatorLayout;
    LinearLayout searchViewLayout;
    SearchView barSearchView;

    boolean useViewPager2 = false;
    boolean optionsMenuCreated = false;


    CurrenciesSettingsModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(PrefsHelper.getAppTheme());
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currencies_editor);
        mContext = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        model = new CurrenciesSettingsModel(this.getApplication());

        fab = findViewById(R.id.floatingActionButton);
        fab.addOnShowAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                fab.setImageResource(currentDrawable);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {
                fab.setImageResource(currentDrawable);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        fab.addOnHideAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (currentDrawable == deleteDrawable) {
                    showDeleteTip();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        coordinatorLayout = findViewById(R.id.coordinator);
        searchViewLayout = findViewById(R.id.search_view_layout);
        barSearchView = findViewById(R.id.search_view);

        tabs = findViewById(R.id.tabs);

        ViewPager pager = findViewById(R.id.pager);
        ViewPager2 pager2 = findViewById(R.id.pager2);

        if (useViewPager2) {
            pager.setVisibility(View.GONE);

            pager2.setOffscreenPageLimit(2);
            pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    int maxScroll = pager2.getMeasuredWidth();
                    int currentScroll = maxScroll * position + positionOffsetPixels;
                    searchViewLayout.setTranslationX(-currentScroll);
                    if (deleteTooltip != null) {
                        deleteTooltip.getView().setTranslationX(maxScroll - currentScroll);
                    }
                }
            });
            CurrencyEditorPager2Adapter pager2Adapter = new CurrencyEditorPager2Adapter();
            pager2.setAdapter(pager2Adapter);
            new TabLayoutMediator(tabs, pager2, (tab, position) ->
                    tab.setText(pager2Adapter.getPageTitle(position))).attach();
        } else {

            pager2.setVisibility(View.GONE);

            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    int maxScroll = pager.getMeasuredWidth();
                    int currentScroll = maxScroll * position + positionOffsetPixels;
                    searchViewLayout.setTranslationX(-currentScroll);
                    if (deleteTooltip != null) {
                        deleteTooltip.getView().setTranslationX(maxScroll - currentScroll);
                    }
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            pager.setAdapter(new CurrencyEditorPagerAdapter(getSupportFragmentManager()));
            tabs.setupWithViewPager(pager);
        }

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTab = tab.getPosition();
                fab.hide();
                modifyVisualElements(tab.getPosition());
                if (optionsMenuCreated) {
                    toggleElementsVisibility();
                }
                if (selectedTab == 1 && !fab.isShown()) {
                    showDeleteTip();
                } else if (selectedTab == 0 && deleteTooltip != null) {
                    deleteTooltip.dismiss();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    default:
                    case 0:
                        add.scrollToTop();
                        break;
                    case 1:
                        edit.scrollToTop();
                        break;
                }
            }

        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        searchViewLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        add.recyclerViewBottomPadding = searchViewLayout.getMeasuredHeight();

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        layoutParams.bottomMargin = add.recyclerViewBottomPadding;
        fab.setLayoutParams(layoutParams);

        selectedTab = tabs.getSelectedTabPosition();
        modifyVisualElements(selectedTab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.currencies_editor_menu, menu);

        deselect = menu.findItem(R.id.deselect_all);
        applyMenuIconTint(deselect.getIcon());
        deselect.setVisible(false);

        select = menu.findItem(R.id.select_all);
        applyMenuIconTint(select.getIcon());

        if (add.adapter.getItemCount() == 0 || selectedTab == 1)
            select.setVisible(false);

        optionsMenuCreated = true;

        barSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                add.filterList(newText);
                return true;
            }
        });


        LifecycleObserver addLifecycleObserver = new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (add.adapter != null) {
                    add.getLifecycle().removeObserver(this);

                    add.adapter.selectedItemsCount.observe(CurrenciesSettingsActivity.this, selectedAndTotal -> {
                        toggleElementsVisibility();
                    });
                }

            }
        };

        add.getLifecycle().addObserver(addLifecycleObserver);


        LifecycleObserver editLifecycleObserver = new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (edit.adapter != null) {
                    add.getLifecycle().removeObserver(this);

                    edit.adapter.selectedItemsCount.observe(CurrenciesSettingsActivity.this, selectedAndTotal -> {
                        toggleElementsVisibility();
                    });
                }

            }
        };

        edit.getLifecycle().addObserver(editLifecycleObserver);

//        toggleElementsVisibility();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deselect_all:
                if (selectedTab == 0)
                    add.adapter.deselectAll();
                else
                    edit.adapter.deselectAll();
                break;
            case R.id.select_all:
                if (selectedTab == 0)
                    add.adapter.selectAll();
                else
                    edit.adapter.selectAll();
                break;
        }
        toggleElementsVisibility();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (changeDone)
            setResult(1);
        else
            setResult(0);
        finish();
        return true;
    }

    public void showDeleteTip() {
        if (!isFirstTimeTooltipShown) {
            return;
        }

        if (deleteTooltip != null) {
            if (!deleteTooltip.isShown()) {
                deleteTooltip.show();
            }
            return;
        }

        deleteTooltip = createStyledSnackbar(
                R.string.tooltip_remove_currency, Snackbar.LENGTH_INDEFINITE);

        deleteTooltip.addCallback(new Snackbar.Callback() {
            @Override
            public void onShown(Snackbar sb) {
                PrefsHelper.setDeleteTooltipShown();
            }

            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event == Snackbar.Callback.DISMISS_EVENT_ACTION
                        || event == Snackbar.Callback.DISMISS_EVENT_SWIPE) {
                    isFirstTimeTooltipShown = false;
                }
            }
        });
        deleteTooltip.setAction(R.string.action_tooltip_got_it, actionView -> {
        });
        deleteTooltip.show();
    }

    private void applyMenuIconTint(Drawable icon) {
        icon.setColorFilter(new PorterDuffColorFilter(
                AndroidHelper.getColorAttribute(this, R.attr.colorAccent),
                PorterDuff.Mode.SRC_ATOP));
    }

    public void toggleElementsVisibility() {
        if (selectedTab == 0) {
            if (add.adapter.isSomethingSelected()) {
                fab.show();
            } else {
                fab.hide();
            }
            select.setVisible(!add.adapter.isAllSelected());
            deselect.setVisible(add.adapter.isSomethingSelected());
        } else {
            boolean selection = edit.adapter.isSelectionMode();
            if (selection) {
                fab.show();
            } else {
                fab.hide();
            }
            select.setVisible(selection && !edit.adapter.isAllSelected());
            deselect.setVisible(selection && edit.adapter.isSomethingSelected());
        }
    }

    public void setNewFabImage(int resId) {
        currentDrawable = resId;
        fab.hide();
    }

    private Snackbar styleSnackbar(Snackbar snackbar) {
        View snackbarView = snackbar.getView();

        MaterialCardView cardView = findViewById(R.id.card_wrapper);
        ViewGroup.MarginLayoutParams cardLayoutParams =
                (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();

        int leftMargin = cardLayoutParams.leftMargin + cardView.getPaddingLeft();
        int rightMargin = cardLayoutParams.rightMargin + cardView.getPaddingRight();
        int height = cardView.getChildAt(0).getMeasuredHeight();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            height += cardView.getPaddingBottom()
                    + cardView.getStrokeWidth()
                    - (int) cardView.getCardElevation();
            leftMargin -= cardView.getCardElevation() + cardView.getStrokeWidth();
            rightMargin -= cardView.getCardElevation() + cardView.getStrokeWidth();
        } else {
            snackbarView.setElevation(0);
        }

        snackbarView.setMinimumHeight(height);
        snackbarView.setTranslationY(cardView.getPaddingTop() - cardView.getStrokeWidth());

        ViewGroup.MarginLayoutParams snackLayoutParams =
                (ViewGroup.MarginLayoutParams) snackbarView.getLayoutParams();
        snackLayoutParams.leftMargin = leftMargin;
        snackLayoutParams.rightMargin = rightMargin;
        snackbarView.setLayoutParams(snackLayoutParams);

        return snackbar;
    }

    @SuppressWarnings("SameParameterValue")
    private Snackbar createStyledSnackbar(@StringRes int message, int duration) {
        return styleSnackbar(
                SnackbarThemeHelper.createThemedSnackbar(coordinatorLayout, message, duration));
    }

    @SuppressWarnings("SameParameterValue")
    private Snackbar createStyledSnackbar(@NonNull CharSequence message, int duration) {
        return styleSnackbar(
                SnackbarThemeHelper.createThemedSnackbar(coordinatorLayout, message, duration));
    }

    protected void generateUndoSnackbar(List<CurrencyItem> editedItems, boolean added) {

        String message;

        if (editedItems.size() == 1) {
            message = mContext.getString(
                    added
                            ? R.string.message_item_shown
                            : R.string.message_item_hidden,
                    editedItems.get(0).code);
        } else {
            message = mContext.getResources().getQuantityString(
                    added
                            ? R.plurals.message_items_shown
                            : R.plurals.message_items_hidden,
                    editedItems.size(),
                    editedItems.size());
        }

        Snackbar undoSnackbar = createStyledSnackbar(message, Snackbar.LENGTH_LONG);

        undoSnackbar.setAction(R.string.action_undo, view -> {

            Completable
                    .fromRunnable(() -> {
                        Log.d("UndoAction", added ? "hide" : "add");
                        if (added) {
                            model.makeItemsHidden(editedItems);
                        } else {
                            model.makeItemsVisible(editedItems);
                        }
                    }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> {
                        generateUndoSnackbar(editedItems, !added);
                    }).subscribe();
        });

        undoSnackbar.show();
    }

    public void modifyVisualElements(int tabPos) {
        if (tabPos == 0) {
            setNewFabImage(checkDrawable);
            fab.setOnClickListener(v -> {
                ArrayList<CurrencyItem> selectedItems = add.adapter.getSelectedItems();

                Log.d("ToAddCount", String.valueOf(selectedItems.size()));
                Log.d("ToAddCount", String.valueOf(add.adapter.getSelectedCount()));

                changeDone = true;

                Completable
                        .fromRunnable(() ->
                                CurrencyDependencies
                                        .getCurrencyDatabase(mContext)
                                        .currencyDao()
                                        .makeItemsVisible(selectedItems)
                        ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() ->
                                generateUndoSnackbar(selectedItems, true))
                        .subscribe();

            });
        } else {
            setNewFabImage(deleteDrawable);
            fab.setOnClickListener(v -> {
                List<CurrencyItem> selectedItems = edit.adapter.getSelectedItems();

                changeDone = true;
                edit.adapter.notifyModeChanged(null);

                Completable
                        .fromRunnable(() ->
                                CurrencyDependencies
                                        .getCurrencyDatabase(mContext)
                                        .currencyDao()
                                        .makeItemsHidden(selectedItems)
                        ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() ->
                                generateUndoSnackbar(selectedItems, false))
                        .subscribe();
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (changeDone)
            setResult(1);
        else
            setResult(0);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tabs.clearOnTabSelectedListeners();
    }

    class CurrencyEditorPager2Adapter extends FragmentStateAdapter {

        String[] categories;
        Fragment[] fragments;

        CurrencyEditorPager2Adapter() {
            super(CurrenciesSettingsActivity.this);
            categories = getResources().getStringArray(R.array.currency_categories);

            List<Fragment> list = CurrenciesSettingsActivity.this.getSupportFragmentManager().getFragments();
            if (list.size() > 0) {
                fragments = new Fragment[list.size()];
                fragments = list.toArray(fragments);
                add = (CurrenciesAddFragment) fragments[0];
                if (list.size() > 1) {
                    edit = (CurrenciesEditFragment) fragments[1];
                }
            }

            if (fragments == null) {
                fragments = new Fragment[]{add, edit};
            }
        }

        @Override
        public int getItemCount() {
            return fragments.length;
        }

        @Nullable
        CharSequence getPageTitle(int position) {
            return categories[position];
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments[position];
        }
    }

    class CurrencyEditorPagerAdapter extends FragmentPagerAdapter {

        String[] categories;
        Fragment[] fragments;

        CurrencyEditorPagerAdapter(FragmentManager fm) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            categories = getResources().getStringArray(R.array.currency_categories);

            List<Fragment> list = fm.getFragments();
            if (list.size() > 0) {
                fragments = new Fragment[list.size()];
                fragments = list.toArray(fragments);
                add = (CurrenciesAddFragment) fragments[0];
                edit = (CurrenciesEditFragment) fragments[1];
            }

            if (fragments == null) {
                fragments = new Fragment[]{add, edit};
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return categories[position];
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return categories.length;
        }
    }

}
