package com.example.iosdev.sensorproject

import android.database.Cursor
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import android.widget.SimpleCursorAdapter

/**
 * Created by iosdev on 26.9.2016.
 */

class Discounts : AppCompatActivity() {

    private var db: DatabaseHelper? = null
    internal var listView_discount: ListView


    @Override
    protected fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.discounts)

        listView_discount = findViewById(R.id.listview_earned_rewards) as ListView
        db = DatabaseHelper(this)
        populateListView()
    }

    private fun populateListView() {
        val cursor = db!!.getDataByIsCompleted()
        val fromFieldNames = arrayOf<String>(DatabaseHelper.COL_REWARD_CODE, DatabaseHelper.COL_REWARD)
        val toViewsIds = intArrayOf(R.id.textView_discount_code, R.id.textView_discount_description)
        val myCursorAdapter: SimpleCursorAdapter
        myCursorAdapter = SimpleCursorAdapter(getBaseContext(), R.layout.discount_single_row, cursor, fromFieldNames, toViewsIds, 0)
        listView_discount.setAdapter(myCursorAdapter)
    }

}