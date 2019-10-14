package de.whatevs.miccheck

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val REQUEST_ENABLE_BT = 1337
const val REQUEST_FINE_LOC = 1338


class MainActivity : AppCompatActivity() {
    private val handler: Handler = Handler(Looper.getMainLooper())


    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val BluetoothAdapter.isDisabled: Boolean
        get() = !isEnabled


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnShow = findViewById<Button>(R.id.findBtn)
        btnShow?.setOnClickListener {
            initBluetooth()
        }
    }

    fun initBluetooth() {
        bluetoothAdapter?.takeIf { it.isDisabled }?.apply {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            Log.d("MC", "init")
            return
        }
        findBluetooth()
    }

    fun findBluetooth() {
        Log.d("MC", "find")
        var permission = ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOC
            )
            return
        }

        var scanner = LeScanner(handler, this@MainActivity)
        scanner.scanLeDevice(true)
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
        if (requestCode == REQUEST_ENABLE_BT) {
            Log.d("MC", "inited")
            initBluetooth()
        } else if (requestCode == REQUEST_FINE_LOC) {
            Log.d("MC", "loc")
            findBluetooth()
        }
    }
}
