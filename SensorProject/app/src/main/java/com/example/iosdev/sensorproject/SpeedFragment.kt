package com.example.iosdev.sensorproject

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView

import com.example.iosdev.sensorproject.R.id.speedtxt
import com.example.iosdev.sensorproject.R.id.tv
import com.example.iosdev.sensorproject.StepFragment.ARG_PAGE_NUMBER
import java.lang.Math


/*
*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SpeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SpeedFragment#newInstance} factory method to
 * create an instance of this fragment.
*/
class SpeedFragment : Fragment() {

    var speedtv: TextView
    private var lm: LocationManager? = null
    private var ll: LocationListener? = null
    internal var mySpeed: Double = 0.toDouble()
    internal var maxSpeed: Double = 0.toDouble()
    private val Speed: String? = null
    private var db: DatabaseHelper? = null

    /** Called when the activity is first created.  */
    @Override
    fun onCreate(savedInstanceState: Bundle) {

        super.onCreate(savedInstanceState)
        db = DatabaseHelper(getContext())
    }

    @Override
    fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                     savedInstanceState: Bundle): View {
        val rootView = inflater.inflate(R.layout.fragment_speed, container, false)
        super.onCreate(savedInstanceState)
        speedtv = rootView.findViewById(R.id.speedtxt) as TextView

        lm = getActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        ll = SpeedoActionListener()

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !== PackageManager.PERMISSION_GRANTED) {
            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST)
        } else if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) === PackageManager.PERMISSION_GRANTED) {
            lm!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll)
        }
        return rootView
    }

    private inner class SpeedoActionListener : LocationListener {


        @Override
        fun onLocationChanged(location: Location?) {
            if (location != null) {
                if (location!!.hasSpeed()) {
                    mySpeed = location!!.getSpeed()
                    mySpeed = Math.round(mySpeed * 100.0) / 100.0
                    if (mySpeed >= maxSpeed) {
                        maxSpeed = mySpeed
                    }
                    speedtv.setText("Current speed: $Double m/s, Max speed: $Double m/s")
                    db!!.updateSpeed(MainActivity.ID, maxSpeed)
                }
            }
        }

        @Override
        fun onProviderDisabled(provider: String) {
            // TODO Auto-generated method stub

        }

        @Override
        fun onProviderEnabled(provider: String) {
            // TODO Auto-generated method stub

        }

        @Override
        fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            // TODO Auto-generated method stub

        }
    }

    companion object {
        private val LOCATION_PERMS = arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION)

        private val LOCATION_REQUEST = 9001

        fun newInstance(page: Int): SpeedFragment {
            val fragment = SpeedFragment()
            val args = Bundle()
            args.putInt(ARG_PAGE_NUMBER, page)
            fragment.setArguments(args)
            return fragment
        }
    }
}// Required empty public constructor