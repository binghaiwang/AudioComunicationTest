package com.ac.audiocomunicationtest.media.runnable;

import android.annotation.SuppressLint;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.util.Log;

import com.ac.audiocomunicationtest.media.EncoderOrDecoder;
import com.ac.audiocomunicationtest.media.MediaDataQueue;

import java.nio.ByteBuffer;

public class AudioTrackRunnable implements Runnable {

	public final static String TAG = "PlayAudioRunnable";
	ByteBuffer inputBuffer;
	ByteBuffer outputBuffer;
	MediaCodec.BufferInfo bufferInfo;
	byte[] outData;
	EncoderOrDecoder coder;
	AudioTrack mAudioTrack;
	MediaDataQueue<byte[]> queue;
	boolean isPlaying;
	boolean isFistdecode;

	public AudioTrackRunnable(EncoderOrDecoder coder, AudioTrack at,
			MediaDataQueue<byte[]> queue) {
		this.mAudioTrack = at;
		this.queue = queue;
		this.coder = coder;
	}

	@SuppressLint("NewApi")
	@Override
	public void run() {
		coder.initDecoder();
		coder.startDecoder();
		mAudioTrack.play();
		isPlaying = true;
		while (isPlaying) {
			if (queue.peek() == null) {
				toWait();
			}
			try {
				if (queue.peek() != null) {
					// int sizeInBytes = queue.peek().length;
					byte[] bytes_pkg = queue.poll();
					/**************************** 编码 AAC--->PCM ****************************/
					int inputBufferIndex = coder.decoder.dequeueInputBuffer(-1);
					if (inputBufferIndex >= 0) {
						inputBuffer = coder.decoder
								.getInputBuffer(inputBufferIndex);
						inputBuffer.clear();
						inputBuffer.put(bytes_pkg);
						if (isFistdecode) {
							isFistdecode = false;
							coder.decoder.queueInputBuffer(inputBufferIndex, 0,
									bytes_pkg.length, 0,
									MediaCodec.BUFFER_FLAG_CODEC_CONFIG);
						} else {
							coder.decoder.queueInputBuffer(inputBufferIndex, 0,
									bytes_pkg.length, 0, 0);
						}
					}
					bufferInfo = new MediaCodec.BufferInfo();
					int outputBufferIndex = coder.decoder.dequeueOutputBuffer(
							bufferInfo, 0);
					while (outputBufferIndex >= 0) {
						outputBuffer = coder.decoder
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
						coder.decoder.releaseOutputBuffer(outputBufferIndex,
								false);
						outputBufferIndex = coder.decoder.dequeueOutputBuffer(
								bufferInfo, 0);
					}
					/***********************************************************************/

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mAudioTrack.stop();
		coder.stopDecoder();
		if (queue != null && queue.size() > 0)
			queue.clear();
	}

	public void toWait() {
		synchronized (AudioTrackRunnable.class) {
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
