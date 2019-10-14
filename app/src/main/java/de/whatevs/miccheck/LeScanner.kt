package de.whatevs.miccheck

import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.util.Log

private const val SCAN_PERIOD: Long = 10000

class LeScanner(
    private val handler: Handler,
    private val context: Context
) {

    private var mScanning: Boolean = false
    private val bluetoothLeScanner: BluetoothLeScanner
        get() {
            val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothAdapter = bluetoothManager.adapter
            return bluetoothAdapter.bluetoothLeScanner
        }

    private val bleScanner = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            Log.d("ScanDeviceActivity", "onScanResult(): ${result?.device?.address} - ${result?.device?.name}")
        }
    }

    fun scanLeDevice(enable: Boolean) {
        when (enable) {
            true -> {
                // Stops scanning after a pre-defined scan period.
                handler.postDelayed({
                    mScanning = false
                    bluetoothLeScanner.stopScan(bleScanner)
                }, SCAN_PERIOD)
                mScanning = true
                bluetoothLeScanner.startScan(bleScanner)
            }
            else -> {
                mScanning = false
                bluetoothLeScanner.stopScan(bleScanner)
            }
        }
    }
}
