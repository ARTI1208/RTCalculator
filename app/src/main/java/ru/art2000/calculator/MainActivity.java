package ru.art2000.calculator;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import java.util.Objects;
import ru.art2000.calculator.calculator.CalculatorFragment;
import ru.art2000.calculator.currency_converter.CurrencyConverterFragment;
import ru.art2000.calculator.settings.PrefsHelper;
import ru.art2000.calculator.settings.SettingsFragment;
import ru.art2000.calculator.unit_converter.UnitConverterFragment;
import ru.art2000.extensions.CurrencyValues;

public class MainActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    BottomNavigationView navigation;
    Context mContext;
    CurrencyConverterFragment currency_converter = new CurrencyConverterFragment();
    CalculatorFragment calculator = new CalculatorFragment();
    UnitConverterFragment unit_converter = new UnitConverterFragment();
    PreferenceFragmentCompat settings = new SettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = getApplicationContext();
        currency_converter.mContext = this;
        PrefsHelper.initialSetup(mContext);
        setTheme(PrefsHelper.getAppTheme());
        Window window = getWindow();
        window.setUiOptions(1,1);
        
        new Thread(() -> CurrencyValues.getDataFromDB(mContext)).start();
        View view = window.getDecorView();
        int flags = view.getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TypedValue statusBarColor = new TypedValue();
            getTheme().resolveAttribute(R.attr.colorPrimaryDark, statusBarColor, true);
            int status_color = ContextCompat.getColor(mContext, statusBarColor.resourceId);
            if (status_color == ContextCompat.getColor(mContext, R.color.DarkTheme_colorPrimaryDark)) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                view.setSystemUiVisibility(flags);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemReselectedListener(item -> {
            switch (item.getItemId()){
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
            TypedValue statusBarColor = new TypedValue();
            getTheme().resolveAttribute(R.attr.colorPrimaryDark, statusBarColor, true);
            int status_color = ContextCompat.getColor(mContext, statusBarColor.resourceId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            switch (item.getItemId()) {
                default:
                case R.id.navigation_calc:
                    getTheme().resolveAttribute(R.attr.calc_input_bg, statusBarColor, true);
                    int calc_status_color = ContextCompat.getColor(mContext, statusBarColor.resourceId);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            calculator, "CalculatorFragment").addToBackStack("CalculatorFragment").commit();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        window.setStatusBarColor(calc_status_color);
                    break;
                case R.id.navigation_unit:
                    if (PrefsHelper.isUnitViewChanged()){
                        unit_converter = new UnitConverterFragment();
                    }
                    Log.d("cliick", "1");

//                    new Handler().post(() -> {

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                unit_converter, "UnitConverterFragment").addToBackStack("UnitConverterFragment").commit();
//                    });



                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        window.setStatusBarColor(status_color);
                    break;
                case R.id.navigation_currency:
                    Log.d("CurrencyTR", "start");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            currency_converter, "CurrencyConverterFragment").addToBackStack("CurrencyConverterFragment").commit();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        window.setStatusBarColor(status_color);
                    Log.d("CurrencyTR", "end");
                    break;
                case R.id.navigation_settings:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            settings, "SettingsFragment").addToBackStack(null).commit();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        window.setStatusBarColor(status_color);
                    break;
            }
            return true;
        });
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
        if (tabId == R.id.navigation_currency)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    currency_converter, "CurrencyConverterFragment").commit();
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
        if (navigation.getSelectedItemId() == R.id.navigation_calc && (
                calculator.panel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
        calculator.panel.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED))
            calculator.panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        else {
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

}