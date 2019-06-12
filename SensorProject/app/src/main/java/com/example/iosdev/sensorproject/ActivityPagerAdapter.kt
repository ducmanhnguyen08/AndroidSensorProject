package com.example.iosdev.sensorproject

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by Antti on 27.9.2016.
 */

class ActivityPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    val count: Int
        @Override
        get() = 2

    @Override
    fun getItem(position: Int): Fragment {
        return if (position == 0) {
            StepFragment.newInstance(position + 1)
        } else {
            SpeedFragment.newInstance(position + 1)
        }
    }

    @Override
    fun getPageTitle(position: Int): CharSequence {
        return if (position == 0) {
            "Steps"
        } else {
            "Speed"
        }
    }
}
