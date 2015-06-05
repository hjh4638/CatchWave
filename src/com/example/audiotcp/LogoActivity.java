package com.example.audiotcp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LogoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.start);
		Intent BLEService = new Intent(LogoActivity.this, BLEService.class);
		startService(BLEService);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				final Intent Intent = new Intent(LogoActivity.this,
						MainActivity.class);
				LogoActivity.this.startActivity(Intent);
				LogoActivity.this.finish();
			}
		}, 2000);
	}
}
