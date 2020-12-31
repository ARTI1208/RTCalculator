package ru.art2000.calculator.view.unit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Locale;

import ru.art2000.calculator.R;
import ru.art2000.calculator.databinding.UnitLayoutBinding;
import ru.art2000.extensions.IReplaceableFragment;
import ru.art2000.extensions.NavigationFragment;
import ru.art2000.helpers.AndroidHelper;

public class UnitConverterFragment extends NavigationFragment {

    private ViewPager2.OnPageChangeCallback pageChangeCallback2 = null;
    private TabLayoutMediator pager2Mediator = null;

    private UnitLayoutBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (binding == null) {

            binding = UnitLayoutBinding.inflate(inflater);
            updateAdapter();
        }

        return binding.getRoot();
    }

    public void updateAdapter() {

        UnitPager2AdapterNew pager2Adapter = new UnitPager2AdapterNew();
        binding.pager2.setAdapter(pager2Adapter);

        binding.pager2.unregisterOnPageChangeCallback(pageChangeCallback2);
        pageChangeCallback2 = new ViewPager2.OnPageChangeCallback() {

            private boolean isFirstRun = true;

            @Override
            public void onPageSelected(int position) {
                if (!isFirstRun) {
                    pager2Adapter.fragments[position].onReplace(null);
                }

                isFirstRun = false;
            }
        };
        binding.pager2.registerOnPageChangeCallback(pageChangeCallback2);

        if (pager2Mediator != null) {
            pager2Mediator.detach();
        }

        pager2Mediator = new TabLayoutMediator(binding.tabs, binding.pager2, (tab, position) ->
                tab.setText(pager2Adapter.getPageTitle(position)));

        pager2Mediator.attach();
    }

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public int getTitle() {
        return R.string.title_unit;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_dashboard_black_24dp;
    }

    @Override
    public int getReplaceableId() {
        return R.id.navigation_unit;
    }

    @Override
    public void onShown(@Nullable IReplaceableFragment previousReplaceable) {
        UnitPager2AdapterNew adapter = (UnitPager2AdapterNew) binding.pager2.getAdapter();
        adapter.fragments[binding.pager2.getCurrentItem()].onShown(null);
    }

    private class UnitPager2AdapterNew extends FragmentStateAdapter {

        private final String[] categoriesNames;
        private final BaseUnitPageFragment<?>[] fragments;

        UnitPager2AdapterNew() {
            super(UnitConverterFragment.this);
            categoriesNames = getResources().getStringArray(R.array.unit_converter_categories);
            String[] categoriesEnglish =
                    AndroidHelper.getLocalizedResources(requireContext(), Locale.ENGLISH)
                            .getStringArray(R.array.unit_converter_categories);
            fragments = new BaseUnitPageFragment<?>[categoriesNames.length];
            for (int i = 0; i < categoriesNames.length; ++i) {
                fragments[i] = BaseUnitPageFragment.newInstance(categoriesEnglish[i].toLowerCase());
            }
        }

        @NonNull
        CharSequence getPageTitle(int position) {
            return categoriesNames[position];
        }

        @NonNull
        @Override
        public BaseUnitPageFragment<?> createFragment(int position) {
            return fragments[position];
        }

        @Override
        public int getItemCount() {
            return categoriesNames.length;
        }
    }
}
