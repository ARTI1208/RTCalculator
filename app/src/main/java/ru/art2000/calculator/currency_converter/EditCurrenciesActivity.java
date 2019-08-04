package ru.art2000.calculator.currency_converter;

import android.animation.Animator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.tabs.TabLayout;

import ru.art2000.calculator.Helper;
import ru.art2000.calculator.R;
import ru.art2000.calculator.settings.PrefsHelper;
import ru.art2000.extensions.CurrencyValues;
import ru.art2000.extensions.DayNightActivity;

public class EditCurrenciesActivity extends DayNightActivity {

    public CurrenciesAddFragment add = new CurrenciesAddFragment();
    public CurrenciesEditFragment edit = new CurrenciesEditFragment();
    Context mContext;
    boolean changeDone = false;
    FloatingActionButton fab;
    MenuItem deselect;
    MenuItem select;
    int selectedTab = 0;
    @DrawableRes
    int checkDrawable = R.drawable.ic_currencies_done;
    @DrawableRes
    int deleteDrawable = R.drawable.ic_clear_history;
    @DrawableRes
    int currentDrawable = checkDrawable;

    LinearLayout searchViewLayout;
    SearchView barSearchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(PrefsHelper.getAppTheme());
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_currencies_layout);
        mContext = getBaseContext();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        applyMenuIconTint(toolbar.getNavigationIcon());
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

        searchViewLayout = findViewById(R.id.search_view_layout);
        searchViewLayout.setVisibility(View.VISIBLE);
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
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        TabLayout tabs = findViewById(R.id.tabs);
        pager.setAdapter(new CurrencyEditorPagerAdapter(getSupportFragmentManager()));
        tabs.setupWithViewPager(pager);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTab = tab.getPosition();
                fab.hide();
                modifyFAB(tab.getPosition());
                toggleElementsVisibility();
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
        modifyFAB(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.currencies_editor_menu, menu);

        deselect = menu.findItem(R.id.deselect_all);
        applyMenuIconTint(deselect.getIcon());
        deselect.setVisible(false);

        select = menu.findItem(R.id.select_all);
        applyMenuIconTint(select.getIcon());

        if (add.adapter.size == 0)
            select.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d("menuitem", String.valueOf(item.getItemId()));
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

    private void applyMenuIconTint(Drawable icon) {
        icon.setColorFilter(new PorterDuffColorFilter(
                Helper.getAccentColor(this), PorterDuff.Mode.SRC_ATOP));
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

    public void modifyFAB(int tabPos) {
        if (tabPos == 0) {
            setNewFabImage(checkDrawable);
            fab.setOnClickListener(v -> {
                CurrencyValues.makeItemsVisible(this, add.adapter.itemsToAdd);
                changeDone = true;
                add.adapter.setNewData();
                edit.adapter.setNewData();
                toggleElementsVisibility();
                CurrencyValues.writeValuesToDB(mContext);
            });
        } else {
            setNewFabImage(deleteDrawable);
            fab.setOnClickListener(v -> {
                CurrencyValues.hideItems(this, edit.adapter.itemsToRemove);
                changeDone = true;
                add.filterList();
                add.adapter.setNewData();
                edit.adapter.notifyModeChanged(null);
                edit.adapter.setNewData();
                CurrencyValues.writeValuesToDB(mContext);
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
        Fragment[] fragments = {add, edit};

        CurrencyEditorPagerAdapter(FragmentManager fm) {
            super(fm);
            categories = getResources().getStringArray(R.array.currency_categories);
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
