package com.example.bluefile.fragment;

import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.bluefile.BTReciever;
import com.example.bluefile.R;
import com.example.bluefile.view.BlueToothHostView;

public class BlueToothHostFragment extends Fragment {

	private View view;
	private UUID btUUID;
	
	private BluetoothAdapter btAdapter;
	private BTReciever btReceiver;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		LinearLayout layout = new LinearLayout(this.getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);

		LinearLayout btLayout = new LinearLayout(this.getActivity());
		btLayout.setGravity(Gravity.CENTER);

		BlueToothHostView blueToothView = new BlueToothHostView(this.getActivity());
		btLayout.addView(blueToothView);

		view = inflater.inflate(R.layout.bluehost_view, layout, true); 
		
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
		btReceiver = new BTReciever();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.getActivity().registerReceiver(btReceiver, filter);
        btAdapter.startDiscovery();             
	}
	
	public void requestBlueToothOn() {
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		((Activity)view.getContext()).startActivityForResult(enableBtIntent, 1);
	}
	
}
