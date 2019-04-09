package com.example.iosdev.sensorproject;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class RewardListActivity extends AppCompatActivity{

    public final static String ID_EXTRA = "_id";
    DatabaseHelper mydb;
    ListView listView_rewards;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_list);
        mydb = new DatabaseHelper(this);
        //viewAll();
        listView_rewards = (ListView) findViewById(R.id.listView_rewards);
        populateRewardListView();
        setListViewOnItemListener(listView_rewards);
    }

    private void populateRewardListView () {
        Cursor cursor = mydb.getAllData();
        String[] fromFieldNames = new String[] {DatabaseHelper.COL_REWARD, DatabaseHelper.COL_GOAL, DatabaseHelper.COL_CURRENT_STEP};
        int[] toViewsIds = new int[] {R.id.textView_reward_description, R.id.textView_goal, R.id.textView_completion};
        SimpleCursorAdapter myCursorAdapter;
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(), R.layout.reward_single_row, cursor, fromFieldNames, toViewsIds, 0);
        listView_rewards.setAdapter(myCursorAdapter);
    }

    private void setListViewOnItemListener (ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                MainActivity.ID = String.valueOf(id);
                Log.d("RewardListActivity", "onItemClick: " + id);
                // Create new intent
                Intent intent = new Intent(RewardListActivity.this, MainActivity.class);
                intent.putExtra(ID_EXTRA, String.valueOf(id));
                System.out.println(id);
                startActivity(intent);


                /*StringBuffer buffer = new StringBuffer();

                    buffer.append("id: " + cursor.getString(0) + "\n");
                    buffer.append("re: " + cursor.getString(1) + "\n");
                    buffer.append("goal: " + cursor.getInt(2) + "\n");
                    buffer.append("com: " + cursor.getInt(3) + "\n\n");


                // Show all data
                showMessage("Data", buffer.toString());*/
                //viewAll();
            }
        });
    }

    public void viewAll () {
        Cursor res = mydb.getAllData();
        if (res.getCount() == 0) {
            // Show message
            showMessage("Error", "No data found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("id: " + res.getString(0) + "\n");
            buffer.append("re: " + res.getString(1) + "\n");
            buffer.append("goal: " + res.getInt(2) + "\n");
            buffer.append("com: " + res.getInt(3) + "\n\n");
        }

        // Show all data
        showMessage("Data", buffer.toString());
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


}
