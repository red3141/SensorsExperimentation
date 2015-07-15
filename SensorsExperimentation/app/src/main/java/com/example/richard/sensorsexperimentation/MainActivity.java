package com.example.richard.sensorsexperimentation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private SensorManager mSensorManager;

    private Button mStartStopButton;
    private boolean mIsRunning;

    private Sensor mIRSensor;

    private IRSensorEventListener mIRSensorEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mStartStopButton = (Button) findViewById(R.id.start_stop_button);
        mIsRunning = false;
        mStartStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsRunning) {
                    unregisterSensorListeners();
                } else {
                    registerSensorListeners();
                }
            }
        });

        RadioGroup waveFormSelector = (RadioGroup) findViewById(R.id.wave_form_selector);
        waveFormSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.sine_button:
                        mIRSensorEventListener.changeWaveForm(TonePlayer.WAVE_FORM.SINE);
                        break;
                    case R.id.sawtooth_button:
                        mIRSensorEventListener.changeWaveForm(TonePlayer.WAVE_FORM.SAWTOOTH);
                        break;
                    case R.id.square_button:
                        mIRSensorEventListener.changeWaveForm(TonePlayer.WAVE_FORM.SQUARE);
                        break;
                    case R.id.triangle_button:
                        mIRSensorEventListener.changeWaveForm(TonePlayer.WAVE_FORM.TRIANGLE);
                        break;
                }
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mIRSensorEventListener = new IRSensorEventListener(this);

        Log.d(TAG, mSensorManager.getSensorList(Sensor.TYPE_ALL).toString());

        for (Sensor sensor : mSensorManager.getSensorList(Sensor.TYPE_ALL)) {
            if (sensor.getName().equals("IR Raw Data")) {
                Log.d(TAG, sensor.toString());
                mIRSensor = sensor;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterSensorListeners();
        super.onPause();
    }

    private void registerSensorListeners() {
        mIsRunning = true;
        mStartStopButton.setText(R.string.stop);
        mSensorManager.registerListener(mIRSensorEventListener, mIRSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterSensorListeners() {
        mIsRunning = false;
        mStartStopButton.setText(R.string.start);
        mSensorManager.unregisterListener(mIRSensorEventListener);
    }
}
