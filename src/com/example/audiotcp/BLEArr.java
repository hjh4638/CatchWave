package com.example.audiotcp;

import android.bluetooth.BluetoothDevice;

public class BLEArr{
	   
	BluetoothDevice device;
	int rssi;
	String uuid; 
	
	void setdevice (BluetoothDevice device, int rssi, String uuid){
		this.device = device;
		this.rssi = rssi;
		this.uuid = uuid;
	}
	String getUUid(){
		return uuid;
	}
	BluetoothDevice getDevice(){
		return device;
	}
}

