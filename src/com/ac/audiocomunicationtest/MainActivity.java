package com.ac.audiocomunicationtest;

import android.app.Activity;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ac.audiocomunicationtest.media.Const;
import com.ac.audiocomunicationtest.media.EncoderOrDecoder;
import com.ac.audiocomunicationtest.media.MediaDataQueue;
import com.ac.audiocomunicationtest.media.runnable.AudioRecorderRunnable;
import com.ac.audiocomunicationtest.media.runnable.AudioTrackRunnable;

public class MainActivity extends Activity {

	public final String TAG = "MainActivity";
	private AudioRecord mAudioRecord;
	private MediaDataQueue<byte[]> queue = new MediaDataQueue<byte[]>();
	private AudioTrack mAudioTrack;
	private int bufferSizeInBytes;
	private AudioRecorderRunnable recorder;
	private AudioTrackRunnable track;
	private EncoderOrDecoder coder;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final boolean initRecord = setAudioRecord();
		final boolean initPlayer = setAudioTrack();
		coder = new EncoderOrDecoder();
		track = new AudioTrackRunnable(coder,mAudioTrack,queue);
		recorder = new AudioRecorderRunnable(coder,mAudioRecord,track,queue);
		Button btn_rd = (Button) findViewById(R.id.btn_record);
		btn_rd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(initRecord && initPlayer){
					new Thread(recorder).start();
					new Thread(track).start();
				}
			}
		});
		Button btn_sr = (Button) findViewById(R.id.btn_stop_r);
		btn_sr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				track.stop();
				recorder.stop();
				synchronized (track) {
					track.notify();
				}
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public boolean setAudioRecord(){
		bufferSizeInBytes = AudioRecord.getMinBufferSize(Const.SAMPLE_RATE_IN_HZ, Const.AUDIO_RECORD_CHANNEL_CONFIG, Const.AUDIO_FORMAT);
		mAudioRecord = new AudioRecord(AudioSource.MIC, Const.SAMPLE_RATE_IN_HZ, Const.AUDIO_RECORD_CHANNEL_CONFIG, Const.AUDIO_FORMAT, bufferSizeInBytes);
		Log.i(TAG, "bufferSizeInBytes----> "+bufferSizeInBytes);
		return mAudioRecord.getState()==AudioRecord.STATE_INITIALIZED;
	}
	
	public boolean setAudioTrack() {
		
//		AudioManager audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
//		audioManager.setMode(AudioManager.MODE_NORMAL);
		int bufferSizePlayer = AudioTrack.getMinBufferSize(Const.SAMPLE_RATE_IN_HZ,Const.AUDIO_TRACK_CHANNEL_CONFIG, Const.AUDIO_FORMAT);
		Log.i("====buffer Size player ", String.valueOf(bufferSizePlayer));
		mAudioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, Const.SAMPLE_RATE_IN_HZ,
				Const.AUDIO_TRACK_CHANNEL_CONFIG, Const.AUDIO_FORMAT, bufferSizePlayer,
				AudioTrack.MODE_STREAM);
		return mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED;
	}
}
