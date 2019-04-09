package com.example.iosdev.sensorproject;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.content.ContentValues.TAG;


/*
*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StepFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StepFragment#newInstance} factory method to
 * create an instance of this fragment.
*/
public class StepFragment extends Fragment implements SensorEventListener, View.OnClickListener {
    private SensorManager sm;
    private Sensor stepCounter;
    private Sensor stepDetector;
    boolean flop, isOn;
    String stepsTaken = "Steps: ";
    static String ARG_PAGE_NUMBER = "page_number";
    ProgressBar mprogressBar;
    public TextView txt,rdview;
    public Button btnDiscounts, btnStatistics, btnRewards;

    private int currentSteps,maxSpeed;
    private String reward;
    private int goal;
    private int isCompleted;
    private DatabaseHelper db;
    private String _id = MainActivity.ID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHelper(getContext());
        Cursor cursor = db.getDataById(MainActivity.ID);

        StringBuffer buffer = new StringBuffer();

        if (cursor.moveToFirst()) {
            currentSteps = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_CURRENT_STEP));
            goal = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_GOAL));
            isCompleted = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_IS_COMPLETED));
            reward = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_REWARD));

            buffer.append("id: " + cursor.getString(0) + "\n");
            buffer.append("reward: " + cursor.getString(1) + "\n");
            buffer.append("goal: " + goal + "\n");
            buffer.append("current_step: " + currentSteps + "\n");
            buffer.append("speed: " + maxSpeed + "\n");
            buffer.append("CODE: " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_REWARD_CODE)) + "\n");
            buffer.append("isCompleted: " + isCompleted + "\n\n");

            cursor.close();
        } else {
            //db.showMessage("Error: ", "No data", getContext());
        }

        //db.showMessage("This is for debug only: ", buffer.toString(), getContext());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_step, container, false);
        flop = false;
        isOn = false;
        //currentSteps = 0;
        //startingSteps = 0;


        txt = (TextView) rootView.findViewById(R.id.tv);
        rdview = (TextView) rootView.findViewById(R.id.rewardview);
        sm = (SensorManager) this.getActivity().getSystemService(Activity.SENSOR_SERVICE);
        //stepCounter = sm.getSensorList(Sensor.TYPE_STEP_COUNTER).get(0);
        stepDetector = sm.getSensorList(Sensor.TYPE_STEP_DETECTOR).get(0);

        btnDiscounts = (Button) rootView.findViewById(R.id.btnDiscounts);
        btnStatistics = (Button) rootView.findViewById(R.id.btnStatistics);
        btnRewards = (Button) rootView.findViewById(R.id.btnRewards);

        rdview.setText(reward);
        txt.setText(stepsTaken + currentSteps);
        btnDiscounts.setOnClickListener(this);
        btnStatistics.setOnClickListener(this);
        btnRewards.setOnClickListener(this);


        mprogressBar = (ProgressBar) rootView.findViewById(R.id.circular_progress_bar);
        ObjectAnimator anim = ObjectAnimator.ofInt(mprogressBar, "progress", 0, goal);
        //mprogressBar.setMax(goal);
        anim.setDuration(150);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();

        return rootView;
    }


    private OnFragmentInteractionListener mListener;

    public StepFragment() {
        // Required empty public constructor
    }

    public static StepFragment newInstance(int page) {
        StepFragment fragment = new StepFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, page);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onResume() {
        super.onResume();
        //sm.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_FASTEST);
        sm.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onPause() {
        super.onPause();
        //sm.unregisterListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        mprogressBar.setMax(goal);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (currentSteps < goal) {
            currentSteps += event.values.length;
            txt.setText(stepsTaken + Integer.toString(currentSteps) + " / " + mprogressBar.getMax());
            mprogressBar.setProgress(currentSteps);
            // Update database
            db.updateCurrentStep(MainActivity.ID, currentSteps);
            Log.d(TAG, "onSensorChanged: IF " + currentSteps + " / " + goal + "  " + MainActivity.ID);

        } else if (currentSteps >= goal){

            isCompleted = 1;
            db.updateIsCompleted(MainActivity.ID, isCompleted);

            Log.d(TAG, "onSensorChanged: ELSE " + currentSteps + " / " + goal + "  " + MainActivity.ID);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
      if (id == R.id.btnDiscounts) {
            Intent discIntent = new Intent(StepFragment.this.getActivity(), Discounts.class);
            startActivity(discIntent);
        }else if (id == R.id.btnStatistics) {
            Intent statIntent = new Intent(StepFragment.this.getActivity(), Statistics.class);
            statIntent.putExtra("cSteps",currentSteps);
            statIntent.putExtra("maxSpeed",maxSpeed);
            startActivity(statIntent);
        } else if (id == R.id.btnRewards) {
            Intent rewardsIntent = new Intent(StepFragment.this.getActivity(), RewardListActivity.class);
            startActivity(rewardsIntent);
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
