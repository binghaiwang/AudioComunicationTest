package com.ac.audiocomunicationtest.media.runnable;

import android.annotation.SuppressLint;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.util.Log;

import com.ac.audiocomunicationtest.media.EncoderOrDecoder;
import com.ac.audiocomunicationtest.media.MediaDataQueue;

import java.nio.ByteBuffer;

public class AudioRecorderRunnable implements Runnable {

	public final static String TAG = "RecorderAudioRunnable";
	private AudioRecord mAudioRecord;
	private EncoderOrDecoder coder;
	byte[] buff;
	ByteBuffer inputBuffer;
	ByteBuffer outputBuffer;
	MediaCodec.BufferInfo bufferInfo;
	byte[] outData;
	boolean isRecording;
	private MediaDataQueue<byte[]> queue;
	private final AudioTrackRunnable pr;
	
	
	public AudioRecorderRunnable(EncoderOrDecoder coder, AudioRecord ar,
			AudioTrackRunnable pr, MediaDataQueue<byte[]> queue) {
		this.coder = coder;
		this.mAudioRecord = ar;
		this.queue = queue;
		this.pr = pr;
		buff = new byte[(coder.audioFormat / 8) * coder.channelCount * 1024];
	}

	@SuppressLint("NewApi")
	@Override
	public void run() {
		byte[] bytes_pkg;
		coder.initEncoder();
		coder.startEncoder();
		mAudioRecord.startRecording();
		isRecording = true;
		while (isRecording) {
			mAudioRecord.read(buff, 0, buff.length);
			bytes_pkg = buff.clone();
			// Log.i(TAG,
			// "byte.length -------> "+bytes_pkg[bytes_pkg.length-1]);

			/**************************** 编码 PCM--->AAC ****************************/
			int inputBufferIndex = coder.encoder.dequeueInputBuffer(-1);
			if (inputBufferIndex >= 0) {
				inputBuffer = coder.encoder.getInputBuffer(inputBufferIndex);
				inputBuffer.clear();
				inputBuffer.put(bytes_pkg);
				coder.encoder.queueInputBuffer(inputBufferIndex, 0,
						bytes_pkg.length, 0, 0);
			}
			bufferInfo = new MediaCodec.BufferInfo();
			int outputBufferIndex = coder.encoder.dequeueOutputBuffer(
					bufferInfo, 0);
			while (outputBufferIndex >= 0) {
				outputBuffer = coder.encoder.getOutputBuffer(outputBufferIndex);
				outputBuffer.position(bufferInfo.offset);
				outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
				outData = new byte[bufferInfo.size];
				outputBuffer.get(outData);
				Log.i(TAG, "encoder:::length -------> " + outData.length);
				queue.add(outData);// 将编码好的一帧存入队列

				outputBuffer.clear();
				coder.encoder.releaseOutputBuffer(outputBufferIndex, false);
				outputBufferIndex = coder.encoder.dequeueOutputBuffer(
						bufferInfo, 0);
			}
			/***********************************************************************/

			synchronized (pr) {
				pr.notify();
			}
		}
		mAudioRecord.stop();
		coder.stopEncoder();
		if (queue != null && queue.size() > 0)
			queue.clear();
	}

	/**
	 * 停止
	 */
	public void stop() {
		isRecording = false;
	}
}
