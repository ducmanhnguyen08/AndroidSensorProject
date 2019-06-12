package com.example.iosdev.sensorproject


import android.app.Activity
import android.app.DialogFragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.mbientlab.metawear.MetaWearBoard
import com.mbientlab.metawear.UnsupportedModuleException
import com.mbientlab.metawear.module.Led


/**
 * A simple [Fragment] subclass.
 */
class MWDeviceConfirmFragment : DialogFragment() {

    private var ledModule: Led? = null
    private var yesButton: Button? = null
    private var noButton: Button? = null
    private var callback: DeviceConfirmCallback? = null

    interface DeviceConfirmCallback {
        fun pairDevice()

        fun dontPairDevice()
    }

    fun flashDeviceLight(mwBoard: MetaWearBoard, fragmentManager: FragmentManager) {
        try {
            ledModule = mwBoard.getModule(Led::class.java)
        } catch (e: UnsupportedModuleException) {
            Log.e("Led Fragment", e.toString())
        }

        ledModule!!.configureColorChannel(Led.ColorChannel.BLUE)
                .setRiseTime(750.toShort()).setPulseDuration(2000.toShort())
                .setRepeatCount((-1).toByte()).setHighTime(500.toShort())
                .setFallTime(750.toShort()).setLowIntensity(0.toByte())
                .setHighIntensity(31.toByte()).commit()

        ledModule!!.play(true)

        show(fragmentManager, "device_confirm_callback")
    }

    @Override
    fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View {
        return inflater.inflate(R.layout.fragment_mwdevice_confirm, container)
    }

    @Override
    fun onAttach(activity: Activity) {
        if (activity !is DeviceConfirmCallback) {
            throw RuntimeException("Acitivty does not implement DeviceConfirmationCallback interface")
        }

        callback = activity
        super.onAttach(activity)
    }

    @Override
    fun onViewCreated(view: View, savedInstanceState: Bundle) {
        noButton = view.findViewById(R.id.confirm_no) as Button
        noButton!!.setOnClickListener(object : View.OnClickListener() {
            @Override
            fun onClick(v: View) {
                ledModule!!.stop(true)
                callback!!.dontPairDevice()
                dismiss()
            }
        })

        yesButton = view.findViewById(R.id.confirm_yes) as Button
        yesButton!!.setOnClickListener(object : View.OnClickListener() {
            @Override
            fun onClick(v: View) {
                ledModule!!.stop(true)
                callback!!.pairDevice()
                dismiss()
            }
        })

    }

}
