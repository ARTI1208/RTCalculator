package ru.art2000.calculator.currency_converter.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ru.art2000.calculator.MainActivity;
import ru.art2000.calculator.R;
import ru.art2000.calculator.currency_converter.model.LoadingState;
import ru.art2000.calculator.currency_converter.view_model.CurrencyConverterModel;
import ru.art2000.extensions.IReplaceable;
import ru.art2000.extensions.ReplaceableFragment;
import ru.art2000.helpers.AndroidHelper;
import ru.art2000.helpers.CurrencyValuesHelper;

public class CurrencyConverterFragment extends ReplaceableFragment {

    public Context mContext;
    public CurrencyListAdapter adapter = null;
    private TextView emptyView;
    private RecyclerView recycler;
    private MainActivity parent;
    private View v = null;

    private SwipeRefreshLayout refresher;
    private boolean isUpdating;
    private boolean didFirstUpdate;
    private Toolbar mToolbar;
    private String titleUpdatedString;


    private CurrencyConverterModel model;


    private void setRefreshStatus(boolean status) {
        if (refresher != null) {
            refresher.setRefreshing(status);
            isUpdating = status;
        }
    }

    public void scrollToTop() {
        if (recycler != null)
            recycler.smoothScrollToPosition(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 666) {
            if (resultCode == 1) {
//                adapter.getDataFromDB();
//                toggleEmptyView();
            }
        }
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (v == null) {
            mContext = getActivity();
            parent = (MainActivity) requireActivity();
            model = new CurrencyConverterModel(parent.getApplication());


            titleUpdatedString = mContext.getString(R.string.updated);

            v = inflater.inflate(R.layout.currency_layout, null);
            recycler = v.findViewById(R.id.currency_list);
            emptyView = v.findViewById(R.id.empty_tv);
            mToolbar = v.findViewById(R.id.toolbar);
            mToolbar.inflateMenu(R.menu.currencies_converter_menu);

            LinearLayoutManager llm = new LinearLayoutManager(mContext);
            recycler.setLayoutManager(llm);
            adapter = new CurrencyListAdapter(mContext);
            recycler.setAdapter(adapter);

            recycler.setOnFocusChangeListener((v, hasFocus) ->
                    adapter.removeEditText());

            int colorAccent = AndroidHelper.getColorAttribute(mContext, R.attr.colorAccent);

            refresher = v.findViewById(R.id.refresher);
            refresher.setColorSchemeColors(colorAccent);
            refresher.setProgressViewEndTarget(true, refresher.getProgressViewEndOffset());
            refresher.setOnRefreshListener(this::updateData);

            ActionMenuItemView editMenuItem = v.findViewById(R.id.edit_currencies);
            editMenuItem.getItemData().getIcon().setColorFilter(
                    new PorterDuffColorFilter(colorAccent, PorterDuff.Mode.SRC_ATOP));
            editMenuItem.setOnClickListener(v -> {
                adapter.removeEditText();
                Intent intent = new Intent(getActivity(), CurrenciesSettingsActivity.class);
                startActivityForResult(intent, 666);
            });
        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model.getLoadingState().observe(getViewLifecycleOwner(), loadingState -> {
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

            Toast.makeText(mContext, messageId, Toast.LENGTH_SHORT).show();
        });


        model.getUpdateDate().observe(getViewLifecycleOwner(), this::setCurrenciesUpdateDate);
        model.getVisibleList().observe(getViewLifecycleOwner(), currencyItems -> {

            adapter.setNewData(currencyItems);

            if (currencyItems.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                refresher.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.GONE);
                refresher.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setCurrenciesUpdateDate(String date) {
        mToolbar.setTitle(titleUpdatedString + " " + date);
    }

    @Override
    public void onPause() {
        adapter.removeEditText();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        setRefreshStatus(isUpdating);
    }

    private void updateData() {
        didFirstUpdate = true;
        new Thread(() -> model.loadData()).start();
    }

    @Override
    protected void onShown(@Nullable IReplaceable previousReplaceable) {
        if (!didFirstUpdate) {
            updateData();
        }

        parent.changeStatusBarColor(false);
    }

    @Override
    public int getOrder() {
        return 0;
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

}