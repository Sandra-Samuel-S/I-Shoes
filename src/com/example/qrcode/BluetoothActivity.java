package com.example.qrcode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class BluetoothActivity extends Activity {
	private static final int REQUEST_ENABLE_BT = 1;

	BluetoothAdapter bluetoothAdapter;
	ArrayList<BluetoothDevice> pairedDeviceArrayList;

	private Timer timer;
	private TimerTask timerTask;

	ListView listViewPairedDevice;
	LinearLayout inputPane;
	Button b1, b2, b3;
	String scan, res;
	Random random;
	StringBuilder sp;
	ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
	private UUID myUUID;
	private final String UUID_STRING_WELL_KNOWN_SPP = "00001101-0000-1000-8000-00805F9B34FB";

	ThreadConnectBTdevice myThreadConnectBTdevice;
	ThreadConnected myThreadConnected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);
		listViewPairedDevice = (ListView) findViewById(R.id.pairedlist);
		inputPane = (LinearLayout) findViewById(R.id.inputpane);
		b2 = (Button) findViewById(R.id.reve);
		b3 = (Button) findViewById(R.id.can);
		b3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finishAffinity();
			}
		});
		
		
		b2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onResume();
			}
		});
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH)) {
			Toast.makeText(this, "FEATURE_BLUETOOTH NOT support",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			Toast.makeText(this,
					"Bluetooth is not supported on this hardware platform",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		String stInfo = bluetoothAdapter.getName() + "\n"
				+ bluetoothAdapter.getAddress();

	}

	public void onPause() {
		super.onPause();
		timer.cancel();
	}
	public void onResume(){
		super.onResume();
		try {
		timer = new Timer();
		timerTask = new TimerTask() {
		@Override
		public void run() {

		runOnUiThread(new Runnable() {
		@Override
		public void run() {
			String rotp="LR";
			random=new  Random();
			sp=new StringBuilder(1);
			for (int i = 0; i < 1; i++) {
				sp.append(rotp.charAt(random.nextInt(rotp.length())));
				
							}
			Toast.makeText(BluetoothActivity.this,sp.toString(),Toast.LENGTH_LONG).show();
			if (myThreadConnected != null) {
				byte[] bytesToSend = sp.toString()
						.getBytes();
				myThreadConnected.write(bytesToSend);
			}

		}
		});

		}
		};
		timer.schedule(timerTask, 15000, 15000);
		} catch (IllegalStateException e){
		android.util.Log.i("Damn", "resume error");
		}
		}

	@Override
	protected void onStart() {
		super.onStart();

		// Turn ON BlueTooth if it is OFF
		if (!bluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}

		setup();
	}

	private void setup() {
		Set<BluetoothDevice> pairedDevices = bluetoothAdapter
				.getBondedDevices();
		if (pairedDevices.size() > 0) {
			pairedDeviceArrayList = new ArrayList<BluetoothDevice>();

			for (BluetoothDevice device : pairedDevices) {
				pairedDeviceArrayList.add(device);
			}

			pairedDeviceAdapter = new ArrayAdapter<BluetoothDevice>(this,
					android.R.layout.simple_list_item_1, pairedDeviceArrayList);
			listViewPairedDevice.setAdapter(pairedDeviceAdapter);

			listViewPairedDevice
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							BluetoothDevice device = (BluetoothDevice) parent
									.getItemAtPosition(position);
							Toast.makeText(
									BluetoothActivity.this,
									"Name: " + device.getName() + "\n"
											+ "Address: " + device.getAddress()
											+ "\n" + "BondState: "
											+ device.getBondState() + "\n"
											+ "BluetoothClass: "
											+ device.getBluetoothClass() + "\n"
											+ "Class: " + device.getClass(),
									Toast.LENGTH_SHORT).show();

							// textStatus.setText("start ThreadConnectBTdevice");
							myThreadConnectBTdevice = new ThreadConnectBTdevice(
									device);
							myThreadConnectBTdevice.start();
						}
					});
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (myThreadConnectBTdevice != null) {
			myThreadConnectBTdevice.cancel();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				setup();
			} else {
				Toast.makeText(this, "BlueTooth NOT enabled",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	// Called in ThreadConnectBTdevice once connect successed
	// to start ThreadConnected
	private void startThreadConnected(BluetoothSocket socket) {

		myThreadConnected = new ThreadConnected(socket);
		myThreadConnected.start();
	}

	/*
	 * ThreadConnectBTdevice: Background Thread to handle BlueTooth connecting
	 */
	private class ThreadConnectBTdevice extends Thread {

		private BluetoothSocket bluetoothSocket = null;
		private final BluetoothDevice bluetoothDevice;

		private ThreadConnectBTdevice(BluetoothDevice device) {
			bluetoothDevice = device;

			try {
				bluetoothSocket = device
						.createRfcommSocketToServiceRecord(myUUID);
				// textStatus.setText("bluetoothSocket: \n" + bluetoothSocket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			boolean success = false;
			try {
				bluetoothSocket.connect();
				success = true;
			} catch (IOException e) {
				e.printStackTrace();

				final String eMessage = e.getMessage();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// textStatus.setText("something wrong bluetoothSocket.connect(): \n"
						// + eMessage);
					}
				});

				try {
					bluetoothSocket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			if (success) {
				// connect successful
				final String msgconnected = "connect successful:\n"
						+ "BluetoothSocket: " + bluetoothSocket + "\n"
						+ "BluetoothDevice: " + bluetoothDevice;

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// textStatus.setText(msgconnected);

						listViewPairedDevice.setVisibility(View.GONE);
						inputPane.setVisibility(View.VISIBLE);
						
					}
				});

				startThreadConnected(bluetoothSocket);
			} else {
				// fail
			}
		}

		public void cancel() {

			Toast.makeText(getApplicationContext(), "close bluetoothSocket",
					Toast.LENGTH_LONG).show();

			try {
				bluetoothSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/*
	 * ThreadConnected: Background Thread to handle Bluetooth data communication
	 * after connected
	 */
	private class ThreadConnected extends Thread {
		private final BluetoothSocket connectedBluetoothSocket;
		private final InputStream connectedInputStream;
		private final OutputStream connectedOutputStream;

		public ThreadConnected(BluetoothSocket socket) {
			connectedBluetoothSocket = socket;
			InputStream in = null;
			OutputStream out = null;

			try {
				in = socket.getInputStream();
				out = socket.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			connectedInputStream = in;
			connectedOutputStream = out;
		}

		@Override
		public void run() {
			byte[] buffer = new byte[1024];
			int bytes;

			while (true) {
				try {
					bytes = connectedInputStream.read(buffer);
					String strReceived = new String(buffer, 0, bytes);
					final String msgReceived = String.valueOf(bytes)
							+ " bytes received:\n" + strReceived;

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// textStatus.setText(msgReceived);
						}
					});

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

					final String msgConnectionLost = "Connection lost:\n"
							+ e.getMessage();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// textStatus.setText(msgConnectionLost);
						}
					});
				}
			}
		}

		public void write(byte[] buffer) {
			try {
				connectedOutputStream.write(buffer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void cancel() {
			try {
				connectedBluetoothSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
