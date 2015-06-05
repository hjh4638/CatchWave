package com.example.audiotcp;

import android.app.Activity;
import android.app.Application;
import android.util.DisplayMetrics;

public class Singleton extends Application {
	private static Singleton m_Instance;
	static final boolean SET_DEBUG = true;
	// Appscreen metrics
	public float m_fFrameS = 0;
	public int m_nFrameW = 0, m_nFrameH = 0, m_nTotalW = 0, m_nTotalH = 0,
			m_nPaddingX = 0, m_nPaddingY = 0;

	public Singleton() {
		super();
		m_Instance = this;
	}

	public static Singleton getInstance() {
		if (m_Instance == null) {
			synchronized (Singleton.class) {
				if (m_Instance == null)
					new Singleton();
			}
		}
		return m_Instance;
	}
	@Override
	public void onCreate() {
		super.onCreate();

	}
	
	public void InitGUIFrame(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		m_nTotalW = dm.widthPixels;
		m_nTotalH = dm.heightPixels;
		// scale factor
		m_fFrameS = (float) m_nTotalW / 1280.0f;

		// compute our frame
		m_nFrameW = m_nTotalW;
		m_nFrameH = (int) (960.0f * m_fFrameS);

		// compute padding for our frame inside the total screen size
		m_nPaddingY = 0;
		m_nPaddingX = (m_nTotalW - m_nFrameW) / 2;

	}
	public int Scale(int v) { // 스케일 정하는 함수 !! <설정 할때>
		float s = (float) v * m_fFrameS;
		int rs = 0;
		if (s - (int) s >= 0.5)
			rs = ((int) s) + 1;
		else
			rs = (int) s;
		return rs;
	}

}
