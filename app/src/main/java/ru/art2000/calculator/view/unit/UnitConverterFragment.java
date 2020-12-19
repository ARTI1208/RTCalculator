package ru.art2000.calculator.view.unit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Locale;

import ru.art2000.calculator.R;
import ru.art2000.extensions.NavigationFragment;
import ru.art2000.helpers.AndroidHelper;

public class UnitConverterFragment extends NavigationFragment {

    private Context mContext;
    private TabLayout tabLayout;
    private ViewPager pager;
    private View v;
    private FragmentManager fm;
    private ViewPager2 pager2;
    private final boolean useViewPager2 = true;

    private ViewPager.OnPageChangeListener pageChangeListener = null;
    private ViewPager2.OnPageChangeCallback pageChangeCallback2 = null;
    private TabLayoutMediator pager2Mediator = null;

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (v == null) {
            v = inflater.inflate(R.layout.unit_layout, null);
            pager = v.findViewById(R.id.pager);
            pager2 = v.findViewById(R.id.pager2);
            tabLayout = v.findViewById(R.id.tabs);
            mContext = getActivity();
            fm = getChildFragmentManager();
            if (useViewPager2) {
                pager.setVisibility(View.GONE);
                setNewAdapter2();
            } else {
                pager2.setVisibility(View.GONE);
                tabLayout.setupWithViewPager(pager);
                setNewAdapter();
            }
        }

        return v;
    }

    public void regenerateAdapter() {
        if (useViewPager2) {
            setNewAdapter2();
        } else {
            setNewAdapter();
        }
    }

    private void setNewAdapter2() {
        UnitPager2Adapter pager2Adapter = new UnitPager2Adapter();
        pager2.unregisterOnPageChangeCallback(pageChangeCallback2);

        boolean isFirstTimeAdapter = pageChangeCallback2 == null;

        pageChangeCallback2 = new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < pager2Adapter.fragments.length; i++) {
                    pager2Adapter.fragments[i].isCurrentPage = i == position;
                }
                if (!isFirstTimeAdapter && pager2Adapter.fragments[position].adapter != null) {
                    pager2Adapter.fragments[position].adapter.requestFocusForCurrent();
                }
            }
        };

        pager2.registerOnPageChangeCallback(pageChangeCallback2);

        pager2.setAdapter(pager2Adapter);

        if (pager2Mediator != null) {
            pager2Mediator.detach();
        }

        pager2Mediator = new TabLayoutMediator(tabLayout, pager2, (tab, position) ->
                tab.setText(pager2Adapter.getPageTitle(position)));

        pager2Mediator.attach();
    }

    private void setNewAdapter() {
        UnitPagerAdapter pagerAdapter = new UnitPagerAdapter();
        pager.removeOnPageChangeListener(pageChangeListener);
        pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < pagerAdapter.fragments.length; i++) {
                    pagerAdapter.fragments[i].isCurrentPage = i == position;
                }
                if (pagerAdapter.fragments[position].adapter != null) {
                    pagerAdapter.fragments[position].adapter.requestFocusForCurrent();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        pager.addOnPageChangeListener(pageChangeListener);

        pager.setAdapter(pagerAdapter);
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

    class UnitPager2Adapter extends FragmentStateAdapter {

        String[] categoriesNames;
        UnitPageFragment[] fragments;

        UnitPager2Adapter() {
            super(UnitConverterFragment.this);
            categoriesNames = getResources().getStringArray(R.array.unit_converter_categories);
            String[] categoriesEnglish =
                    AndroidHelper.getLocalizedResources(mContext, Locale.ENGLISH)
                            .getStringArray(R.array.unit_converter_categories);
            fragments = new UnitPageFragment[categoriesNames.length];
            for (int i = 0; i < categoriesNames.length; ++i) {
                fragments[i] = UnitPageFragment.newInstance(categoriesEnglish[i].toLowerCase());
            }
            fragments[0].isCurrentPage = true;
        }

        @Nullable
        CharSequence getPageTitle(int position) {
            return categoriesNames[position];
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments[position];
        }

        @Override
        public int getItemCount() {
            return categoriesNames.length;
        }
    }

    class UnitPagerAdapter extends FragmentStatePagerAdapter {

        String[] categoriesNames;
        UnitPageFragment[] fragments;

        UnitPagerAdapter() {
            super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            categoriesNames = getResources().getStringArray(R.array.unit_converter_categories);
            String[] categoriesEnglish =
                    AndroidHelper.getLocalizedResources(mContext, Locale.ENGLISH)
                            .getStringArray(R.array.unit_converter_categories);
            fragments = new UnitPageFragment[categoriesNames.length];
            for (int i = 0; i < categoriesNames.length; ++i) {
                fragments[i] = UnitPageFragment.newInstance(categoriesEnglish[i].toLowerCase());
            }
            fragments[0].isCurrentPage = true;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return categoriesNames[position];
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return categoriesNames.length;
        }
    }
}
