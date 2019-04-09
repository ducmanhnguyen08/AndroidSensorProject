package com.example.iosdev.sensorproject;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Created by iosdev on 26.9.2016.
 */

public class Discounts extends AppCompatActivity {

    private DatabaseHelper db;
    ListView listView_discount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discounts);

        listView_discount = (ListView) findViewById(R.id.listview_earned_rewards);
        db = new DatabaseHelper(this);
        populateListView();
    }

    private void populateListView() {
        Cursor cursor = db.getDataByIsCompleted();
        String[] fromFieldNames = new String[] {DatabaseHelper.COL_REWARD_CODE, DatabaseHelper.COL_REWARD};
        int[] toViewsIds = new int[] {R.id.textView_discount_code, R.id.textView_discount_description};
        SimpleCursorAdapter myCursorAdapter;
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(), R.layout.discount_single_row, cursor, fromFieldNames, toViewsIds, 0);
        listView_discount.setAdapter(myCursorAdapter);
    }

}