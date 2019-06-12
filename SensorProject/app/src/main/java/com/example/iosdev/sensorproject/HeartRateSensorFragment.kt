package com.example.iosdev.sensorproject

import java.util.Arrays

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.mbientlab.metawear.AsyncOperation
import com.mbientlab.metawear.Message
import com.mbientlab.metawear.MetaWearBoard
import com.mbientlab.metawear.RouteManager
import com.mbientlab.metawear.UnsupportedModuleException
import com.mbientlab.metawear.module.Gpio
import com.mbientlab.metawear.module.Timer


import java.util.concurrent.CompletionService


class HeartRateSensorFragment : Fragment() {

    private var metaWearBoard: MetaWearBoard? = null
    private var gpio: Gpio? = null
    private val GPIO_PIN: Byte = 0
    private val HEART_RATE = "heart_rate"
    private val SCAN_INTERVAL = 50
    private var sampleCounter: Int = 0
    private var lastBeatTime: Int = 0
    private var threshold: Int = 0
    private var ibi: Int = 0
    private var trough: Int = 0
    private var peak: Int = 0
    private var pulse: Boolean = false
    private var secondBeat: Boolean = false
    private var rate: IntArray? = null
    private var firstBeat: Boolean = false
    private var bpm: Int = 0
    private var amp: Int = 0
    private var bpmView: TextView? = null

    private val dataHandler = object : RouteManager.MessageHandler() {
        @Override
        fun process(message: Message) {
            val rawValue = message.getData(Short::class.java)
            Log.i("HeartRateSensorFragment", String.valueOf(rawValue))


            val signal = (rawValue * 0.512).toInt()
            Log.i("HRSFagment signal ", String.valueOf(signal))

            // We take a reading every 0.05 seconds
            sampleCounter += 50                        // keep track of the time in mS with this variable
            val timeInterval = sampleCounter - lastBeatTime       // monitor the time since the last beat to avoid noise

            Log.i("HRSFragment timeInt ", String.valueOf(timeInterval))

            // Find the peak and trough of the pulse wave
            if (signal < threshold && timeInterval > ibi / 5 * 3) {       // avoid dichrotic noise by waiting 3/5 of last IBI
                if (signal < trough) {                        // T is the trough
                    trough = signal                         // keep track of lowest point in pulse wave
                    Log.i("HRSFragment trough", String.valueOf(trough))
                }
            }

            if (signal > threshold && signal > peak) {          // thresh condition helps avoid noise
                peak = signal                             // P is the peak
                Log.i("HRSFragment Peaks", String.valueOf(peak))
            }                                           // keep track of highest point in pulse wave

            // Look for the heart beat
            // Signal surges up in value every time there is a pulse
            if (timeInterval > 250) {                               // avoid high frequency noise
                if (signal > threshold && pulse == false && timeInterval > ibi / 5 * 3) {
                    pulse = true                       // set the Pulse flag when we think there is a pulse
                    ibi = sampleCounter - lastBeatTime // measure time between beats in mS
                    Log.i("HRSFragment The IBI is ", String.valueOf(ibi))
                    lastBeatTime = sampleCounter       // keep track of time for next pulse

                    if (secondBeat) {                     // if this is the second beat, if secondBeat == TRUE
                        Log.i("HSRFragment ", "Second beat")
                        secondBeat = false             // clear secondBeat flag
                        for (i in 0..9) {        // seed the running total to get a realisitic BPM at startup
                            rate[i] = ibi
                        }
                    }

                    if (firstBeat) {                      // if it's the first time we found a beat, if firstBeat == TRUE
                        Log.i("HSRFragment", "First beat")
                        firstBeat = false              // clear firstBeat flag
                        secondBeat = true              // set the second beat flag
                        return                          // IBI value is unreliable so discard it
                    }

                    // Keep a running total of the last 10 IBI values
                    var runningTotal = 0               // clear the runningTotal variable

                    for (i in 0..8) {            // shift data in the rate array
                        rate[i] = rate!![i + 1]
                        runningTotal += rate!![i]
                        Log.i("HRSFragment", " Count " + String.valueOf(i) + " from added " + String.valueOf(rate!![i]))
                        //runningTotal += rate[i];        // add up the 9 oldest IBI values
                    }

                    rate[9] = ibi

                    runningTotal += rate!![9]
                    Log.i("HRSFragment RunningTot", String.valueOf(runningTotal))
                    runningTotal /= 10                 // average the last 10 IBI values
                    Log.i("HRSFragment RunTot Avg", String.valueOf(runningTotal))
                    bpm = 60000 / runningTotal           // get the beats per minutes -> BPM
                    Log.i("HRSFragment BMP is ", String.valueOf(bpm))
                }
            }

            if (signal < threshold && pulse == true) {      // when the values are going down, the beat is over
                pulse = false                          // reset the Pulse flag so we can do it again
                amp = peak - trough                            // get amplitude of the pulse wave
                threshold = amp / 2 + trough                     // set thresh at 50% of the amplitude
                peak = threshold                             // reset these for next time
                trough = threshold
            }

            if (timeInterval > 2500) {                              // if 2.5 seconds go by without a beat -> reset
                threshold = 250                           // set thresh default
                peak = 250                                // set P default
                trough = 250                                // set T default
                lastBeatTime = sampleCounter           // bring the lastBeatTime up to date
                firstBeat = true                       // set these to avoid noise
                secondBeat = false                     // when we get the heartbeat backf
            }

            getActivity().runOnUiThread(
                    object : Runnable() {
                        @Override
                        fun run() {
                            bpmView!!.setText(String.valueOf(bpm))
                        }
                    }
            )
            Log.i("HRSFragment BPM", String.valueOf(bpm))

        }
    }

