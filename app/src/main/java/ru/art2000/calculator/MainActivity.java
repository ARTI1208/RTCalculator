package ru.art2000.calculator;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.preference.PreferenceFragmentCompat;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Objects;

import ru.art2000.calculator.calculator.CalculatorFragment;
import ru.art2000.calculator.currency_converter.CurrencyConverterFragment;
import ru.art2000.calculator.settings.SettingsFragment;
import ru.art2000.calculator.unit_converter.UnitConverterFragment;
import ru.art2000.extensions.DayNightActivity;
import ru.art2000.helpers.AndroidHelper;
import ru.art2000.helpers.CurrencyValuesHelper;
import ru.art2000.helpers.PrefsHelper;

public class MainActivity extends DayNightActivity {

    boolean doubleBackToExitPressedOnce = false;
    BottomNavigationView navigation;
    Context mContext;
    CurrencyConverterFragment currency_converter = new CurrencyConverterFragment();
    CalculatorFragment calculator = new CalculatorFragment();
    UnitConverterFragment unit_converter = new UnitConverterFragment();
    PreferenceFragmentCompat settings = new SettingsFragment();
    Fragment currentFragment = currency_converter;
    int currentOrder = 0;
    FragmentManager fragmentManager = getSupportFragmentManager();

    boolean isTransitionRunning;
    boolean isTransitioned;

    boolean useViewPager = true;
    boolean useViewPager2 = false;

    @ColorInt
    private int statusBarColor;
    @ColorInt
    private int calculatorStatusBarColor;

    @Override
    protected void onResumeNightModeChanged(int mode) {
        switch (navigation.getSelectedItemId()) {
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        PrefsHelper.initialSetup(mContext);
        setTheme(PrefsHelper.getAppTheme());
        Window window = getWindow();
        new Thread(() -> CurrencyValuesHelper.getDataFromDB(mContext)).start();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Log.d("MainActivityKeys", key);
            }
        } else {
            Log.d("MainActivityKeys", "No keys");
        }

        statusBarColor = AndroidHelper.getColorAttribute(this, R.attr.colorPrimaryDark);
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

        FrameLayout frameLayout = findViewById(R.id.fragment_container);
        ViewPager pager = findViewById(R.id.pager);
        ViewPager2 pager2 = findViewById(R.id.pager2);

