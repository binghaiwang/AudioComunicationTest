package com.ac.audiocomunicationtest.media;

import java.io.IOException;

import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

@SuppressWarnings("deprecation")
public class EncoderOrDecoder {
	public MediaCodec encoder;
	public MediaCodec decoder;
	public int audioFormat;
	public int channelCount;
	
	public EncoderOrDecoder(){
		switch (Const.AUDIO_FORMAT) {
		case AudioFormat.ENCODING_PCM_16BIT:
			audioFormat = 16;
			break;
		case AudioFormat.ENCODING_PCM_8BIT:
			audioFormat = 8;
			break;
		}
		switch (Const.AUDIO_RECORD_CHANNEL_CONFIG) {
		case AudioFormat.CHANNEL_IN_DEFAULT: // AudioFormat.CHANNEL_CONFIGURATION_DEFAULT
		case AudioFormat.CHANNEL_IN_MONO:
		case AudioFormat.CHANNEL_CONFIGURATION_MONO:
			channelCount = 1;
			break;
		case AudioFormat.CHANNEL_IN_STEREO:
		case AudioFormat.CHANNEL_CONFIGURATION_STEREO:
		case (AudioFormat.CHANNEL_IN_FRONT | AudioFormat.CHANNEL_IN_BACK):
			channelCount = 2;
			break;
		default:
			break;
		}
	}
	public void initEncoder(){
		try {
			encoder = MediaCodec.createEncoderByType("audio/mp4a-latm");
			MediaFormat mediaFormat = new MediaFormat();
			mediaFormat.setString("mime", "audio/mp4a-latm");
			mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 2);
			mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE,
					Const.SAMPLE_RATE_IN_HZ);
			mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 64 * 1024);// AAC-HE
																		// //
																		// 64kbps
			mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE,
					MediaCodecInfo.CodecProfileLevel.AACObjectLC);// AACObjectLC
			encoder.configure(mediaFormat, null, null,
					MediaCodec.CONFIGURE_FLAG_ENCODE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//05-14 10:56:17.700: I/MediaCodec(30938): (0xb8b5ba30) Component Allocated (OMX.google.aac.decoder)

	
	public void initDecoder(){
		try {
			decoder = MediaCodec.createDecoderByType("audio/mp4a-latm");
			MediaFormat format = new MediaFormat();
			format.setString(MediaFormat.KEY_MIME, "audio/mp4a-latm");
			format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 2);
			format.setInteger(MediaFormat.KEY_SAMPLE_RATE,Const.SAMPLE_RATE_IN_HZ);
			format.setInteger(MediaFormat.KEY_BIT_RATE, 64 * 1024);// AAC-HE
																	// 64kbps
			format.setInteger(MediaFormat.KEY_AAC_PROFILE,
					MediaCodecInfo.CodecProfileLevel.AACObjectHE);

			decoder.configure(format, null, null, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startEncoder() {
		encoder.start();
	}
	
	public void stopEncoder() {
		encoder.stop();
	}
	
	public void startDecoder() {
		decoder.start();
	}
	
	public void stopDecoder() {
		decoder.stop();
	}
}
