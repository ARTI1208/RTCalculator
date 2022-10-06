package ru.art2000.calculator.view.currency;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.art2000.calculator.R;
import ru.art2000.calculator.databinding.CurrencyLayoutBinding;
import ru.art2000.calculator.model.currency.LoadingState;
import ru.art2000.calculator.view.MainScreenFragment;
import ru.art2000.calculator.view_model.currency.CurrencyConverterModel;
import ru.art2000.extensions.fragments.IReplaceableFragment;
import ru.art2000.extensions.views.ListenerSubscription;
import ru.art2000.extensions.views.ViewsKt;
import ru.art2000.helpers.AndroidHelper;
import ru.art2000.helpers.SnackbarThemeHelper;

public class CurrencyConverterFragment extends MainScreenFragment {

    private CurrencyConverterModel model;
    private CurrencyLayoutBinding binding;
    private CurrencyListAdapter adapter;
    private ListenerSubscription<Boolean> keyboardListenerSubscription;
    private Snackbar updateSnackbar;

    @SuppressLint({"InflateParams", "RestrictedApi"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (binding == null) {

            model = new ViewModelProvider(this,
                    new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
            ).get(CurrencyConverterModel.class);

            binding = CurrencyLayoutBinding.inflate(inflater);

            binding.toolbar.inflateMenu(R.menu.currencies_converter_menu);

            LinearLayoutManager llm = new LinearLayoutManager(requireContext());
            adapter = new CurrencyListAdapter(requireContext(), model);

            binding.currencyList.setLayoutManager(llm);
            binding.currencyList.setAdapter(adapter);

            binding.currencyList.setEmptyViewGenerator((context, viewGroup, integer) ->
                    ViewsKt.createTextEmptyView(context, R.string.empty_text_no_currencies_added));

            int colorAccent = AndroidHelper.getColorAttribute(requireContext(), com.google.android.material.R.attr.colorSecondary);
            int circleBackground = AndroidHelper.getColorAttribute(requireContext(), R.attr.floatingViewBackground);

            binding.refresher.setProgressBackgroundColorSchemeColor(circleBackground);
            binding.refresher.setColorSchemeColors(colorAccent);
            binding.refresher.setProgressViewEndTarget(true,
                    binding.refresher.getProgressViewEndOffset());
            binding.refresher.setOnRefreshListener(model::loadData);

            ActionMenuItemView editMenuItem = binding.getRoot().findViewById(R.id.edit_currencies);
            editMenuItem.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), CurrenciesSettingsActivity.class);
                startActivity(intent);
            });

            ActionMenuItemView selectDateMenuItem = binding.getRoot().findViewById(R.id.select_date);
            selectDateMenuItem.setOnClickListener(v -> {

                CalendarConstraints.DateValidator minValidator =
                        DateValidatorPointForward.from(model.getMinDateMillis());
                CalendarConstraints.DateValidator maxValidator =
                        DateValidatorPointBackward.before(model.getMaxDateMillis());

                List<CalendarConstraints.DateValidator> validators = new ArrayList<>();
                validators.add(minValidator);
                validators.add(maxValidator);

                MaterialDatePicker<Long> picker = MaterialDatePicker.Builder
                        .datePicker()
                        .setCalendarConstraints(
                                new CalendarConstraints.Builder()
                                        .setStart(model.getMinDateMillis())
                                        .setEnd(model.getMaxDateMillis())
                                        .setValidator(CompositeDateValidator.allOf(validators))
                                        .build()
                        )
                        .build();

                picker.addOnPositiveButtonClickListener(selection -> {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(selection);
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    model.loadData(year, month + 1, day);
                });

                picker.show(requireActivity().getSupportFragmentManager(), "taag");
            });
            selectDateMenuItem.setOnLongClickListener(v -> {
                model.loadData();
                return true;
            });

            model.getPreferences()
                    .registerOnSharedPreferenceChangeListener(model.getPreferenceListener());
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model.getLoadingState().observe(getViewLifecycleOwner(), this::applyLoadingState);
        model.getUpdateDate().observe(getViewLifecycleOwner(), this::setCurrenciesUpdateDate);
        model.getVisibleList().observe(getViewLifecycleOwner(), adapter::setNewData);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        model.getPreferences()
                .unregisterOnSharedPreferenceChangeListener(model.getPreferenceListener());
        adapter = null;
        binding = null;
        model = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        keyboardListenerSubscription = ViewsKt.addImeVisibilityListener(binding.getRoot(), isVisible -> {
            if (!isVisible) {
                adapter.removeEditText();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (keyboardListenerSubscription != null) {
            keyboardListenerSubscription.invoke(false);
            keyboardListenerSubscription = null;
        }
    }

    @Override
    public void onReselected() {
        if (binding != null) {
            binding.currencyList.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onShown(@Nullable IReplaceableFragment previousReplaceable) {
        if (!model.isFirstUpdateDone()) {
            if (model.isUpdateOnFirstTabOpenEnabled()) {
                model.loadData();
            } else {
                updateSnackbar = SnackbarThemeHelper.createThemedSnackbar(
                        binding.currencyList,
                        R.string.message_manually_update,
                        Snackbar.LENGTH_INDEFINITE
                );
                updateSnackbar.setAction(R.string.action_update, v -> model.loadData());
                updateSnackbar.show();
            }
        }
    }

    @Override
    public void onReplaced(@Nullable IReplaceableFragment nextReplaceable) {
        if (updateSnackbar != null) {
            updateSnackbar.dismiss();
            updateSnackbar = null;
        }
    }

    @Override
    public int getReplaceableId() {
        return R.id.navigation_currency;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_currency;
    }

    @Override
    public int getTitle() {
        return R.string.title_currency;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void applyLoadingState(LoadingState loadingState) {
        setRefreshStatus(loadingState == LoadingState.LOADING_STARTED);

        if (loadingState == LoadingState.UNINITIALISED)
            return;

        if (loadingState == LoadingState.LOADING_ENDED) {
            adapter.notifyDataSetChanged();
            return;
        }

        int messageId;
        switch (loadingState) {
            case LOADING_STARTED:
                messageId = R.string.currencies_update_toast;
                break;
            case NETWORK_ERROR:
                messageId = R.string.currencies_no_internet;
                break;
            default:
                messageId = R.string.currencies_update_failed;
                break;
        }

        Toast.makeText(requireContext(), messageId, Toast.LENGTH_SHORT).show();
    }

    private void setCurrenciesUpdateDate(String date) {
        binding.toolbar.setTitle(model.getTitleUpdatedString() + " " + date);
    }

    private void setRefreshStatus(boolean status) {
        if (status && binding.refresher.isRefreshing())
            return;

        binding.refresher.setRefreshing(status);
    }
}