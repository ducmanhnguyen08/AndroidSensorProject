package com.example.iosdev.sensorproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import static com.example.iosdev.sensorproject.R.id.speedtxt;
import static com.example.iosdev.sensorproject.R.id.tv;
import static com.example.iosdev.sensorproject.StepFragment.ARG_PAGE_NUMBER;
import java.lang.Math;
import java.lang.Number;


/*
*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SpeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SpeedFragment#newInstance} factory method to
 * create an instance of this fragment.
*/
public class SpeedFragment extends Fragment {

    public TextView speedtv;
    private LocationManager lm;
    private LocationListener ll;
    double mySpeed, maxSpeed;
    private final String Speed = null;
    private DatabaseHelper db;
    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int LOCATION_REQUEST=9001;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_speed, container, false);
        super.onCreate(savedInstanceState);
        speedtv = (TextView) rootView.findViewById(R.id.speedtxt);

        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        ll = new SpeedoActionListener();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
        }else if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
        }
        return rootView;
    }
    public SpeedFragment() {
        // Required empty public constructor
    }

    public static SpeedFragment newInstance(int page) {
        SpeedFragment fragment = new SpeedFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, page);
        fragment.setArguments(args);
        return fragment;
    }

    private class SpeedoActionListener implements LocationListener
    {



        @Override
        public void onLocationChanged(Location location) {
            if(location!=null) {
                if(location.hasSpeed()){
                    mySpeed = location.getSpeed();
                    mySpeed = Math.round(mySpeed * 100d) / 100d;
                    if (mySpeed >= maxSpeed) {
                        maxSpeed = mySpeed;
                    }
                    speedtv.setText("Current speed: " + Double.toString(mySpeed) + " m/s, Max speed: " + Double.toString(maxSpeed) + " m/s");
                    db.updateSpeed(MainActivity.ID, (maxSpeed));
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle
                extras) {
            // TODO Auto-generated method stub

        }
    }
}