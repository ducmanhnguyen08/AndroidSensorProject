package com.example.iosdev.sensorproject;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    protected static String ID = "1";
    private String _id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get intent values
        _id = getIntent().getStringExtra(RewardListActivity.ID_EXTRA);
        Log.d("Intent on main activity", "onCreate: " + _id);
        if (MainActivity.ID == null) {
            MainActivity.ID = this._id;
        }
        Log.d("MainActivity", "onCreate: " +  MainActivity.ID);

        TabLayout tabs = (TabLayout) findViewById(R.id.sliding_tabs);
        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        ActivityPagerAdapter adapter = new ActivityPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);


    }

}
