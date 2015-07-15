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

    public enum WAVE_FORM {
        SINE, SAWTOOTH, SQUARE, TRIANGLE
    }

    private final int SAMPLE_RATE = 8000;
    private final float DURATION = 0.1f;
    private final int NUM_SAMPLES = (int)(Math.ceil(DURATION * SAMPLE_RATE));
    private final int RAMP = NUM_SAMPLES / 20;

    private WAVE_FORM mWaveForm = WAVE_FORM.SINE;

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
            //sample[i] = Math.sin(hz * 2 * Math.PI * i / (SAMPLE_RATE)) * volume;
            switch (mWaveForm) {
                case SINE:
                    sample[i] = sine(hz * i / (SAMPLE_RATE)) * volume;
                    break;
                case SAWTOOTH:
                    sample[i] = sawtooth(hz * i / (SAMPLE_RATE)) * volume;
                    break;
                case SQUARE:
                    sample[i] = square(hz * i / (SAMPLE_RATE)) * volume;
                    break;
                case TRIANGLE:
                    sample[i] = triangle(hz * i / (SAMPLE_RATE)) * volume;
                    break;
            }
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

    public void changeWaveForm(WAVE_FORM newWaveForm) {
        mWaveForm = newWaveForm;
    }

    private double sine(double x) {
        return Math.sin(x * 2 * Math.PI);
    }

    private double sawtooth(double x) {
        x -= (int)x;
        if (x <= 0.5) {
            return 2 * x;
        } else {
            return 2 * x - 2;
        }
    }

    private double square(double x) {
        x -= (int)x;
        return x <= 0.5 ? 1 : -1;
    }

    private double triangle(double x) {
        x -= (int)x;
        if (x <= 0.25) {
            return 4 * x;
        } else if (x <= 0.75) {
            return -4 * x + 2;
        } else {
            return 4 * x - 4;
        }
    }
}
