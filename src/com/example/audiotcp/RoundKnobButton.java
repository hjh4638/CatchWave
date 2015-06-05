package com.example.audiotcp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class RoundKnobButton extends RelativeLayout implements
		OnGestureListener {
	final String TAG = "Check Event";
	private Context context;
	private GestureDetector gestureDetector;
	private ImageView ivRotor;
	private Bitmap bmpRotorOn, bmpRotorOff;
	private boolean mState = false;
	private int m_nWidth = 0, m_nHeight = 0;

	interface RoundKnobButtonListener {
		public void onStateChange(boolean newstate);
		public void onRotate(int percentage);
	}

	private RoundKnobButtonListener m_listener;

	public void SetListener(RoundKnobButtonListener l) {
		m_listener = l;
	}

	public void SetState(boolean state) {
		mState = state;
		if (state) {
			((MainActivity) context).play();
		} else {
			((MainActivity) context).stop();
		}
		// Toast.makeText(context, "state : " + state,
		// Toast.LENGTH_SHORT).show();
		ivRotor.setImageBitmap(state ? bmpRotorOn : bmpRotorOff);
	}

	public void ChangeView(boolean state) {
		mState = state;
		ivRotor.setImageBitmap(state ? bmpRotorOn : bmpRotorOff);
	}

	public RoundKnobButton(Context context, int rotoron, int rotoroff,
			final int w, final int h) {
		super(context);
		// we won't wait for our size to be calculated, we'll just store out
		// fixed size
		this.context = context;
		m_nWidth = w;
		m_nHeight = h;
		// create stator
		// load rotor images
		Bitmap srcon = BitmapFactory.decodeResource(context.getResources(),
				rotoron);
		Bitmap srcoff = BitmapFactory.decodeResource(context.getResources(),
				rotoroff);
		float scaleWidth = ((float) w) / srcon.getWidth();
		float scaleHeight = ((float) h) / srcon.getHeight();
		Log.i("flase", scaleWidth + "");
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		bmpRotorOn = Bitmap.createBitmap(srcon, 0, 0, srcon.getWidth(),
				srcon.getHeight(), matrix, true);

		bmpRotorOff = Bitmap.createBitmap(srcoff, 0, 0, srcoff.getWidth(),
				srcoff.getHeight(), matrix, true);
		// create rotor
		ivRotor = new ImageView(context);
		ivRotor.setImageBitmap(bmpRotorOn);
		RelativeLayout.LayoutParams lp_ivKnob = new RelativeLayout.LayoutParams(
				250, 250);// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp_ivKnob.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(ivRotor, lp_ivKnob);

		// set initial state
		SetState(mState);
		// enable gesture detector
		gestureDetector = new GestureDetector(getContext(), this);
	}

	// << 그림 그리기 >>
	private float cartesianToPolar(float x, float y) {
		Log.i(TAG, "carresianToPolar");
		return (float) -Math.toDegrees(Math.atan2(x - 0.5f, y - 0.5f));
	}

	// << 그림 그리기 >>
	public void setRotorPosAngle(float deg) {
		Log.i(TAG, "setRotorPosAngle");
		if (deg >= 210 || deg <= 150) {
			if (deg > 180)
				deg = deg - 360;
			Matrix matrix = new Matrix();
			ivRotor.setScaleType(ScaleType.MATRIX);
			matrix.postRotate((float) deg, m_nWidth / 2, m_nHeight / 2);
			ivRotor.setImageMatrix(matrix);
		}
	}

	// << 그림 그리기 >>
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		Log.i(TAG, "onScroll");
		float x = e2.getX() / ((float) getWidth());
		float y = e2.getY() / ((float) getHeight());
		float rotDegrees = cartesianToPolar(1 - x, 1 - y);// 1- to correct our
															// custom axis
															// direction
		if (!Float.isNaN(rotDegrees)) {
			// instead of getting 0-> 180, -180 0 , we go for 0 -> 360
			float posDegrees = rotDegrees;
			if (rotDegrees < 0)
				posDegrees = 360 + rotDegrees;

			// deny full rotation, start start and stop point, and get a linear
			// scale
			if (posDegrees > 210 || posDegrees < 150) {
				// rotate our imageview
				setRotorPosAngle(posDegrees);
				// get a linear scale
				float scaleDegrees = rotDegrees + 150; // given the current
														// parameters, we go
														// from 0 to 300
				// get position percent
				int percent = (int) (scaleDegrees / 3);
				if (m_listener != null)
					m_listener.onRotate(percent);
				return true; // consumed
			} else
				return false;
		} else
			return false; // not consumed
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_UP:
			Matrix matrix = new Matrix();
			ivRotor.setScaleType(ScaleType.FIT_CENTER);
			matrix.postRotate((float) 0, m_nWidth / 2, m_nHeight / 2);
			ivRotor.setImageMatrix(matrix);
		case MotionEvent.ACTION_DOWN:
		}

		if (gestureDetector.onTouchEvent(event))
			return true;
		else
			return super.onTouchEvent(event);
	}

	public boolean onDown(MotionEvent event) {
		return true;
	}

	public boolean onSingleTapUp(MotionEvent e) {
		SetState(!mState);
		return true;
	}

	public void onShowPress(MotionEvent e) {
		// 오래 눌렀을때 이벤트 발생 (onLongPress 보다 덜 길어도 가능)
	}

	// 방향 캐치
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		if (arg0.getX() - arg1.getX() > 100 && Math.abs(arg2) > 30) {
			((MainActivity) context).prev();
			Matrix matrix = new Matrix();
			/*ivRotor.setScaleType(ScaleType.MATRIX);*/
			matrix.postRotate((float) 0, m_nWidth / 2, m_nHeight / 2);
			ivRotor.setImageMatrix(matrix);
			
		} else if (arg1.getX() - arg0.getX() > 100 && Math.abs(arg2) > 30) {
			((MainActivity) context).next();
			Matrix matrix = new Matrix();
			/*ivRotor.setScaleType(ScaleType.MATRIX);*/
			matrix.postRotate((float) 0, m_nWidth / 2, m_nHeight / 2);
			ivRotor.setImageMatrix(matrix);
		}

		return false;
	}

	public void onLongPress(MotionEvent e) {
	}

}
