package ru.art2000.calculator;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class UnitClass extends FragmentPagerAdapter {
    private Context mContext;

    public UnitClass(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new DistanceFragment();
        } else if (position == 1){
            return new VelocityFragment();
        }
//        else if (position == 2){
//            return new FoodFragment();
//        }
 else {
            return new DistanceFragment();
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return mContext.getString(R.string.unit_category_distance);
            case 1:
                return mContext.getString(R.string.unit_category_velocity);
//            case 2:
//                return mContext.getString(R.string.category_food);
//            case 3:
//                return mContext.getString(R.string.category_nature);
            default:
                return null;
        }
    }
}
