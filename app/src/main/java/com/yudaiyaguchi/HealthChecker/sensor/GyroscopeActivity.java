package com.yudaiyaguchi.HealthChecker.sensor;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.kircherelectronics.fsensor.filter.averaging.MeanFilter;
import com.kircherelectronics.fsensor.filter.gyroscope.OrientationGyroscope;
import com.kircherelectronics.fsensor.filter.gyroscope.fusion.complimentary.OrientationFusedComplimentary;
import com.kircherelectronics.fsensor.filter.gyroscope.fusion.kalman.OrientationFusedKalman;
import com.kircherelectronics.fsensor.util.rotation.RotationUtil;
import com.yudaiyaguchi.HealthChecker.R;
import com.yudaiyaguchi.HealthChecker.activity.ConfigActivity;
import com.yudaiyaguchi.HealthChecker.datalogger.DataLoggerManager;
import com.yudaiyaguchi.HealthChecker.gauge.GaugeBearing;
import com.yudaiyaguchi.HealthChecker.gauge.GaugeRotation;
import com.yudaiyaguchi.HealthChecker.view.VectorDrawableButton;

import org.apache.commons.math3.complex.Quaternion;

/*
 * Copyright 2013-2017, Kaleb Kircher - Kircher Engineering, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * The main activity displays the orientation estimated by the sensor(s) and
 * provides an interface for the user to modify settings, reset or view help.
 *
 * @author Kaleb
 */
public class GyroscopeActivity extends AppCompatActivity implements SensorEventListener {
    private static final int MY_PERMISSIONS_NETWORK_PROVIDER = 1;
    private static final String tag = GyroscopeActivity.class.getSimpleName();

    private final static int WRITE_EXTERNAL_STORAGE_REQUEST = 1000;

    // Indicate if the output should be logged to a .csv file
    private boolean logData = false;

    private boolean hasAcceleration = false;
    private boolean hasMagnetic = false;

    private boolean meanFilterEnabled;
    private boolean kalmanFilterEnabled;
    private boolean complimentaryFilterEnabled;

    private float[] fusedOrientation = new float[3];
    private float[] acceleration = new float[4];
    private float[] magnetic = new float[3];
    private float[] rotation = new float[3];
    private float[] latilong = new float[2];

    private Mode mode = Mode.GYROSCOPE_ONLY;

    // The gauge views. Note that these are views and UI hogs since they run in
    // the UI thread, not ideal, but easy to use.
    private GaugeBearing gaugeBearingCalibrated;
    private GaugeRotation gaugeTiltCalibrated;

    // Handler for the UI plots so everything plots smoothly
    protected Handler handler;

    protected Runnable runable;

//    private TextView tvXAxis;
//    private TextView tvYAxis;
//    private TextView tvZAxis;

    private TextView roXAxis;
    private TextView roYAxis;
    private TextView roZAxis;


    private TextView acXAxis;
    private TextView acYAxis;
    private TextView acZAxis;

    private TextView mgXAxis;
    private TextView mgYAxis;
    private TextView mgZAxis;

    private TextView latitude;
    private TextView longitude;
    private LocationManager locationManager;
    private LocationListener locationListener;




    private OrientationGyroscope orientationGyroscope;
    private OrientationFusedComplimentary orientationComplimentaryFusion;
    private OrientationFusedKalman orientationKalmanFusion;

    private MeanFilter meanFilter;

    private SensorManager sensorManager;

    private DataLoggerManager dataLogger;

    //////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gyroscope);
        dataLogger = new DataLoggerManager(this);
        meanFilter = new MeanFilter();
        // # can do this because it is in the Activity, otherwise we have to pass context
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        initUI();


        // related to location searvice
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
//                makeUseOfNewLocation(location);
//                t.append("\n " + location.getLongitude() + " " + location.getLatitude());


                Log.d("location", location.getLatitude() + "   :    "  + location.getLongitude());
                latilong[0] = (float) location.getLatitude();
                latilong[1] = (float) location.getLongitude();