        if (useViewPager2) {
            frameLayout.setVisibility(View.GONE);
            pager.setVisibility(View.GONE);
            pager2.setOffscreenPageLimit(3);
            pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    Log.d("PageCallback", String.valueOf(position));
                }
            });

            pager2.setUserInputEnabled(false);
            pager2.setAdapter(new FragmentStateAdapter(this) {
                @NonNull
                @Override
                public Fragment createFragment(int position) {
                    switch (position) {
                        case 0:
                            return currency_converter;
                        default:
                        case 1:
                            return calculator;
                        case 2:
                            return unit_converter;
                        case 3:
                            return settings;
                    }
                }

                @Override
                public int getItemCount() {
                    return 4;
                }
            });

            navigation.setOnNavigationItemSelectedListener(item -> {
                PrefsHelper.setDefaultTab(this, item.getOrder());

                int nextOrder = item.getOrder();
                int nextStatusBarColor;

                switch (item.getItemId()) {
                    default:
                    case R.id.navigation_calc:
                        getIntent().setAction("ru.art2000.calculator.action.CALCULATOR");
                        nextStatusBarColor = calculatorStatusBarColor;
                        break;
                    case R.id.navigation_unit:
                        getIntent().setAction("ru.art2000.calculator.action.CONVERTER");
                        nextStatusBarColor = statusBarColor;
                        break;
                    case R.id.navigation_currency:
                        getIntent().setAction("ru.art2000.calculator.action.CURRENCIES");
                        nextStatusBarColor = statusBarColor;
                        break;
                    case R.id.navigation_settings:
                        getIntent().setAction("ru.art2000.calculator.action.SETTINGS");
                        nextStatusBarColor = statusBarColor;
                        break;
                }

                pager2.setCurrentItem(nextOrder);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.setStatusBarColor(nextStatusBarColor);
                }
                return true;
            });

        } else if (useViewPager) {
            frameLayout.setVisibility(View.GONE);
            pager2.setVisibility(View.GONE);
            pager.setOffscreenPageLimit(3);
            pager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager(),
                    FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                @NonNull
                @Override
                public Fragment getItem(int position) {
                    switch (position) {
                        case 0:
                            return currency_converter;
                        default:
                        case 1:
                            return calculator;
                        case 2:
                            return unit_converter;
                        case 3:
                            return settings;
                    }
                }

                @Override
                public int getCount() {
                    return 4;
                }
            });

            navigation.setOnNavigationItemSelectedListener(item -> {
                PrefsHelper.setDefaultTab(this, item.getOrder());

                int nextOrder = item.getOrder();
                int nextStatusBarColor;

                switch (item.getItemId()) {
                    default:
                    case R.id.navigation_calc:
                        getIntent().setAction("ru.art2000.calculator.action.CALCULATOR");
                        nextStatusBarColor = calculatorStatusBarColor;
                        break;
                    case R.id.navigation_unit:
                        getIntent().setAction("ru.art2000.calculator.action.CONVERTER");
                        nextStatusBarColor = statusBarColor;
                        break;
                    case R.id.navigation_currency:
                        getIntent().setAction("ru.art2000.calculator.action.CURRENCIES");
                        nextStatusBarColor = statusBarColor;
                        break;
                    case R.id.navigation_settings:
                        getIntent().setAction("ru.art2000.calculator.action.SETTINGS");
                        nextStatusBarColor = statusBarColor;
                        break;
                }

                pager.setCurrentItem(nextOrder);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.setStatusBarColor(nextStatusBarColor);
                }
                return true;
            });

        } else {
            pager.setVisibility(View.GONE);
            pager2.setVisibility(View.GONE);
            fragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, currency_converter, "CurrencyConverterFragment")

                    .add(R.id.fragment_container, calculator, "CalculatorFragment")
                    .hide(calculator)
                    .add(R.id.fragment_container, unit_converter, "UnitConverterFragment")
                    .hide(unit_converter)
                    .add(R.id.fragment_container, settings, "SettingsFragment")
                    .hide(settings)

                    .commit();

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
                if (isTransitionRunning && isTransitioned) {
                    return false;
                }

                PrefsHelper.setDefaultTab(this, item.getOrder());

                Fragment nextFragment;
                int nextOrder = item.getOrder();
                int nextStatusBarColor;

                switch (item.getItemId()) {
                    default:
                    case R.id.navigation_calc:
                        getIntent().setAction("ru.art2000.calculator.action.CALCULATOR");
                        nextFragment = calculator;
                        nextStatusBarColor = calculatorStatusBarColor;
                        break;
                    case R.id.navigation_unit:
                        getIntent().setAction("ru.art2000.calculator.action.CONVERTER");
                        nextFragment = unit_converter;
                        nextStatusBarColor = statusBarColor;
                        break;
                    case R.id.navigation_currency:
                        getIntent().setAction("ru.art2000.calculator.action.CURRENCIES");
                        nextFragment = currency_converter;
                        nextStatusBarColor = statusBarColor;
                        break;
                    case R.id.navigation_settings:
                        getIntent().setAction("ru.art2000.calculator.action.SETTINGS");
                        nextFragment = settings;
                        nextStatusBarColor = statusBarColor;
                        break;
                }

                //noinspection ConstantConditions
                nextFragment.setEnterTransition(
                        getEnterTransition(currentOrder, nextOrder)
                                .addTarget(nextFragment.getView()));

                //noinspection ConstantConditions
                currentFragment.setExitTransition(
                        getExitTransition(currentOrder, item.getOrder())
                                .addTarget(currentFragment.getView())
                                .addListener(new Transition.TransitionListener() {
                                    @Override
                                    public void onTransitionStart(@NonNull Transition transition) {
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

                isTransitionRunning = true;
                isTransitioned = nextFragment.getView() != null;
                fragmentManager
                        .beginTransaction()
                        .hide(currentFragment)
                        .show(nextFragment)
                        .runOnCommit(() -> {
                            currentFragment = nextFragment;
                            currentOrder = nextOrder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                window.setStatusBarColor(nextStatusBarColor);
                            }
                        })
                        .commit();

                return true;
            });
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