package com.ac.audiocomunicationtest.media;

import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import com.ac.audiocomunicationtest.media.runnable.AudioRecorderRunnable;
import com.ac.audiocomunicationtest.media.runnable.AudioTrackRunnable;

public class AudioHelper {
//	public static final int PLAY_MODE_RECEIVER	= 1;		// 听筒播放模式
//	public static final int PLAY_MODE_SPEAKER	= 2;		// 喇叭播放模式
    public AudioRecord mAudioRecord;
    public AudioTrack mAudioTrack;
    private int mBufferSizeInBytes;
    public final String TAG = "AudioHelper";
    private static AudioHelper instance;
    public boolean isInitComplete = false;
    private MediaDataQueue<byte[]> mQueue = new MediaDataQueue<>();
    private AudioRecorderRunnable mRecorder;
    private final AudioTrackRunnable mTrack;
    private EncoderOrDecoder mCoder;
    private AudioHelper(){
        System.out.print("init");
        boolean isInitRd = initAudioRecord();
        boolean isInitAt = initAudioTrack();
        isInitComplete = isInitAt && isInitRd;
        mCoder = new EncoderOrDecoder();
        mTrack = new AudioTrackRunnable(mCoder,mAudioTrack, mQueue);
        mRecorder = new AudioRecorderRunnable(mCoder,mAudioRecord, mTrack, mQueue);
    }

    public static AudioHelper getInstance(){
        if(instance==null){
            synchronized (AudioHelper.class){
                if(instance==null){
                    instance = new AudioHelper();
                }
            }
        }
        return instance;
    }

    public boolean initAudioRecord(){
        mBufferSizeInBytes = AudioRecord.getMinBufferSize(Const.SAMPLE_RATE_IN_HZ, Const.AUDIO_RECORD_CHANNEL_CONFIG, Const.AUDIO_FORMAT);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, Const.SAMPLE_RATE_IN_HZ, Const.AUDIO_RECORD_CHANNEL_CONFIG, Const.AUDIO_FORMAT, mBufferSizeInBytes);
        Log.i(TAG, "mBufferSizeInBytes----> " + mBufferSizeInBytes);
        return mAudioRecord.getState()==AudioRecord.STATE_INITIALIZED;
    }


    public boolean initAudioTrack() {
        int bufferSizePlayer = AudioTrack.getMinBufferSize(Const.SAMPLE_RATE_IN_HZ, Const.AUDIO_TRACK_CHANNEL_CONFIG, Const.AUDIO_FORMAT);
        Log.i("====buffer Size player ", String.valueOf(bufferSizePlayer));
        mAudioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, Const.SAMPLE_RATE_IN_HZ,
                Const.AUDIO_TRACK_CHANNEL_CONFIG, Const.AUDIO_FORMAT, bufferSizePlayer,
                AudioTrack.MODE_STREAM);
        return mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED;
    }

    public void start(){
        new Thread(mRecorder).start();
        new Thread(mTrack).start();
    }

    public void stop(){
        mTrack.stop();
        mRecorder.stop();
        synchronized (mTrack) {
            mTrack.notify();
        }
    }
}
