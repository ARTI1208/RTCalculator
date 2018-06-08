package ru.art2000.calculator;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class UnitClass extends FragmentPagerAdapter {
    private Context mContext;

    UnitClass(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new DistanceFragment();
        } else if (position == 1) {
            return new VelocityFragment();
        } else {
            return new DistanceFragment();
        }
    }

    // Количество вкладок
    @Override
    public int getCount() {
        return 2;
    }

    // Название вкладок конвертера
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.unit_category_distance);
            case 1:
                return mContext.getString(R.string.unit_category_velocity);
            default:
                return null;
        }
    }
}