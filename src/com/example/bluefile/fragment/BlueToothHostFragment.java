package com.example.bluefile.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bluefile.BTDataManager;
import com.example.bluefile.BTFile;
import com.example.bluefile.BTFileManager;
import com.example.bluefile.BTReciever;
import com.example.bluefile.R;
import com.example.bluefile.Serializer;
import com.example.bluefile.view.BlueToothHostView;

public class BlueToothHostFragment extends Fragment {

	private View view;
	private Activity activity;
	private TextView mCurrentDeviceName;
	private UUID btUUID;

	private BluetoothAdapter btAdapter;
	private BTReciever btReceiver;
	private StableArrayAdapter mDeviceAdapter;
	private BluetoothDevice mCurrentBtDevice;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			System.out.println(msg);
		}
	};


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		LinearLayout layout = new LinearLayout(this.getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);

		LinearLayout btLayout = new LinearLayout(this.getActivity());
		btLayout.setGravity(Gravity.CENTER);

		BlueToothHostView blueToothView = new BlueToothHostView(this.getActivity());
		btLayout.addView(blueToothView);

		view = inflater.inflate(R.layout.bluehost_view, layout, true); 
		activity = this.getActivity();

		layout.addView(btLayout);

		String uuid ="566156c0-49a8-11e3-8f96-0800200c9a66";
		btUUID = UUID.fromString(uuid);

		return layout;
	}

	@Override 
	public void onStart() {
		super.onStart();

		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null) {
			System.out.println("Device not bluetooth compatible");
			System.exit(0);
		}

		if(!btAdapter.isEnabled()) {
			requestBlueToothOn();
		}

		// Get a list of the bluetooth devices available
		ListView listview = (ListView)view.findViewById(R.id.listDevices);
		mDeviceAdapter = new StableArrayAdapter(getActivity(), R.layout.row, new ArrayList<String>());
		btReceiver = new BTReciever(mDeviceAdapter);
		listview.setAdapter(mDeviceAdapter);

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.getActivity().registerReceiver(btReceiver, filter);
		btAdapter.startDiscovery();       
		
		mCurrentDeviceName = (TextView)view.findViewById(R.id.currentDevice);
		
		// Select the current device
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mCurrentBtDevice = btReceiver.getCurrentDevice((String)parent.getItemAtPosition(position));
				mCurrentDeviceName.setText(getResources().getString(R.string.selectedDevice) + mCurrentBtDevice.getName());
			}
		});		
	}
	
	public boolean startHostTransfer() {
		if(mCurrentBtDevice != null) {
			System.out.println("Passed");
			ConnectRunnable connectRun = new ConnectRunnable(mCurrentBtDevice);
			Thread connectHostThread = new Thread(connectRun);
			connectHostThread.start();
			return true;
		} else {
			System.out.println("Failed");
			return false;
		}
	}
	
	private void requestBlueToothOn() {
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		((Activity)view.getContext()).startActivityForResult(enableBtIntent, 1);
	}

	/**
	 * 
	 * @author Max
	 *
	 */
	public class StableArrayAdapter extends ArrayAdapter<String> {

		Map<String, Integer> mIdMap;

		public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
			super(context, textViewResourceId, objects);
			mIdMap = new HashMap<String, Integer>();
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
		}

		@Override
		public long getItemId(int position) { 		
			return super.getItemId(position);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}

	/**
	 * 
	 * @author Max
	 *
	 */
	private class ConnectRunnable implements Runnable {
		
		private BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectRunnable(BluetoothDevice device) {
			BluetoothSocket tmp = null;
			mmDevice = device;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server code
				tmp = device.createInsecureRfcommSocketToServiceRecord(btUUID);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			mmSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection
			btAdapter.cancelDiscovery();

			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				Log.v("Connection: ", "Trying to connect");
				mmSocket.connect();
			} catch (IOException connectException) {
				Log.v("Connection: ", "Failed");
				return;
			}

			Log.v("Connection: ", "Success");
			// Do work to manage the connection (in a separate thread)
			// transfer(mmSocket);
			btAdapter.startDiscovery();
		}

		private void transfer(BluetoothSocket mmSocket2) {
			BTDataManager manager = new BTDataManager(mmSocket2);
			Thread t = new Thread(manager);
			t.start();

			while(true) {
				Object obj = manager.getLatestData();
				if(obj != null) {
					byte [] data = (byte [])obj;
					try {
						BTFile file = (BTFile)Serializer.deserialize(data);
						BTFileManager.writeFile(file, activity);
						handler.sendEmptyMessage(0);                        

					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}

		}

	}



}
