package com.example.shopapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopapp.databinding.FragmentSensorsBinding;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SensorsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SensorsFragment extends Fragment implements SensorEventListener {
    private SensorManager sensorManager;
    private TextView tvAccelerometer;
    private TextView tvLinearAccelerometer;
    private TextView tvMagneticField;
    private TextView tvProximitySensor;
    private TextView tvGyroscope;
    private static final int SHAKE_THRESHOLD = 800;
    private long lastUpdate;
    private float last_x;
    private float last_y;
    private float last_z;
    private FragmentSensorsBinding fragmentSensorsBinding;

    public SensorsFragment() {
        // Required empty public constructor
    }

    public static SensorsFragment newInstance() {
        return new SensorsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentSensorsBinding = FragmentSensorsBinding.inflate(inflater, container, false);
        return fragmentSensorsBinding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);

        tvAccelerometer = fragmentSensorsBinding.tvAccelerometar;
        tvLinearAccelerometer = fragmentSensorsBinding.tvLinearAccelerometer;
        tvMagneticField = fragmentSensorsBinding.tvMagneticField;
        tvProximitySensor = fragmentSensorsBinding.tvProximitySensor;
        tvGyroscope = fragmentSensorsBinding.tvGyroscope;
    }


    @Override
    public void onResume() {
        super.onResume();
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for(Sensor s : sensors){
            Log.i("REZ_TYPE_ALL", s.getName());
        }

        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);

        Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (proximitySensor == null) {
            Toast.makeText(requireContext(), "No proximity sensor found in device.", Toast.LENGTH_SHORT).show();
        } else {
            // registering our sensor with sensor manager
            sensorManager.registerListener(this,
                    proximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float[] values = sensorEvent.values;
                float x = values[0];
                float y = values[1];
                float z = values[2];

                float speed = Math.abs(x+y+z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    Log.d("REZ", "shake detected w/ speed: " + speed);
                    tvAccelerometer.setText("Accelerometer: shaking \n [" + x + ", " + y + ", " + z + "]");
                }else{
                    tvAccelerometer.setText("Accelerometer: not shaking \n [" + x + ", " + y + ", " + z + "]");
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }

        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float[] values = sensorEvent.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            tvLinearAccelerometer.setText("Non-gravity accelerometer: [" + x + ", " + y + ", " + z + "]");
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float[] values = sensorEvent.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            tvMagneticField.setText("Magnetic field: [" + x + ", " + y + ", " + z + "]");
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float[] values = sensorEvent.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            tvGyroscope.setText("Gyroscpe : [" + x + ", " + y + ", " + z + "]");
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float[] values = sensorEvent.values;
            float x = values[0];
            if (x == 0) {
                // here we are setting our status to our textview..
                // if sensor event return 0 then object is closed
                // to sensor else object is away from sensor.
                tvProximitySensor.setText("Proximity sensor: Blizu");
            } else {
                tvProximitySensor.setText("Proximity sensor: Daleko");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            Log.i("REZ_ACCELEROMETER", String.valueOf(accuracy));
        }else if(sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            Log.i("REZ_LINEAR_ACCELERATION", String.valueOf(accuracy));
        }else if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            Log.i("REZ_MAGNETIC_FIELD", String.valueOf(accuracy));
        }else if(sensor.getType() == Sensor.TYPE_GYROSCOPE){
            Log.i("REZ_GYROSCOPE", String.valueOf(accuracy));
        }else if(sensor.getType() == Sensor.TYPE_PROXIMITY){
            Log.i("REZ_TYPE_PROXIMITY", String.valueOf(accuracy));
        }else{
            Log.i("REZ_OTHER_SENSOR", String.valueOf(accuracy));
        }
    }
}