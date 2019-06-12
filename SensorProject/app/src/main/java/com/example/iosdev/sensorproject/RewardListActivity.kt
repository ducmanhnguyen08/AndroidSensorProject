package com.example.iosdev.sensorproject

import android.content.Intent
import android.database.Cursor
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleCursorAdapter

class RewardListActivity : AppCompatActivity() {
    internal var mydb: DatabaseHelper
    internal var listView_rewards: ListView


    @Override
    protected fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward_list)
        mydb = DatabaseHelper(this)
        //viewAll();
        listView_rewards = findViewById(R.id.listView_rewards) as ListView
        populateRewardListView()
        setListViewOnItemListener(listView_rewards)
    }

    private fun populateRewardListView() {
        val cursor = mydb.getAllData()
        val fromFieldNames = arrayOf<String>(DatabaseHelper.COL_REWARD, DatabaseHelper.COL_GOAL, DatabaseHelper.COL_CURRENT_STEP)
        val toViewsIds = intArrayOf(R.id.textView_reward_description, R.id.textView_goal, R.id.textView_completion)
        val myCursorAdapter: SimpleCursorAdapter
        myCursorAdapter = SimpleCursorAdapter(getBaseContext(), R.layout.reward_single_row, cursor, fromFieldNames, toViewsIds, 0)
        listView_rewards.setAdapter(myCursorAdapter)
    }

    private fun setListViewOnItemListener(listView: ListView) {
        listView.setOnItemClickListener(object : AdapterView.OnItemClickListener() {
            @Override
            fun onItemClick(listView: AdapterView<*>, view: View, position: Int, id: Long) {
                val cursor = listView.getItemAtPosition(position) as Cursor

                MainActivity.ID = String.valueOf(id)
                Log.d("RewardListActivity", "onItemClick: $id")
                // Create new intent
                val intent = Intent(this@RewardListActivity, MainActivity::class.java)
                intent.putExtra(ID_EXTRA, String.valueOf(id))
                System.out.println(id)
                startActivity(intent)


                /*StringBuffer buffer = new StringBuffer();

                    buffer.append("id: " + cursor.getString(0) + "\n");
                    buffer.append("re: " + cursor.getString(1) + "\n");
                    buffer.append("goal: " + cursor.getInt(2) + "\n");
                    buffer.append("com: " + cursor.getInt(3) + "\n\n");


                // Show all data
                showMessage("Data", buffer.toString());*/
                //viewAll();
            }
        })
    }

    fun viewAll() {
        val res = mydb.getAllData()
        if (res.getCount() === 0) {
            // Show message
            showMessage("Error", "No data found")
            return
        }

        val buffer = StringBuffer()
        while (res.moveToNext()) {
            buffer.append("id: " + res.getString(0) + "\n")
            buffer.append("re: " + res.getString(1) + "\n")
            buffer.append("goal: " + res.getInt(2) + "\n")
            buffer.append("com: " + res.getInt(3) + "\n\n")
        }

        // Show all data
        showMessage("Data", buffer.toString())
    }

    fun showMessage(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.show()
    }

    companion object {

        val ID_EXTRA = "_id"
    }


}
