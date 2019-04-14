package com.slingHealth.reCentr.activities

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.slingHealth.reCentr.AdafruitBluefruitLeAttributes
import com.slingHealth.reCentr.BluetoothLeService
import com.slingHealth.reCentr.R
import com.slingHealth.reCentr.models.DayStatistic
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var statReference: DatabaseReference
    private lateinit var today: DayStatistic
    private var mDataField: TextView? = null
    private var mDegreeField: TextView? = null
    private var mDeviceName: String? = null
    private var mDeviceAddress: String? = null
    private var mBluetoothLeService: BluetoothLeService? = null
    private var mConnected = false
    private var mNotifyCharacteristic: BluetoothGattCharacteristic? = null
    private lateinit var stringDate: String
    private var count = 0

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val date = Date()
        val newDate = Date(date.time + 604800000L * 2 + 24 * 60 * 60)
        val dt = SimpleDateFormat("yyyy-MM-dd")

        stringDate = dt.format(newDate)

        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        statReference = FirebaseDatabase.getInstance().reference
            .child("statistics").child(auth.currentUser!!.uid).child(stringDate)

        newStat()
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)


        b_bluetooth.setOnClickListener {
            val intent = Intent(this, DeviceScanActivity::class.java)
            startActivity(intent)
        }
        b_stats.setOnClickListener {
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }

        b_pt.setOnClickListener {
            val intent = Intent(this, PTActivity::class.java)
            startActivity(intent)
        }


        val intent = intent
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME)
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS)


        mDataField = findViewById(R.id.tv_data)
        mDegreeField = findViewById(R.id.tv_degree)

        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)

    }

    // [START write_fan_out]
    private fun writeStatUpdate() {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        val key = stringDate


        val statValues = today.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/statistics/${auth.currentUser!!.uid}/$key"] = statValues

        database.updateChildren(childUpdates)
    }
    // [END write_fan_out]

    private fun newStat() {
        statReference.addListenerForSingleValueEvent(
            object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val currentStat = dataSnapshot.getValue(DayStatistic::class.java)

                    if (currentStat == null) {
                        today = DayStatistic()
                        statReference.setValue(today)
                    } else {
                        today = currentStat
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("", "getUser:onCancelled", databaseError.toException())
                }

            })

    }

    private fun checkData(data: Double) {
        if (data > today.maximum) {
            today.maximum = data
        }
        if (data > 8.0) {
            today.total++
            today.data[Calendar.getInstance().get(Calendar.HOUR)]++
        }
        writeStatUpdate()
    }


    // Code to manage Service lifecycle.
    private val mServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            mBluetoothLeService = (service as BluetoothLeService.LocalBinder).service
            if (!mBluetoothLeService!!.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth")
                finish()
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService!!.connect(mDeviceAddress)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBluetoothLeService = null
        }
    }


    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private val mGattUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when (action) {
                BluetoothLeService.ACTION_GATT_CONNECTED -> {
                    mConnected = true
                    b_bluetooth.setBackgroundColor(250)

                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    mConnected = false
                    clearUI()
                }
                BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED -> findUARTService(mBluetoothLeService!!.supportedGattServices)
                BluetoothLeService.ACTION_DATA_AVAILABLE -> displayData(
                    intent.getStringExtra(
                        BluetoothLeService.EXTRA_DATA
                    )
                )
            }
        }
    }

    private fun findUARTService(gattServices: List<BluetoothGattService>?) {
        var uartService: BluetoothGattService
        var characteristic: BluetoothGattCharacteristic
        if (gattServices == null) return
        for (gattService in gattServices) {

            val uuid = gattService.uuid.toString()
            val unknownServiceString = resources.getString(R.string.unknown_service)
            val name = AdafruitBluefruitLeAttributes.lookup(uuid, unknownServiceString)
            Log.i(TAG, name)

            if (name.equals("UART", ignoreCase = true)) {
                uartService = gattService
                characteristic = uartService.getCharacteristic(BluetoothLeService.RX_UUID)
                val charaProp = characteristic.properties
                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                    // If there is an active notification on a characteristic, clear
                    // it first so it doesn't update the data field on the user interface.
                    if (mNotifyCharacteristic != null) {
                        mBluetoothLeService!!.setCharacteristicNotification(
                            mNotifyCharacteristic!!, false
                        )
                        mNotifyCharacteristic = null
                    }
                    Log.i(TAG, "about to read char")
                    mBluetoothLeService!!.readCharacteristic(characteristic)

                    if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                        mNotifyCharacteristic = characteristic
                        mBluetoothLeService!!.setCharacteristicNotification(
                            characteristic, true
                        )
                    }
                    break
                }
            }

        }


    }


    private fun clearUI() {
        mDataField!!.text = "---"
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(
            mGattUpdateReceiver,
            makeGattUpdateIntentFilter()
        )
        if (mBluetoothLeService != null) {
            val result = mBluetoothLeService!!.connect(mDeviceAddress)
            Log.d(TAG, "Connect request result=$result")
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mGattUpdateReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mServiceConnection)
        mBluetoothLeService = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_out -> {
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    companion object {
        private val TAG = MainActivity::class.java.simpleName

        const val EXTRAS_DEVICE_NAME = "DEVICE_NAME"
        const val EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS"


        private fun makeGattUpdateIntentFilter(): IntentFilter {
            val intentFilter = IntentFilter()
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
            intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE)
            return intentFilter
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.b_about_us -> {
                displayDialog(R.layout.dialog_about_us)
            }
            R.id.b_privacy_policy -> {
                displayDialog(R.layout.dialog_privacy_policy)

            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun displayDialog(layout: Int) {
        val dialog = Dialog(this)
        dialog.setContentView(layout)

        val window = dialog.window
        window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

        dialog.findViewById<Button>(R.id.close).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    @SuppressLint("MissingPermission")
    private fun displayData(data: String?) {

        if (data != null && data.length >= 6) {
            if (data.contains("HELP")) {
                val number = "+19144348523"
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:$number")
                startActivity(intent)
            }
            if (count % 10 == 0) {
                checkData(data.toDouble())
            }
            count++


            Log.d(TAG, data.length.toString())
            pointerSpeedometer.speedTo(data.toFloat())
            mDataField!!.text = data
            mDegreeField!!.text = "°"
        }
    }


}
