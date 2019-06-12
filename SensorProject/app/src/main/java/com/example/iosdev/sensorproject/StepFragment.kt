package com.example.iosdev.sensorproject

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

import android.content.ContentValues.TAG


/*
*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StepFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StepFragment#newInstance} factory method to
 * create an instance of this fragment.
*/
class StepFragment : Fragment(), SensorEventListener, View.OnClickListener {
    private var sm: SensorManager? = null
    private val stepCounter: Sensor? = null
    private var stepDetector: Sensor? = null
    internal var flop: Boolean = false
    internal var isOn: Boolean = false
    internal var stepsTaken = "Steps: "
    internal var mprogressBar: ProgressBar
    var txt: TextView
    var rdview: TextView
    var btnDiscounts: Button
    var btnStatistics: Button
    var btnRewards: Button

    private var currentSteps: Int = 0
    private val maxSpeed: Int = 0
    private var reward: String? = null
    private var goal: Int = 0
    private var isCompleted: Int = 0
    private var db: DatabaseHelper? = null
    private val _id = MainActivity.ID


    private var mListener: OnFragmentInteractionListener? = null

    @Override
    fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)

        db = DatabaseHelper(getContext())
        val cursor = db!!.getDataById(MainActivity.ID)

        val buffer = StringBuffer()

        if (cursor.moveToFirst()) {
            currentSteps = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_CURRENT_STEP))
            goal = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_GOAL))
            isCompleted = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_IS_COMPLETED))
            reward = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_REWARD))

            buffer.append("id: " + cursor.getString(0) + "\n")
            buffer.append("reward: " + cursor.getString(1) + "\n")
            buffer.append("goal: $goal\n")
            buffer.append("current_step: $currentSteps\n")
            buffer.append("speed: $maxSpeed\n")
            buffer.append("CODE: " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_REWARD_CODE)) + "\n")
            buffer.append("isCompleted: $isCompleted\n\n")

            cursor.close()
        } else {
            //db.showMessage("Error: ", "No data", getContext());
        }

        //db.showMessage("This is for debug only: ", buffer.toString(), getContext());

    }


    @Override
    fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                     savedInstanceState: Bundle): View {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_step, container, false)
        flop = false
        isOn = false
        //currentSteps = 0;
        //startingSteps = 0;


        txt = rootView.findViewById(R.id.tv) as TextView
        rdview = rootView.findViewById(R.id.rewardview) as TextView
        sm = this.getActivity().getSystemService(Activity.SENSOR_SERVICE) as SensorManager
        //stepCounter = sm.getSensorList(Sensor.TYPE_STEP_COUNTER).get(0);
        stepDetector = sm!!.getSensorList(Sensor.TYPE_STEP_DETECTOR).get(0)

        btnDiscounts = rootView.findViewById(R.id.btnDiscounts) as Button
        btnStatistics = rootView.findViewById(R.id.btnStatistics) as Button
        btnRewards = rootView.findViewById(R.id.btnRewards) as Button

        rdview.setText(reward)
        txt.setText(stepsTaken + currentSteps)
        btnDiscounts.setOnClickListener(this)
        btnStatistics.setOnClickListener(this)
        btnRewards.setOnClickListener(this)


        mprogressBar = rootView.findViewById(R.id.circular_progress_bar) as ProgressBar
        val anim = ObjectAnimator.ofInt(mprogressBar, "progress", 0, goal)
        //mprogressBar.setMax(goal);
        anim.setDuration(150)
        anim.setInterpolator(DecelerateInterpolator())
        anim.start()

        return rootView
    }


    @Override
    fun onAttach(context: Context) {
        super.onAttach(context)
    }

    @Override
    fun onDetach() {
        super.onDetach()
        mListener = null
    }

    @Override
    fun onResume() {
        super.onResume()
        //sm.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_FASTEST);
        sm!!.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_FASTEST)
    }

    @Override
    fun onPause() {
        super.onPause()
        //sm.unregisterListener(this);
    }

    @Override
    fun onStop() {
        super.onStop()
    }

    @Override
    fun onDestroy() {
        super.onDestroy()
    }

    @Override
    fun onStart() {
        super.onStart()
        mprogressBar.setMax(goal)
    }

    @Override
    fun onSensorChanged(event: SensorEvent) {

        if (currentSteps < goal) {
            currentSteps += event.values.length
            txt.setText(stepsTaken + Integer.toString(currentSteps) + " / " + mprogressBar.getMax())
            mprogressBar.setProgress(currentSteps)
            // Update database
            db!!.updateCurrentStep(MainActivity.ID, currentSteps)
            Log.d(TAG, "onSensorChanged: IF " + currentSteps + " / " + goal + "  " + MainActivity.ID)

        } else if (currentSteps >= goal) {

            isCompleted = 1
            db!!.updateIsCompleted(MainActivity.ID, isCompleted)

            Log.d(TAG, "onSensorChanged: ELSE " + currentSteps + " / " + goal + "  " + MainActivity.ID)
        }
    }

    @Override
    fun onAccuracyChanged(sensor: Sensor, i: Int) {
    }

    @Override
    fun onClick(v: View) {
        val id = v.getId()
        if (id == R.id.btnDiscounts) {
            val discIntent = Intent(this@StepFragment.getActivity(), Discounts::class.java)
            startActivity(discIntent)
        } else if (id == R.id.btnStatistics) {
            val statIntent = Intent(this@StepFragment.getActivity(), Statistics::class.java)
            statIntent.putExtra("cSteps", currentSteps)
            statIntent.putExtra("maxSpeed", maxSpeed)
            startActivity(statIntent)
        } else if (id == R.id.btnRewards) {
            val rewardsIntent = Intent(this@StepFragment.getActivity(), RewardListActivity::class.java)
            startActivity(rewardsIntent)
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        internal var ARG_PAGE_NUMBER = "page_number"

        fun newInstance(page: Int): StepFragment {
            val fragment = StepFragment()
            val args = Bundle()
            args.putInt(ARG_PAGE_NUMBER, page)
            fragment.setArguments(args)
            return fragment
        }
    }

}// Required empty public constructor