                dataLogger.setLatiLong(latilong);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        getLocation();

    }


    // # I am not sure whether I need this or not.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_NETWORK_PROVIDER:
                getLocation();
                break;
            default:
                break;
        }
    }

    public void getLocation() {
// Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        } else {
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, locationListener);
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, locationListener);
        }
    }


    public void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to use GPS")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(GyroscopeActivity.this,
                                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_NETWORK_PROVIDER);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_NETWORK_PROVIDER);


        }
    }


    ///////#############
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gyroscope, menu);
        return true;
    }


    ///////#############
    /**
     * Event Handling for Individual menu item selected Identify single menu
     * item by it's id
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Reset everything
            case R.id.action_reset:
                if(orientationGyroscope != null) {
                    orientationGyroscope.reset();
                }
                return true;

            // Reset everything
            case R.id.action_config:
                Intent intent = new Intent();
                intent.setClass(this, ConfigActivity.class);
                startActivity(intent);
                return true;

            // Reset everything
            case R.id.action_help:
                showHelpDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //////////////////////////
    @Override
    public void onResume() {
        super.onResume();

        requestPermissions();
        readPrefs();

        switch (mode) {
            case GYROSCOPE_ONLY:
                orientationGyroscope = new OrientationGyroscope();
                break;
            case COMPLIMENTARY_FILTER:
                orientationComplimentaryFusion = new OrientationFusedComplimentary();
                break;
            case KALMAN_FILTER:
                orientationKalmanFusion = new OrientationFusedKalman();
                break;
        }

        reset();

        if(orientationKalmanFusion != null) {
            orientationKalmanFusion.startFusion();
        }

        sensorManager.registerListener(this, sensorManager
                        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

        // Register for sensor updates.
        sensorManager.registerListener(this, sensorManager
                        .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);

        // Register for sensor updates.
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST);

        handler.post(runable);
    }


    /////////////////////////////
    @Override
    public void onPause() {
        super.onPause();

        if(orientationKalmanFusion != null) {
            orientationKalmanFusion.stopFusion();
        }

        sensorManager.unregisterListener(this);
        handler.removeCallbacks(runable);
    }


    //////////////////////////////
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Android reuses events, so you probably want a copy
            System.arraycopy(event.values, 0, acceleration, 0, event.values.length);
            hasAcceleration = true;
            dataLogger.setAcceleration(acceleration);
        } else  if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            // Android reuses events, so you probably want a copy
            System.arraycopy(event.values, 0, magnetic, 0, event.values.length);
            hasMagnetic = true;
            dataLogger.setMagnetic(magnetic);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // Android reuses events, so you probably want a copy
            System.arraycopy(event.values, 0, rotation, 0, event.values.length);

                Log.d("myTag3", "value0-x: " + rotation[0]);
                Log.d("myTag3", "value0-y: " + rotation[1]);
                Log.d("myTag3", "value0-z: " + rotation[2]);

            dataLogger.setRotation(rotation);

            float[] rotationTemp = new float[3];
            System.arraycopy(rotation, 0, rotationTemp, 0, rotation.length);
            // # I guess the filter is only for Gyroscope
            switch (mode) {
                case GYROSCOPE_ONLY:
                    if(!orientationGyroscope.isBaseOrientationSet()) {
                        orientationGyroscope.setBaseOrientation(Quaternion.IDENTITY);
                    } else {
                        fusedOrientation = orientationGyroscope.calculateOrientation(rotationTemp, event.timestamp);
                    }

                    break;
                case COMPLIMENTARY_FILTER:
                    if(!orientationComplimentaryFusion.isBaseOrientationSet()) {
                        if(hasAcceleration && hasMagnetic) {
                            orientationComplimentaryFusion.setBaseOrientation(RotationUtil.getOrientationQuaternionFromAccelerationMagnetic(acceleration, magnetic));
                        }
                    } else {
                        fusedOrientation = orientationComplimentaryFusion.calculateFusedOrientation(rotationTemp, event.timestamp, acceleration, magnetic);
                    }

                    break;
                case KALMAN_FILTER:
                    if(!orientationKalmanFusion.isBaseOrientationSet()) {
                        if(hasAcceleration && hasMagnetic) {
                            orientationKalmanFusion.setBaseOrientation(RotationUtil.getOrientationQuaternionFromAccelerationMagnetic(acceleration, magnetic));
                        }
                    } else {
                        fusedOrientation = orientationKalmanFusion.calculateFusedOrientation(rotationTemp, event.timestamp, acceleration, magnetic);
                    }
                    break;
            }

            if(meanFilterEnabled) {
                fusedOrientation = meanFilter.filter(fusedOrientation);
            }

//            for(int i = 0; i < rotation.length; i++)
//                Log.d("myTag3", "value2: " + rotation[i]);
//
//            Log.d("myTag3", "value1-x: " + rotation[0]);
//            Log.d("myTag3", "value1-y: " + rotation[1]);
//            Log.d("myTag3", "value1-z: " + rotation[2]);


//            dataLogger.setRotation(rotation);
//            dataLogger.setOrientation(fusedOrientation);

        }
    }

    //////////////////////
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}


    //////////////////////
    private boolean getPrefMeanFilterEnabled() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        return prefs.getBoolean(ConfigActivity.MEAN_FILTER_SMOOTHING_ENABLED_KEY,
                false);
    }

    //////////////////////
    private float getPrefMeanFilterTimeConstant() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        return Float.valueOf(prefs.getString(ConfigActivity.MEAN_FILTER_SMOOTHING_TIME_CONSTANT_KEY, "0.5"));
    }

    //////////////////////
    private boolean getPrefKalmanEnabled() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        return prefs.getBoolean(ConfigActivity.KALMAN_QUATERNION_ENABLED_KEY,
                false);
    }

    //////////////////////
    private boolean getPrefComplimentaryEnabled() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        return prefs.getBoolean(ConfigActivity.COMPLIMENTARY_QUATERNION_ENABLED_KEY,
                false);
    }

    //////////////////////
    private float getPrefImuOCfQuaternionCoeff() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        return Float.valueOf(prefs.getString(
                ConfigActivity.COMPLIMENTARY_QUATERNION_COEFF_KEY, "0.5"));
    }

    //////////////////////
    private void initStartButton() {
        final VectorDrawableButton button = findViewById(R.id.button_start);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!logData) {
                    button.setText("Stop Log");
                    startDataLog();
                } else {
                    button.setText("Start Log");
                    stopDataLog();
                }
            }
        });
    }

    /**
     * Initialize the UI.
     */
    // //////////////////////////
    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Initialize the calibrated text views
