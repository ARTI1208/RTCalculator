package ru.art2000.calculator.unit_converter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.LifecycleObserver;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Locale;
import ru.art2000.calculator.Helper;
import ru.art2000.calculator.R;

public class UnitConverterFragment extends Fragment {

    private Context mContext;
    private TabLayout tabLayout;
    private ViewPager pager;
    private View v;

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (v == null){
            v = inflater.inflate(R.layout.unit_layout, null);
            pager = v.findViewById(R.id.pager);
            tabLayout = v.findViewById(R.id.tabs);
            mContext = getActivity();
            setNewAdapter();
        }
        Log.d("cliick", "2");
        return v;
    }

    private void setNewAdapter(){
        fm = getChildFragmentManager();
        UnitPagerAdapter pagerAdapter = new UnitPagerAdapter();
        pager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(pager);
    }

    FragmentManager fm;

    class UnitPagerAdapter extends FragmentPagerAdapter {

        String[] categoriesNames;
        Fragment[] fragments;


        UnitPagerAdapter() {
            super(fm);
            categoriesNames = getResources().getStringArray(R.array.unit_converter_categories);
            String[] categoriesEnglish = Helper.getLocalizedResources(mContext, Locale.ENGLISH).getStringArray(R.array.unit_converter_categories);
            fragments = new Fragment[categoriesNames.length];
            for (int i = 0; i < categoriesNames.length; ++i) {
                fragments[i] = UnitPageFragment.newInstance(categoriesEnglish[i].toLowerCase());
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            Log.d("ptitle", categoriesNames[position]);
            return categoriesNames[position];
        }

        private String makeFragmentName(int viewId, long id) {
            return "android:switcher:" + viewId + ":" + id;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            finishUpdate(container);
            Log.d("fuufi", fragments[position].getLifecycle().getCurrentState().toString());
            final long itemId = getItemId(position);
            String name = makeFragmentName(container.getId(), itemId);
            Fragment fragment = fm.findFragmentByTag(name);
            if (fragment != null) {
                Log.d("INSTYS", "fragment " + categoriesNames[position] + " isnt null");
//                mCurTransaction.attach(fragment);
            } else {
                Log.d("INSTYS", "fragment " + categoriesNames[position] + " IS NULL");
//                fragment = getItem(position);
//                if (DEBUG) Log.v(TAG, "Adding item #" + itemId + ": f=" + fragment);
//                mCurTransaction.add(container.getId(), fragment,
//                        makeFragmentName(container.getId(), itemId));
            }
            return super.instantiateItem(container, position);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("fuuuuuuuuuuuuuuuuu", "whaaaaaaaaaat");
//            return fragments[position];
            String[] categoriesEnglish = Helper.getLocalizedResources(mContext, Locale.ENGLISH).getStringArray(R.array.unit_converter_categories);
            return UnitPageFragment.newInstance(categoriesEnglish[position].toLowerCase());
        }

        @Override
        public int getCount() {
            Log.d("sizeeeee", "yeeeeeeeeeep");
            return categoriesNames.length;
        }
    }


}
