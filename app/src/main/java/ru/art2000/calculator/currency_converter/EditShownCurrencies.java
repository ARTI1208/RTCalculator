package ru.art2000.calculator.currency_converter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import ru.art2000.calculator.R;
import ru.art2000.calculator.settings.PrefsHelper;
import ru.art2000.extensions.CurrencyValues;

public class EditShownCurrencies extends AppCompatActivity {

    Context mContext;
    boolean changeDone = false;
    CurrenciesAddFragment add = new CurrenciesAddFragment();
    CurrenciesEditFragment edit = new CurrenciesEditFragment();
    FloatingActionButton fab;
    MenuItem deselect;
    MenuItem select;
    int selectedTab = 0;
    @DrawableRes int checkDrawable = R.drawable.ic_currencies_done;
    @DrawableRes int deleteDrawable = R.drawable.ic_clear_history;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(PrefsHelper.getAppTheme());

//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_currencies_layout);
        mContext = getBaseContext();
        Toolbar toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.floatingActionButton);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewPager pager = findViewById(R.id.pager);
        TabLayout tabs = findViewById(R.id.tabs);
        pager.setAdapter(new CurrencyEditorPagerAdapter(getSupportFragmentManager()));
        tabs.setupWithViewPager(pager);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTab = tab.getPosition();
                fab.hide();
                modifyFAB(tab.getPosition());
                setFABVisibility();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
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
        deselect = menu.getItem(0);
        select = menu.getItem(1);
        deselect.setVisible(false);
        if (add.adapter.size == 0)
            select.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.deselect_all:
//                deselect.setVisible(false);
//                select.setVisible(true);
                if (selectedTab == 0)
                    add.adapter.deselectAll();
                else
                    edit.adapter.deselectAll();
                break;
            case R.id.select_all:
//                select.setVisible(false);
//                deselect.setVisible(true);
                if (selectedTab == 0)
                    add.adapter.selectAll();
                else
                    edit.adapter.selectAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        if (changeDone){
            new Thread(()->{
                CurrencyDB DBHelper = new CurrencyDB(mContext);
                DBHelper.writeUpdatedValuesToDB();
                DBHelper.close();
            }).start();
        }
        super.onPause();
    }

    @Override
    public boolean onSupportNavigateUp(){
        if (changeDone)
            setResult(1);
        else
            setResult(0);
        finish();
        return true;
    }

    public void setFABVisibility(){


        Log.d("vussfg", String.valueOf(fab.getVisibility()));
//        while (fab.getVisibility() != View.GONE)
//            fab.hide();
        if (selectedTab == 0){
            if (add.adapter.isSomethingSelected()){
                if (add.selectionState * add.previousSelectionState == 0 ) {
//                    fab.setImageResource(checkDrawable);
                }
                fab.show();
                deselect.setVisible(true);
            } else {
                fab.hide();
                deselect.setVisible(false);
            }
            select.setVisible(!add.adapter.isAllSelected());

        } else {
//            fab.setImageResource(deleteDrawable);
            boolean selection = edit.adapter.isSelectionMode();
            if (selection) {
                fab.show();
            } else
                fab.hide();
            select.setVisible(selection && !edit.adapter.isAllSelected());
            deselect.setVisible(selection);
        }
    }

    public void setNewFabImage(int resId){
        fab.hide();
        fab.setImageResource(resId);
    }

    public void modifyFAB(int tabPos){
//        setFABVisibility();
        if (tabPos == 0){

            setNewFabImage(checkDrawable);

            fab.setOnClickListener(v -> {
                CurrencyValues.makeItemsVisible(add.adapter.itemsToAdd);
                changeDone = true;
                add.adapter.setNewData();
                edit.adapter.setNewData();
                setFABVisibility();
            });
        } else {
            setNewFabImage(deleteDrawable);
            fab.setOnClickListener(v -> {
                CurrencyValues.hideItems(edit.adapter.itemsToRemove);
                changeDone = true;
                add.adapter.setNewData();
                edit.adapter.notifyModeChanged(null);
                edit.adapter.setNewData();
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
