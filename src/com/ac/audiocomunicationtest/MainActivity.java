package com.ac.audiocomunicationtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ac.audiocomunicationtest.media.AudioHelper;

public class MainActivity extends Activity {

	public final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		AudioHelper.getInstance();
		Button btn_rd = (Button) findViewById(R.id.btn_record);
		btn_rd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(AudioHelper.getInstance().isInitComplete){
					AudioHelper.getInstance().start();
				}
			}
		});

		Button btn_sr = (Button) findViewById(R.id.btn_stop_r);
		btn_sr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AudioHelper.getInstance().stop();
			}
		});

		Button btn_py = (Button) findViewById(R.id.btn_play);
		btn_py.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});

		Button btn_sp = (Button) findViewById(R.id.btn_stop_p);
		btn_sp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
	}
}
