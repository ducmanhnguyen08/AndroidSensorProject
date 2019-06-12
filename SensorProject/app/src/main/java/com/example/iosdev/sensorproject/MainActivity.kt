package com.example.iosdev.sensorproject

import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log


class MainActivity : AppCompatActivity() {
    private var _id: String? = null


    @Override
    protected fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get intent values
        _id = getIntent().getStringExtra(RewardListActivity.ID_EXTRA)
        Log.d("Intent on main activity", "onCreate: " + _id!!)
        if (MainActivity.ID == null) {
            MainActivity.ID = this._id
        }
        Log.d("MainActivity", "onCreate: " + MainActivity.ID!!)

        val tabs = findViewById(R.id.sliding_tabs) as TabLayout
        val pager = findViewById(R.id.viewpager) as ViewPager
        val adapter = ActivityPagerAdapter(getSupportFragmentManager())

        pager.setAdapter(adapter)
        tabs.setupWithViewPager(pager)


    }

    companion object {

        protected var ID: String? = "1"
    }

}
