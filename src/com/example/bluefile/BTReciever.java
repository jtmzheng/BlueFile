package com.example.bluefile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class BTReciever extends BroadcastReceiver {

	public BluetoothDevice myDevice;
	public Map<String, BluetoothDevice> map;
	public List<BluetoothDevice> devices;

	public BTReciever () {
		map = new HashMap<String, BluetoothDevice> ();
		devices = new ArrayList<BluetoothDevice>();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			
			// Add the device if valid and not already in the list
			if(device != null && !devices.contains(device)) {
				devices.add(device);
				map.put(device.getName(), device);
				Log.v("BTReciever", "Found device! " + device.getName() + "\n" + device.getName());
			}
		}

	}
}
