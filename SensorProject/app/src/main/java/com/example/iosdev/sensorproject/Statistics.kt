package com.example.iosdev.sensorproject

import android.bluetooth.BluetoothDevice
import android.content.SharedPreferences
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu

import android.app.Fragment
import android.app.FragmentTransaction
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.mbientlab.bletoolbox.scanner.BleScannerFragment
import com.mbientlab.metawear.MetaWearBleService
import com.mbientlab.metawear.MetaWearBoard
import com.mbientlab.metawear.module.Debug

import java.util.UUID


/**
 * Created by iosdev on 27.9.2016.
 */

class Statistics : AppCompatActivity(), ServiceConnection, BleScannerFragment.ScannerCommunicationBus, MWDeviceConfirmFragment.DeviceConfirmCallback {

    private var mwBinder: MetaWearBleService.LocalBinder? = null
    private var bluetoothDevice: BluetoothDevice? = null
    private var btDeviceSelected: Boolean = false
    private var mwBoard: MetaWearBoard? = null
    private var mwScannerFragment: MWScannerFragment? = null
    private var menu: Menu? = null
    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var heartRateSensorFragment: HeartRateSensorFragment? = null
    private var reconnecting = false
    private val HEART_RATE_SENSOR_FRAGMENT_KEY = "heart_rate_sensor_key"
    private val MAC_ADDRESS = "MAC_ADDRESS"
    var step: TextView
    var distance: TextView
    var speed: TextView
    internal var conversion = 0.762
    internal var d: Double = 0.toDouble()
    private val maxSpeed: Int = 0
    private var db: DatabaseHelper? = null

    ///< Only return MetaWear boards in the scan
    val filterServiceUuids: Array<UUID>
        @Override
        get() = arrayOf<UUID>(UUID.fromString("326a9000-85cb-9195-d9dd-464cfbbae75a"))

    ///< Scan for 10000ms (10 seconds)
    val scanDuration: Long
        @Override
        get() = 10000

    private val connectionStateHandler = object : MetaWearBoard.ConnectionStateHandler() {
        @Override
        fun connected() {
            Log.i("Metawear Controller", "Device Connected")
            runOnUiThread(object : Runnable() {
                @Override
                fun run() {
                    Toast.makeText(getApplicationContext(), R.string.toast_connected, Toast.LENGTH_SHORT).show()
                }
            }
            )
            if (btDeviceSelected) {
                val mwDeviceConfirmFragment = MWDeviceConfirmFragment()
                mwDeviceConfirmFragment.flashDeviceLight(mwBoard, getFragmentManager())
                btDeviceSelected = false
            } else if (reconnecting) {
                runOnUiThread(object : Runnable() {
                    @Override
                    fun run() {
                        setStatusToConnected()
                    }
                }
                )
                heartRateSensorFragment!!.startSensor(mwBoard)
                reconnecting = false
            }

        }

        @Override
        fun disconnected() {
            Log.i("Metawear Controler", "Device Disconnected")
            if (reconnecting) {
                mwBoard!!.connect()
            }
            runOnUiThread(object : Runnable() {
                @Override
                fun run() {
                    Toast.makeText(getApplicationContext(), R.string.toast_disconnected, Toast.LENGTH_SHORT).show()
                }
            })

        }

        @Override
        fun failure(status: Int, error: Throwable) {
            if (mwBoard != null) {
                mwBoard!!.connect()
            }
            Log.i("Failure", "Connection Failed")
        }

    }


