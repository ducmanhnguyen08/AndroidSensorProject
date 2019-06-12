package com.example.iosdev.sensorproject


import android.app.DialogFragment
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * A simple [Fragment] subclass.
 */
class MWScannerFragment : DialogFragment() {


    @Override
    fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                     savedInstanceState: Bundle): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mwscanner, container, false)
    }

}// Required empty public constructor
