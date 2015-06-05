package com.example.audiotcp;



public class DSPforJNI {
	static {
		System.loadLibrary("dsp-main");
	}

	private native byte[] DSPfromJNI(byte i[]);
	private native void  DSPfromLOG();
	

	public byte[] playAfterDSP(byte i[]) {
		return DSPfromJNI(i);
	}
	
	public void DSPAfterLog(){
		DSPfromLOG();
	}
	// Wrapping functions
	public void func(byte[] a, int b, int c) {
	}
}
