package com.ac.audiocomunicationtest.media.runnable;

import android.annotation.SuppressLint;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.util.Log;

import com.ac.audiocomunicationtest.media.EncoderOrDecoder;
import com.ac.audiocomunicationtest.media.MediaDataQueue;

import java.nio.ByteBuffer;

public class AudioTrackRunnable implements Runnable {

	public final static String TAG = "AudioTrackRunnable";
	ByteBuffer inputBuffer;
	ByteBuffer outputBuffer;
	MediaCodec.BufferInfo bufferInfo;
	byte[] outData;
	private EncoderOrDecoder mCoder;
	private AudioTrack mAudioTrack;
	private MediaDataQueue<byte[]> mQueue;
	boolean isPlaying;
	boolean isFistdecode;

	public AudioTrackRunnable(EncoderOrDecoder coder, AudioTrack at,
			MediaDataQueue<byte[]> queue) {
		this.mAudioTrack = at;
		this.mQueue = queue;
		this.mCoder = coder;
	}

	@SuppressLint("NewApi")
	@Override
	public void run() {
		mCoder.initDecoder();
		mCoder.startDecoder();
		mAudioTrack.play();
		isPlaying = true;
		while (isPlaying) {
			if (mQueue.peek() == null) {
				toWait();
			}
			try {
				if (mQueue.peek() != null) {
					// int sizeInBytes = mQueue.peek().length;
					byte[] bytes_pkg = mQueue.poll();
					/**************************** 编码 AAC--->PCM ****************************/
					int inputBufferIndex = mCoder.decoder.dequeueInputBuffer(-1);
					if (inputBufferIndex >= 0) {
						inputBuffer = mCoder.decoder
								.getInputBuffer(inputBufferIndex);
						inputBuffer.clear();
						inputBuffer.put(bytes_pkg);
						if (isFistdecode) {
							isFistdecode = false;
							mCoder.decoder.queueInputBuffer(inputBufferIndex, 0,
									bytes_pkg.length, 0,
									MediaCodec.BUFFER_FLAG_CODEC_CONFIG);
						} else {
							mCoder.decoder.queueInputBuffer(inputBufferIndex, 0,
									bytes_pkg.length, 0, 0);
						}
					}
					bufferInfo = new MediaCodec.BufferInfo();
					int outputBufferIndex = mCoder.decoder.dequeueOutputBuffer(
							bufferInfo, 0);
					while (outputBufferIndex >= 0) {
						outputBuffer = mCoder.decoder
								.getOutputBuffer(outputBufferIndex);
						outputBuffer.position(bufferInfo.offset);
						outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
						outData = new byte[bufferInfo.size];
						outputBuffer.get(outData);
						Log.i(TAG, "decoder:::length -------> "
								+ outData.length);
						mAudioTrack.write(outData, 0, outData.length);
						// 将编码完成的进行播放

						outputBuffer.clear();
						mCoder.decoder.releaseOutputBuffer(outputBufferIndex,
								false);
						outputBufferIndex = mCoder.decoder.dequeueOutputBuffer(
								bufferInfo, 0);
					}
					/***********************************************************************/

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mAudioTrack.stop();
		mCoder.stopDecoder();
		if (mQueue != null && mQueue.size() > 0)
			mQueue.clear();
	}

	public void toWait() {
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				// do nothing
			}
		}
	}

	public void stop() {
		isPlaying = false;
		isFistdecode = true;
	}
}
