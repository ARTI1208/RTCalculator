package ru.art2000.calculator;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;
import java.util.Objects;

import ru.art2000.calculator.calculator.CalculatorFragment;
import ru.art2000.calculator.currency_converter.CurrencyConverterFragment;
import ru.art2000.calculator.settings.SettingsFragment;
import ru.art2000.calculator.unit_converter.UnitConverterFragment;
import ru.art2000.extensions.DayNightActivity;
import ru.art2000.extensions.ReplaceableBottomNavigation;
import ru.art2000.extensions.ScrollControlledViewPager;
import ru.art2000.helpers.AndroidHelper;
import ru.art2000.helpers.CurrencyValuesHelper;
import ru.art2000.helpers.PrefsHelper;

public class MainActivity extends DayNightActivity {

    boolean useViewPager = true;
    boolean useViewPager2 = false;
    private boolean doubleBackToExitPressedOnce = false;
    private ReplaceableBottomNavigation navigation;
    private Context mContext;
    private CurrencyConverterFragment currency_converter;
    private CalculatorFragment calculator;
    private UnitConverterFragment unit_converter;
    private SettingsFragment settings;
    @ColorInt
    private int normalStatusBarColor;
    @ColorInt
    private int calculatorStatusBarColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        PrefsHelper.initialSetup(mContext);
        setTheme(PrefsHelper.getAppTheme());
        new Thread(() -> CurrencyValuesHelper.getDataFromDB(mContext)).start();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<Fragment> list = getSupportFragmentManager().getFragments();
        for (Fragment fragment : list) {
            if (fragment instanceof CurrencyConverterFragment) {
                currency_converter = (CurrencyConverterFragment) fragment;
            } else if (fragment instanceof CalculatorFragment) {
                calculator = (CalculatorFragment) fragment;
            } else if (fragment instanceof UnitConverterFragment) {
                unit_converter = (UnitConverterFragment) fragment;
            } else if (fragment instanceof SettingsFragment) {
                settings = (SettingsFragment) fragment;
            }
        }
        if (currency_converter == null) {
            currency_converter = new CurrencyConverterFragment();
        }
        if (calculator == null) {
            calculator = new CalculatorFragment();
        }
        if (unit_converter == null) {
            unit_converter = new UnitConverterFragment();
        }
        if (settings == null) {
            settings = new SettingsFragment();
        }

        normalStatusBarColor = AndroidHelper.getColorAttribute(this, R.attr.colorPrimaryDark);
        calculatorStatusBarColor =
                AndroidHelper.getColorAttribute(this, R.attr.calc_input_bg);

        navigation = findViewById(R.id.navigation);

        navigation.setOnNavigationItemReselectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_currency:
                    currency_converter.scrollToTop();
                    break;
                case R.id.navigation_calc:
                    SlidingUpPanelLayout.PanelState state = SlidingUpPanelLayout.PanelState.EXPANDED;
                    if (calculator.panel.getPanelState() == state)
                        state = SlidingUpPanelLayout.PanelState.COLLAPSED;
                    calculator.panel.setPanelState(state);
                    break;
            }
        });

        navigation.setOnNavigationItemSelectedListener(item -> {
            PrefsHelper.setDefaultTab(this, item.getOrder());

            switch (item.getItemId()) {
                default:
                case R.id.navigation_calc:
                    getIntent().setAction("ru.art2000.calculator.action.CALCULATOR");
                    break;
                case R.id.navigation_unit:
                    getIntent().setAction("ru.art2000.calculator.action.CONVERTER");
                    break;
                case R.id.navigation_currency:
                    getIntent().setAction("ru.art2000.calculator.action.CURRENCIES");
                    break;
                case R.id.navigation_settings:
                    getIntent().setAction("ru.art2000.calculator.action.SETTINGS");
                    break;
            }
            return true;
        });

        FrameLayout frameLayout = findViewById(R.id.fragment_container);
        ScrollControlledViewPager pager = findViewById(R.id.pager);
        ViewPager2 pager2 = findViewById(R.id.pager2);

        if (useViewPager2) {
            frameLayout.setVisibility(View.GONE);
            pager.setVisibility(View.GONE);

            navigation.setupWithViewPager2(
                    this,
                    pager2,
                    unit_converter, currency_converter, calculator, settings);

        } else if (useViewPager) {
            frameLayout.setVisibility(View.GONE);
            pager2.setVisibility(View.GONE);

            navigation.setupWithViewPager(
                    this,
                    pager,
                    unit_converter, currency_converter, calculator, settings);

        } else {
            pager.setVisibility(View.GONE);
            pager2.setVisibility(View.GONE);

            navigation.setupWithFragments(this,
                    R.id.fragment_container,
                    currency_converter, calculator, unit_converter, settings);

        }

        int tabId;
        if (Objects.requireNonNull(getIntent().getAction()).equals("android.intent.action.MAIN")) {
            tabId = PrefsHelper.getDefaultNavItem();
        } else {
            switch (getIntent().getAction()) {
                default:
                case "ru.art2000.calculator.action.CALCULATOR":
                    tabId = R.id.navigation_calc;
                    break;
                case "ru.art2000.calculator.action.CONVERTER":
                    tabId = R.id.navigation_unit;
                    break;
                case "ru.art2000.calculator.action.CURRENCIES":
                    tabId = R.id.navigation_currency;
                    break;
                case "ru.art2000.calculator.action.SETTINGS":
                    tabId = R.id.navigation_settings;
                    break;
            }
        }
        navigation.setSelectedItemId(tabId);
    }

    @Override
    protected void onPause() {
        if (currency_converter.adapter != null)
            currency_converter.adapter.removeEditText2();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (navigation.getSelectedItemId() == R.id.navigation_calc
                && (calculator.panel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                calculator.panel.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {

            calculator.panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            if (doubleBackToExitPressedOnce) {
                finish();
                return;
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(mContext, R.string.twice_tap_exit, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() ->
                    doubleBackToExitPressedOnce = false, 2000);
        }
    }

    public void updateUnitView() {
        unit_converter.regenerateAdapter();
    }

    public void changeStatusBarColor(boolean isCalculatorPage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isCalculatorPage) {
                getWindow().setStatusBarColor(calculatorStatusBarColor);
            } else {
                getWindow().setStatusBarColor(normalStatusBarColor);
            }
        }
    }
}