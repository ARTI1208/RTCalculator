package ru.art2000.calculator.currency_converter;

import android.animation.Animator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import ru.art2000.calculator.R;
import ru.art2000.extensions.DayNightActivity;
import ru.art2000.helpers.AndroidHelper;
import ru.art2000.helpers.CurrencyValuesHelper;
import ru.art2000.helpers.PrefsHelper;
import ru.art2000.helpers.SnackbarThemeHelper;

public class EditCurrenciesActivity extends DayNightActivity {

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

    LinearLayout searchViewLayout;
    SearchView barSearchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(PrefsHelper.getAppTheme());
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currencies_editor);
        mContext = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        searchViewLayout = findViewById(R.id.search_view_layout);
        barSearchView = findViewById(R.id.search_view);
        barSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                add.setNewList(add.searchByQuery(newText));
                return true;
            }
        });

        ViewPager pager = findViewById(R.id.pager);
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
        tabs = findViewById(R.id.tabs);
        pager.setAdapter(new CurrencyEditorPagerAdapter(getSupportFragmentManager()));
        tabs.setupWithViewPager(pager);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        searchViewLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        add.recyclerViewBottomPadding = searchViewLayout.getMeasuredHeight();

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTab = tab.getPosition();
                fab.hide();
                modifyVisualElements(tab.getPosition());
                toggleElementsVisibility();
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

        if (add.adapter.size == 0 || selectedTab == 1)
            select.setVisible(false);

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
            if (deleteTooltip.isShown()) {
                return;
            } else {
                deleteTooltip.show();
                return;
            }
        }

        deleteTooltip = SnackbarThemeHelper.createThemedSnackbar(findViewById(R.id.coordinator),
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
                deselect.setVisible(true);
            } else {
                fab.hide();
                deselect.setVisible(false);
            }
            select.setVisible(!add.adapter.isAllSelected());
        } else {
            boolean selection = edit.adapter.isSelectionMode();
            if (selection) {
                fab.show();
            } else {
                fab.hide();
            }
            select.setVisible(selection && !edit.adapter.isAllSelected());
            deselect.setVisible(selection);
        }
    }

    public void setNewFabImage(int resId) {
        currentDrawable = resId;
        fab.hide();
    }

    public void modifyVisualElements(int tabPos) {
        if (tabPos == 0) {
            setNewFabImage(checkDrawable);
            fab.setOnClickListener(v -> {
                CurrencyValuesHelper.makeItemsVisible(this, add.adapter.itemsToAdd);
                changeDone = true;
                add.adapter.setNewData();
                edit.adapter.setNewData();
                toggleElementsVisibility();
                CurrencyValuesHelper.writeValuesToDB(mContext);
            });
        } else {
            setNewFabImage(deleteDrawable);
            fab.setOnClickListener(v -> {
                CurrencyValuesHelper.hideItems(edit.adapter.itemsToRemove);
                changeDone = true;
                add.filterList();
                add.adapter.setNewData();
                edit.adapter.notifyModeChanged(null);
                edit.adapter.setNewData();
                CurrencyValuesHelper.writeValuesToDB(mContext);
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
