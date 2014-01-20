package com.example.bluefile.fragment;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.bluefile.BTDataManager;
import com.example.bluefile.BTFile;
import com.example.bluefile.BTFileManager;
import com.example.bluefile.R;
import com.example.bluefile.Serializer;
import com.example.bluefile.view.BlueToothClientView;

public class BlueToothClientFragment extends Fragment {

	private static final String NAME = "BlueFile";
	
	private View view;
	private Activity activity;
	private UUID btUUID;

	private BluetoothAdapter btAdapter;	
	private ProgressDialog mProgress;
	
	private Handler handler;

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
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(mProgress != null)
					mProgress.dismiss();
			}
		};
		
		return layout;
	}

	@Override
	public void onStart() {
		System.out.println("ClientOnStart");
		
		super.onStart();

		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null) {
			System.out.println("Device not bluetooth compatible");
			System.exit(0);
		}

		if(!btAdapter.isEnabled()) {
			requestBlueToothOn();
		}
		
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		activity.startActivity(discoverableIntent);
	}
	

	public void startClientRecieve() {
		mProgress = new ProgressDialog(activity);
		mProgress.setTitle("Client");
		mProgress.setMessage("Trying to connect to host...");
		mProgress.show();
		
		/*
		ConnectRunnable connRun = new ConnectRunnable();
		Thread connThread = new Thread(connRun);
		connThread.run();
		*/
		
		new ConnectTask().execute(0);
	}
	

	private void requestBlueToothOn() {
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		((Activity)view.getContext()).startActivityForResult(enableBtIntent, 1);
	}
	
	private class ConnectTask extends AsyncTask<Integer, Integer, Long> {
		
		private BluetoothServerSocket mServerSocket;


		protected Long doInBackground(Integer ... i) {
			
			BluetoothServerSocket tmp;
			try {
				tmp = btAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, btUUID);
			} catch (IOException e) { 
				handler.sendEmptyMessage(0);
				return -1L;
			}

			mServerSocket = tmp;
			
			// Cancel discovery because it will slow down the connection
			btAdapter.cancelDiscovery();

			BluetoothSocket socket = null;

			// Keep listening until exception occurs or a socket is returned
			while (socket == null) {
				try {
					socket = mServerSocket.accept();
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}

			}

			publishProgress(0);
			
			acceptTransfer(socket);
			
			try {
				mServerSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return 0L;
		}

		protected void onProgressUpdate(Integer... progress) {
			if(progress[0] == 0) {
				mProgress.setMessage("Trying to recieve file from ...");
			} else if (progress[0] == 1) {
				mProgress.setMessage("Wrote file to system.");
			} else {
				mProgress.setMessage("Done!");
			}
			
		}

		protected void onPostExecute(Long result) {
			mProgress.dismiss();
		}
		
		
		private void acceptTransfer(BluetoothSocket mSocket) {
			BTDataManager manager = new BTDataManager(mSocket);
			Thread t = new Thread(manager);
			t.start();

			boolean hasTransfered = false;
			
			while(!hasTransfered) {
				Object obj = manager.getLatestData();

				if(obj != null) {
					byte [] data = (byte [])obj;
					try {
						BTFile file = (BTFile)Serializer.deserialize(data);
						BTFileManager.writeFile(file, activity);
						
						publishProgress(1);
						
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
					hasTransfered = true;
				}
			}
			
			manager.cancel();
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * 
	 * @author Max
	 *
	 */
	private class ConnectRunnable implements Runnable {
		
		private BluetoothServerSocket mServerSocket;
		private volatile boolean isCanceled;

		public ConnectRunnable() {			
			isCanceled = false;
		}

		public void run() {
			BluetoothServerSocket tmp;
			try {
				tmp = btAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, btUUID);
			} catch (IOException e) { 
				e.printStackTrace();
				handler.sendEmptyMessage(0);
				return;
			}
			
			mServerSocket = tmp;
			handler.sendMessage(new Message());
			
			// Cancel discovery because it will slow down the connection
	        btAdapter.cancelDiscovery();
	        
			BluetoothSocket socket = null;
			
			// Keep listening until exception occurs or a socket is returned
			while (socket == null) {
				try {
					socket = mServerSocket.accept();
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
				
			}
						
			mProgress.setMessage("Trying to recieve file from " + socket.getRemoteDevice().getName() + "...");
			mProgress.incrementProgressBy(50);
			
			isCanceled = false;
			acceptTransfer(socket);
			try {
				mServerSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		public void cancel() {
			isCanceled = true;
		}
		
		private void acceptTransfer(BluetoothSocket mSocket) {
			BTDataManager manager = new BTDataManager(mSocket);
			Thread t = new Thread(manager);
			t.start();

			while(!isCanceled) {
				Object obj = manager.getLatestData();

				if(obj != null) {
					byte [] data = (byte [])obj;
					try {
						BTFile file = (BTFile)Serializer.deserialize(data);
						BTFileManager.writeFile(file, activity);
						
						mProgress.incrementProgressBy(50);
						handler.sendEmptyMessage(0);                        

					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
					// Only one object for now
					isCanceled = true;
				}
			}
			
			manager.cancel();

		}

	}
	
	
}
