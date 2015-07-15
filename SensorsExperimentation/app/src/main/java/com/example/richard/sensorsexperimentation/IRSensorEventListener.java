package com.example.richard.sensorsexperimentation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.widget.TextView;

public class IRSensorEventListener implements SensorEventListener {
    private static final String TAG = "IRSensorEventListener";

    private Context mContext;
    private TonePlayer mTonePlayer;

    private double MIN_TOP = 400.;
    private double MAX_TOP = 4060.;
    private double TOP_RANGE = MAX_TOP - MIN_TOP;
    private double MIN_BOTTOM = 500.;
    private double MAX_BOTTOM = 3200.;
    private double BOTTOM_RANGE = MAX_BOTTOM - MIN_BOTTOM;

    private double MIN_PITCH = 55.;
    private double MAX_PITCH = 880.;
    private double PITCH_RANGE = MAX_PITCH - MIN_PITCH;

    public IRSensorEventListener(Context c) {
        mContext = c;
        mTonePlayer = new TonePlayer();
    }

    public void changeWaveForm(TonePlayer.WAVE_FORM newWaveForm) {
        mTonePlayer.changeWaveForm(newWaveForm);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // event.values[0] -> top, large range
        // event.values[1] -> bottom left, large range
        // event.values[2] -> bottom right, large range
        // event.values[3] -> bottom both, large range
        // event.values[4] -> top, smaller range
        // event.values[5] -> bottom left, smaller range
        // event.values[6] -> bottom right, smaller range
        // event.values[7] -> bottom both, smaller range
        // event.values[8] -> ambient
        // event.values[9] -> ambient
        // mDataView.setText("" + (int)event.values[3]);
        double pitch = event.values[0];
        double volume = event.values[3];
        double pitchFraction = 1 - Math.sqrt(Math.sqrt(Math.max(0, Math.min(1, (pitch - MIN_TOP) / TOP_RANGE))));
        double volumeFraction = Math.max(0, Math.min(1, (volume - MIN_BOTTOM) / BOTTOM_RANGE));
        double hertz = MIN_PITCH + pitchFraction * PITCH_RANGE;

        mTonePlayer.play(hertz, volumeFraction);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