    private val gpioHandler = object : AsyncOperation.CompletionHandler<RouteManager>() {
        @Override
        fun success(result: RouteManager) {
            result.subscribe(HEART_RATE, dataHandler)
            try {
                gpio!!.clearDigitalOut(1.toByte())
                val taskResult = metaWearBoard!!.getModule(Timer::class.java).scheduleTask(
                        object : Timer.Task() {
                            @Override
                            fun commands() {
                                gpio!!.readAnalogIn(GPIO_PIN, Gpio.AnalogReadMode.ADC)
                            }
                        }, SCAN_INTERVAL, false)
                taskResult.onComplete(
                        object : AsyncOperation.CompletionHandler<Timer.Controller>() {
                            @Override
                            fun success(result: Timer.Controller) {
                                result.start()
                            }
                        }
                )
            } catch (e: UnsupportedModuleException) {
                Log.e("HeartRateSensorFragment", e.toString())
            }

        }

    }


    @Override
    fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                     savedInstanceState: Bundle): View? {
        bpmView = getActivity().findViewById(R.id.bpm) as TextView
        return null
    }

    private fun initializeCounters() {
        sampleCounter = 0
        lastBeatTime = 0
        threshold = 256
        ibi = 1
        trough = 256
        peak = 256
        pulse = false
        secondBeat = false
        rate = IntArray(10)
        Arrays.fill(rate, 0)
        firstBeat = true
        bpm = 60
        amp = 100
    }

    fun startSensor(metaWearBoard: MetaWearBoard): Boolean {
        this.metaWearBoard = metaWearBoard
        initializeCounters()

        try {
            gpio = metaWearBoard.getModule(Gpio::class.java)

        } catch (e: UnsupportedModuleException) {
            Log.e("HeartRateSensorFragment", e.toString())
            return false
        }

        gpio!!.routeData().fromAnalogIn(GPIO_PIN, Gpio.AnalogReadMode.ADC).stream(HEART_RATE)
                .commit().onComplete(gpioHandler)
        return true
    }

}// Required empty public constructor
