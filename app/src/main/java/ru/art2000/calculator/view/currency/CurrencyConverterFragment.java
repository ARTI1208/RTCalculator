package ru.art2000.calculator.view.currency;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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

import ru.art2000.calculator.R;
import ru.art2000.calculator.databinding.CurrencyLayoutBinding;
import ru.art2000.calculator.model.currency.LoadingState;
import ru.art2000.calculator.view_model.currency.CurrencyConverterModel;
import ru.art2000.extensions.fragments.IReplaceableFragment;
import ru.art2000.extensions.fragments.NavigationFragment;
import ru.art2000.extensions.views.ViewsKt;
import ru.art2000.helpers.AndroidHelper;

public class CurrencyConverterFragment extends NavigationFragment {

    private CurrencyConverterModel model;
    private CurrencyLayoutBinding binding;
    private CurrencyListAdapter adapter;

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

            int colorAccent = AndroidHelper.getColorAttribute(requireContext(), R.attr.colorSecondary);
            int circleBackground = AndroidHelper.getColorAttribute(requireContext(), R.attr.floatingViewBackground);

            binding.refresher.setProgressBackgroundColorSchemeColor(circleBackground);
            binding.refresher.setColorSchemeColors(colorAccent);
            binding.refresher.setProgressViewEndTarget(true,
                    binding.refresher.getProgressViewEndOffset());
            binding.refresher.setOnRefreshListener(model::loadData);

            ActionMenuItemView editMenuItem = binding.getRoot().findViewById(R.id.edit_currencies);
            editMenuItem.getItemData().getIcon().setColorFilter(
                    new PorterDuffColorFilter(colorAccent, PorterDuff.Mode.SRC_ATOP));
            editMenuItem.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), CurrenciesSettingsActivity.class);
                startActivity(intent);
            });
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
        adapter = null;
        binding = null;
        model = null;
    }

    @Override
    public void onReselected() {
        binding.currencyList.smoothScrollToPosition(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.removeEditText();
    }

    @Override
    public void onShown(@Nullable IReplaceableFragment previousReplaceable) {
        if (!model.isFirstUpdateDone()) {
            model.loadData();
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

    private void applyLoadingState(LoadingState loadingState) {
        setRefreshStatus(loadingState == LoadingState.LOADING_STARTED);

        if (loadingState == LoadingState.UNINITIALISED || loadingState == LoadingState.LOADING_ENDED)
            return;

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