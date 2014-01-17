package com.example.bluefile.fragment;

import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bluefile.BTReciever;
import com.example.bluefile.R;
import com.example.bluefile.fragment.BlueToothHostFragment.StableArrayAdapter;
import com.example.bluefile.view.BlueToothClientView;

public class BlueToothClientFragment extends Fragment {

	private View view;
	private Activity activity;
	private TextView mCurrentDeviceName;
	private UUID btUUID;

	private BluetoothAdapter btAdapter;
	private BTReciever btReceiver;
	private StableArrayAdapter mDeviceAdapter;
	private BluetoothDevice mCurrentBtDevice;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout layout = new LinearLayout(this.getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);

		LinearLayout btLayout = new LinearLayout(this.getActivity());
		btLayout.setGravity(Gravity.CENTER);

		BlueToothClientView blueToothView = new BlueToothClientView(this.getActivity());
		btLayout.addView(blueToothView);

		view = inflater.inflate(R.layout.blueclient_view, layout, true); 
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
	}
	
	private void requestBlueToothOn() {
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		((Activity)view.getContext()).startActivityForResult(enableBtIntent, 1);
	}
	
	
}
