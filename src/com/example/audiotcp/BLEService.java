package com.example.audiotcp;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class BLEService extends Service {

	final String TAG = "BLETEST-Service";
	// BLE
	private static final int BLE_SEND_SIGNAL = 0;
	private static final long SCAN_PERIOD = 10000;
	public static CircularQueue cqueue;
	static boolean RECVING;

	private BluetoothAdapter mBluetoothAdapter;
	private Handler mHandler;

	public boolean ready = true;
	int size = 3;

	Thread BLErec;
	BLEArr bldevice;

	int count = 0;

	@Override
	public void onCreate() {
		Log.d(TAG, "BLE_SERVICE START");
		super.onCreate();
		cqueue = new CircularQueue(size);
		RECVING = true;
		mHandler = new Handler();
		bluetoothcheck();
		scanLeDevice(true);
	}

	// BLE
	public void bluetoothcheck() {
		Log.d("BLETEST", "BLECHECK");
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT)
					.show();
		}

		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		mBluetoothAdapter.enable();

		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT)
					.show();
			return;
		}
	}

	private void scanLeDevice(final boolean enable) {
		Log.d("BLETEST", "SCAN_LE_DEVICE");
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				}
			}, SCAN_PERIOD);
			mBluetoothAdapter.startLeScan(mLeScanCallback);

		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			final String uuid = getUid(device, scanRecord);
			final int Rssi = rssi;

			BLErec = new Thread(new Runnable() {
				@Override
				public void run() {
					while (RECVING) {
						if (device != null) {
							try {
								bldevice = new BLEArr();
								bldevice.setdevice(device, Rssi, uuid);

								if (!cqueue.contains(device)) {
									cqueue.insert(bldevice);
									Log.d("queue", "insert");
								}
								Log.d("JUSTTEST",
										String.valueOf(ready)
												+ " "
												+ String.valueOf(MainActivity.Activity_ready));
								if (ready && MainActivity.Activity_ready) {

									Message msg = MainActivity.RHandler
											.obtainMessage();
									msg.what = BLE_SEND_SIGNAL;
									MainActivity.RHandler.sendMessage(msg);
									ready = false;
								}

								Log.d("BLETEST", String.valueOf(uuid));
								Thread.sleep(50);

							} catch (Exception e) {
							}
						}
					}
				}
			});
			BLErec.start();
		}
	};

	String getUid(BluetoothDevice device, byte[] scanRecord) {
		int startByte = 2;

		boolean patternFound = false;
		String uuid = new String();
		while (startByte <= 5) {
			if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && // Identifies
																	// an
																	// iBeacon
					((int) scanRecord[startByte + 3] & 0xff) == 0x15) { // Identifies
																		// correct
																		// data
																		// length
				patternFound = true;
				break;
			}
			startByte++;
		}

		if (patternFound) {
			// Convert to hex String
			byte[] uuidBytes = new byte[17];
			System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 17);

			for (int i = 0; i < 17; i++) {
				uuid += (char) uuidBytes[i];
			}
		}
		return uuid;
	}

	// SERVICE LIFE CYCLE

	@Override
	public int onStartCommand(Intent intent, int RECVINGs, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, RECVINGs, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "SERVICE FINISH");
		// TODO Auto-generated method stub
		ready = true;
		RECVING = false;
		super.onDestroy();
	}
}