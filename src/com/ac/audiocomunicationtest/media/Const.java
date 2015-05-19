package com.ac.audiocomunicationtest.media;

import android.media.AudioFormat;

public interface Const {
	int AUDIO_RECORD_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
	int AUDIO_TRACK_CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_STEREO;
	int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
	int SAMPLE_RATE_IN_HZ = 44100;
}
