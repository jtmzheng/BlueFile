package com.example.bluefile;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BTDataManager implements Runnable {
	private final BluetoothSocket m_socket; //The Bluetooth socket that is connected
	private final InputStream m_instream; 
	private final OutputStream m_outstream;
	private DataInputStream m_instreamReader;

	private static final Object m_socketLock = new Object(); // uh I don't know how many objects can use the bluetooth socket.. this may screw up
	private final ArrayBlockingQueue<Object> m_dataPackets; //The data packets that have been read
	private volatile boolean isCanceled;

	public BTDataManager(BluetoothSocket socket) {
		m_socket = socket;
		InputStream tmpIn = null;
		OutputStream tmpOut = null;
		
		try {
			tmpIn = m_socket.getInputStream();
			tmpOut = m_socket.getOutputStream();

		} catch (IOException e) {
			e.printStackTrace();
		}

		m_instream = tmpIn;

		m_instreamReader = new DataInputStream(m_instream);


		m_outstream = tmpOut;
		
		// At most 10 objects queued 
		m_dataPackets = new ArrayBlockingQueue<Object>(10);
		isCanceled = false;


	}

	@Override
	public void run() {
		while (!isCanceled) { 
			try {
				if(m_instreamReader.available() <= 0)
					continue;
				byte[] numBytes = new byte [4];
				m_instreamReader.readFully(numBytes, 0, 4);
				int result = (numBytes[3] & 0xFF) | (numBytes[2] & 0xFF) << 8 | (numBytes[1] & 0xFF) << 16 | (numBytes[0] & 0xFF) << 24;

				byte[] buffer = new byte[result];
				m_instreamReader.readFully(buffer);
				m_dataPackets.add(buffer);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Retrieve the object data read in from the bluetooth socket from the queue
	 * @author Max 
	 */
	public synchronized Object getLatestData() {
		return this.m_dataPackets.poll(); // returns null if empty
	}

	public void write(byte [] bytes) {
		try {
			m_outstream.write(bytes);
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}

	public void write(BTFile btFile) throws IOException {
		byte[] data = Serializer.serialize(btFile);
		byte[] data2 = new byte[data.length + 4];
		int size = data.length;
		byte[] arr = ByteBuffer.allocate(4).putInt(size).array();
		data2[0] = arr[0];
		data2[1] = arr[1];
		data2[2] = arr[2];
		data2[3] = arr[3];

		for (int i = 4; i < data2.length; i++) {
			data2[i] = data[i - 4];
		}

		write(data2);

		Log.d("WRITE", "Write BTFile 2");

	}

	/* Call this from the main activity to shutdown the connection */
	public void cancel() {
		isCanceled = true;
		try {
			m_socket.close();
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}

}