//        tvXAxis = this.findViewById(R.id.value_x_axis_calibrated);
//        tvYAxis = this.findViewById(R.id.value_y_axis_calibrated);
//        tvZAxis = this.findViewById(R.id.value_z_axis_calibrated);

        roXAxis = this.findViewById(R.id.value_x_axis_calibrated_ro);
        roYAxis = this.findViewById(R.id.value_y_axis_calibrated_ro);
        roZAxis = this.findViewById(R.id.value_z_axis_calibrated_ro);


        acXAxis = this.findViewById(R.id.value_x_axis_calibrated_ac);
        acYAxis = this.findViewById(R.id.value_y_axis_calibrated_ac);
        acZAxis = this.findViewById(R.id.value_z_axis_calibrated_ac);

        mgXAxis = this.findViewById(R.id.value_x_axis_calibrated_mg);
        mgYAxis = this.findViewById(R.id.value_y_axis_calibrated_mg);
        mgZAxis = this.findViewById(R.id.value_z_axis_calibrated_mg);



        latitude = this.findViewById(R.id.value_latitude_location);
        longitude = this.findViewById(R.id.value_longitude_location);




        // Initialize the calibrated gauges views
        gaugeBearingCalibrated = findViewById(R.id.gauge_bearing_calibrated);
        gaugeTiltCalibrated = findViewById(R.id.gauge_tilt_calibrated);

        initStartButton();
    }


    ////////////////////
    private void reset() {

        switch (mode) {
            case GYROSCOPE_ONLY:
                orientationGyroscope.reset();
                break;
            case COMPLIMENTARY_FILTER:
                orientationComplimentaryFusion.reset();
                break;
            case KALMAN_FILTER:
                orientationKalmanFusion.reset();
                break;
        }


        rotation = new float[3];
        acceleration = new float[4];
        magnetic = new float[3];
        latilong = new float[2];

        hasAcceleration = false;
        hasMagnetic = false;

        handler = new Handler();

        runable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 100);
                updateText();
                updateGauges();
            }
        };
    }

    ////////////////////////
    private void readPrefs() {
        meanFilterEnabled = getPrefMeanFilterEnabled();
        complimentaryFilterEnabled = getPrefComplimentaryEnabled();
        kalmanFilterEnabled = getPrefKalmanEnabled();

        if(meanFilterEnabled) {
            meanFilter.setTimeConstant(getPrefMeanFilterTimeConstant());
        }

        if(!complimentaryFilterEnabled && !kalmanFilterEnabled) {
            mode = Mode.GYROSCOPE_ONLY;
        } else if(complimentaryFilterEnabled) {
            mode = Mode.COMPLIMENTARY_FILTER;
        } else if(kalmanFilterEnabled) {
            mode = Mode.KALMAN_FILTER;
        }
    }

    ///////////////////////
    private void showHelpDialog() {
        Dialog helpDialog = new Dialog(this);

        helpDialog.setCancelable(true);
        helpDialog.setCanceledOnTouchOutside(true);
        helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = getLayoutInflater()
                .inflate(R.layout.layout_help_home, null);

        helpDialog.setContentView(view);

        helpDialog.show();
    }

    /////////////////////////
    private void startDataLog() {
        // # use global boolean value
        logData = true;
        dataLogger.startDataLog();
    }

    //////////////////////
    private void stopDataLog() {
        logData = false;
        String path = dataLogger.stopDataLog();
        Toast.makeText(this, "File Written to: " + path, Toast.LENGTH_SHORT).show();
    }

    //////////////////////
    private void updateText() {
//        tvXAxis.setText(String.format("%.2f", (Math.toDegrees(fusedOrientation[0]) + 360) % 360));
//        tvYAxis.setText(String.format("%.2f", (Math.toDegrees(fusedOrientation[1]) + 360) % 360));
//        tvZAxis.setText(String.format("%.2f", (Math.toDegrees(fusedOrientation[2]) + 360) % 360));

        roXAxis.setText(String.format("%.2f", rotation[0]));
        roYAxis.setText(String.format("%.2f", rotation[1]));
        roZAxis.setText(String.format("%.2f", rotation[2]));

        acXAxis.setText(String.format("%.2f", acceleration[0]));
        acYAxis.setText(String.format("%.2f", acceleration[1]));
        acZAxis.setText(String.format("%.2f", acceleration[2]));

        mgXAxis.setText(String.format("%.2f", magnetic[0]));
        mgYAxis.setText(String.format("%.2f", magnetic[1]));
        mgZAxis.setText(String.format("%.2f", magnetic[2]));

        latitude.setText(String.format("%.4f", latilong[0]));
        longitude.setText(String.format("%.4f", latilong[1]));

    }

    //////////////////////
    private void updateGauges() {
        gaugeBearingCalibrated.updateBearing(fusedOrientation[0]);
        gaugeTiltCalibrated.updateRotation(fusedOrientation);
    }

    //////////////////////
    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST);
        }
    }

    //////////////////////
    private enum Mode {
        GYROSCOPE_ONLY,
        COMPLIMENTARY_FILTER,
        KALMAN_FILTER
    }

}