    @Override
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.testheartrate)
        db = DatabaseHelper(getApplicationContext())
        val cursor = db!!.getDataById(MainActivity.ID)
        cursor.moveToFirst()
        //Getting the intent extras and step values from stepfragment
        val intent = getIntent()
        val stepvalue = intent.getIntExtra("cSteps", 0)
        val speedvalue = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COL_SPEED))

        step = findViewById(R.id.steps) as TextView
        distance = findViewById(R.id.distance) as TextView
        speed = findViewById(R.id.speed) as TextView

        step.setText(Integer.toString(stepvalue))
        speed.setText(Double.toString(speedvalue))

        //Converting steps to meters
        d = stepvalue * conversion
        distance.setText(Double.toString(d))

        sharedPreferences = getApplicationContext().getSharedPreferences("com.mbientlab.heartRateMonitor", 0) // 0 - for private mode
        editor = sharedPreferences!!.edit()

        if (savedInstanceState == null) {
            heartRateSensorFragment = HeartRateSensorFragment()
            getFragmentManager().beginTransaction().add(heartRateSensorFragment, HEART_RATE_SENSOR_FRAGMENT_KEY).commit()
        } else {
            heartRateSensorFragment = getFragmentManager().getFragment(savedInstanceState, HEART_RATE_SENSOR_FRAGMENT_KEY) as HeartRateSensorFragment
        }

        val macAddress = sharedPreferences!!.getString(MAC_ADDRESS, "")

        if (!macAddress.equals("")) {

            val btManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothDevice = btManager.getAdapter().getRemoteDevice(macAddress)
            reconnecting = true
        }

        getApplicationContext().bindService(Intent(this, MetaWearBleService::class.java),
                this, Context.BIND_AUTO_CREATE)
        Log.i("OnCreate", "done with calls")
        Log.i("MacAddress", macAddress + " foo ")
    }

    @Override
    fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.menu_main, menu)
        this.menu = menu

        return true
    }

    @Override
    fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.getItemId()


        if (id == R.id.action_connect) {
            if (mwBoard != null) {
                disconnectAdapter()
            } else {
                if (mwScannerFragment != null) {
                    val metawearBlescannerPopup = getFragmentManager().findFragmentById(R.id.metawear_blescanner_popup_fragment)
                    if (metawearBlescannerPopup != null) {
                        val fragmentTransaction = getFragmentManager().beginTransaction()
                        fragmentTransaction.remove(metawearBlescannerPopup)
                        fragmentTransaction.commit()
                    }
                    mwScannerFragment!!.dismiss()
                }
                mwScannerFragment = MWScannerFragment()
                mwScannerFragment!!.show(getFragmentManager(), "metawear_scanner_fragment")
                return true
            }

        } else if (id == R.id.action_clear_log) {
            if (mwBoard != null) {
                reconnecting = true
                mwBoard!!.disconnect()
            }
        } else if (id == R.id.action_reset_device) {
            try {
                mwBoard!!.getModule(Debug::class.java).resetDevice()
                setStatusToDisconnected()
                mwBoard = null
            } catch (e: Exception) {
                runOnUiThread(
                        object : Runnable() {
                            @Override
                            fun run() {
                                disconnectAdapter()
                                Toast.makeText(getApplicationContext(), R.string.error_soft_reset, Toast.LENGTH_SHORT).show()
                            }
                        }
                )
            }

        }

        return super.onOptionsItemSelected(item)
    }

    @Override
    fun onServiceConnected(name: ComponentName, service: IBinder) {
        ///< Get a reference to the MetaWear service from the binder
        mwBinder = service as MetaWearBleService.LocalBinder
        if (reconnecting) {
            connectDevice(bluetoothDevice)
        }
    }

    ///< Don't need this callback method but we must implement it
    @Override
    fun onServiceDisconnected(name: ComponentName) {
        mwBoard!!.disconnect()
    }


    @Override
    fun onDeviceSelected(device: BluetoothDevice) {
        bluetoothDevice = device
        btDeviceSelected = true
        connectDevice(device)
        val metawearBlescannerPopup = getFragmentManager().findFragmentById(R.id.metawear_blescanner_popup_fragment)
        val fragmentTransaction = getFragmentManager().beginTransaction()
        fragmentTransaction.remove(metawearBlescannerPopup)
        fragmentTransaction.commit()
        mwScannerFragment!!.dismiss()
    }

    @Override
    protected fun onSaveInstanceState(state: Bundle) {
        if (mwBoard != null) {
            editor!!.putString(MAC_ADDRESS, bluetoothDevice!!.getAddress())
            editor!!.apply()
            editor!!.commit()
            mwBoard!!.disconnect()
        }
        super.onSaveInstanceState(state)
    }

    @Override
    protected fun onResume() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf<String>(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    0)
        } else if (mwBoard != null) {
            mwBoard!!.connect()
        }
        super.onResume()
    }


    fun pairDevice() {
        setStatusToConnected()
        heartRateSensorFragment!!.startSensor(mwBoard)
    }

    fun dontPairDevice() {
        mwBoard!!.disconnect()
        bluetoothDevice = null
        mwScannerFragment!!.show(getFragmentManager(), "metawear_scanner_fragment")
    }


    private fun connectDevice(device: BluetoothDevice?) {
        mwBoard = mwBinder!!.getMetaWearBoard(device)

        if (mwBoard != null) {
            mwBoard!!.setConnectionStateHandler(connectionStateHandler)
            mwBoard!!.connect()
        }
    }

    private fun disconnectAdapter() {
        mwBoard!!.disconnect()
        mwBoard = null
        setStatusToDisconnected()
    }


    fun setStatusToConnected() {
        val connectMenuItem = menu!!.findItem(R.id.action_connect)
        connectMenuItem.setTitle(R.string.disconnect)
        val connectionStatus = findViewById(R.id.connection_status) as TextView
        connectionStatus.setText(getText(R.string.metawear_connected))

    }

    private fun setStatusToDisconnected() {
        val connectMenuItem = menu!!.findItem(R.id.action_connect)
        connectMenuItem.setTitle(R.string.connect)
        val connectionStatus = findViewById(R.id.connection_status) as TextView
        connectionStatus.setText(getText(R.string.no_metawear_connected))
        editor!!.remove(MAC_ADDRESS)
        editor!!.apply()
        editor!!.commit()
        reconnecting = false
    }

}
