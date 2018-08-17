package com.yudaiyaguchi.HealthChecker.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yudaiyaguchi.HealthChecker.R;
import com.yudaiyaguchi.HealthChecker.sensor.GyroscopeActivity;

public class WelcomeActivity extends BaseActivity {
    private boolean isTTS;
    private boolean isSTT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        isTTS = settings.getBoolean("tts_switch", true);
        isSTT = settings.getBoolean("stt_switch", true);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        // this always use the default value somehow
//        boolean isChecked = sharedPref.getBoolean("tts_switch", true);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(!sharedPref.contains("seekBarPitchProgress")
                || !sharedPref.contains("seekBarSpeedProgress"))
        editor.putInt("seekBarPitchProgress", 50);
        editor.putInt("seekBarSpeedProgress", 50);
        editor.apply();


        if(isTTS) {
            Toast.makeText(getApplicationContext(), "The choice is: " +
                    " true ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "The choice is: " +
                    " false ", Toast.LENGTH_SHORT).show();
        }

        final Button databaseButton = findViewById(R.id.database);
        databaseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent databaseIntent = new Intent(WelcomeActivity.this, Questionnaire.class);
                startActivity(databaseIntent);
            }
        });

        final Button sensorButton = findViewById(R.id.sensor);
        sensorButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent sensorIntent = new Intent(WelcomeActivity.this, GyroscopeActivity.class);
                startActivity(sensorIntent);
            }
        });

        final Button speechRecButton = findViewById(R.id.speechrec);
        speechRecButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent speechRecIntent = new Intent(WelcomeActivity.this, SpeechRecActivity.class);
                startActivity(speechRecIntent);
            }
        });

        final Button TTSPracticeButton = findViewById(R.id.ttspractice);
        TTSPracticeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent TTSPracticeIntent = new Intent(WelcomeActivity.this, TTSActivity.class);
                startActivity(TTSPracticeIntent);
            }
        });

    }
}
