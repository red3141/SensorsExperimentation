package com.example.richard.sensorsexperimentation;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Based on code by users posting on
 * http://stackoverflow.com/questions/2413426/playing-an-arbitrary-tone-with-android
 */
public class TonePlayer {
    private static final String TAG = "TONE_PLAYER";

    private final int SAMPLE_RATE = 8000;
    private final float DURATION = 0.1f;
    private final int NUM_SAMPLES = (int)(Math.ceil(DURATION * SAMPLE_RATE));
    private final int RAMP = NUM_SAMPLES / 20;

    private AudioTrack mAudioTrack;

    public TonePlayer() {
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, NUM_SAMPLES*2,
                AudioTrack.MODE_STREAM);
    }

    public void play(double hz, double volume) {
        if (hz <= 0 || volume > 1.0 || volume < 0.0) {
            Log.e(TAG, "Invalid parameter");
        }

        double sample[] = new double[NUM_SAMPLES];

        byte generatedSound[] = new byte[2 * NUM_SAMPLES];

        for (int i = 0; i < NUM_SAMPLES; ++i) {
            sample[i] = Math.sin(hz * 2 * Math.PI * i / (SAMPLE_RATE)) * volume;
            //if (sample[i] > 0) sample[i] = volume;
            //else sample[i] = -volume;
        }

        // convert to 16 bit pcm sound array.
        int idx = 0;
        int i = 0;

        // Ramp up amplitude to avoid clicks.
        for (; i < RAMP; ++i) {
            double dVal= sample[i];

            final short val = (short) (dVal * 32767 * i/RAMP);

            // in 16 bit wav PCM, first byte is the low order byte
            generatedSound[idx++] = (byte) (val & 0x00ff);
            generatedSound[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        for (; i < NUM_SAMPLES - RAMP; ++i) {
            double dVal = sample[i];

            final short val = (short) (dVal * 32767);

            generatedSound[idx++] = (byte) (val & 0x00ff);
            generatedSound[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        // Ramp down amplitude to avoid clicks.
        for (; i < NUM_SAMPLES; ++i) {
            double dVal = sample[i];

            final short val = (short) (dVal * 32767 * (NUM_SAMPLES - i) / RAMP);

            generatedSound[idx++] = (byte) (val & 0x00ff);
            generatedSound[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        mAudioTrack.stop();
        try {
            mAudioTrack.write(generatedSound, 0, generatedSound.length);
            mAudioTrack.play();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
}
