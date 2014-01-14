package com.example.bluefile;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.bluefile.fragment.BlueToothHostFragment.StableArrayAdapter;

public class BTReciever extends BroadcastReceiver {

	public BluetoothDevice myDevice;
	public Map<String, BluetoothDevice> mapDevices;
	public List<BluetoothDevice> devices;
		
	private StableArrayAdapter mDeviceAdapter;

	public BTReciever (StableArrayAdapter adapter) {
		mapDevices = new LinkedHashMap<String, BluetoothDevice> ();
		devices = new ArrayList<BluetoothDevice>();
		mDeviceAdapter = adapter;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			
			// Add the device if valid and not already in the list
			if(device != null && !devices.contains(device)) {
				devices.add(device);
				mapDevices.put(device.getName(), device);
				
				mDeviceAdapter.clear();
				mDeviceAdapter.addAll(mapDevices.keySet());
				mDeviceAdapter.notifyDataSetChanged();
				
				Log.v("BTReciever", "Found device! " + device.getName() + "\n" + device.getName());
				Log.v("BTReciever", "Devices map: " + mapDevices.keySet());
			}
		}

	}
	
}
