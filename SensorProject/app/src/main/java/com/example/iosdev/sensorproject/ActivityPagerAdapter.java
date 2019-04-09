package com.example.iosdev.sensorproject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Antti on 27.9.2016.
 */

public class ActivityPagerAdapter extends FragmentPagerAdapter{
    public ActivityPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return StepFragment.newInstance(position + 1);
        }else {
            return SpeedFragment.newInstance(position + 1);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Steps";
        }else{
            return "Speed";
        }
    }
}
