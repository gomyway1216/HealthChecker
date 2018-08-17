package com.yudaiyaguchi.HealthChecker.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yudaiyaguchi.HealthChecker.R;
import com.yudaiyaguchi.HealthChecker.sensor.GyroscopeActivity;

/**
 * This is the main Activity that user can choose what they want to do.
 */
public class WelcomeActivity extends BaseActivity {
    private boolean isTTS;
    private boolean isSTT;

    /**
     *
     * @param savedInstanceState : contains setting data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        isTTS = settings.getBoolean("tts_switch", true);
        isSTT = settings.getBoolean("stt_switch", true);

//        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
//        // this always use the default value somehow
////        boolean isChecked = sharedPref.getBoolean("tts_switch", true);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        if(!sharedPref.contains("seekBarPitchProgress")
//                || !sharedPref.contains("seekBarSpeedProgress"))
//        editor.putInt("seekBarPitchProgress", 50);
//        editor.putInt("seekBarSpeedProgress", 50);
//        editor.apply();

        final Button databaseButton = findViewById(R.id.database);
        databaseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent databaseIntent = new Intent(WelcomeActivity.this, Questionnaire.class);
                startActivity(databaseIntent);
            }
        });

        final Button sensorButton = findViewById(R.id.sensor);
        sensorButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent sensorIntent = new Intent(WelcomeActivity.this, GyroscopeActivity.class);
                startActivity(sensorIntent);
            }
        });

        final Button speechRecButton = findViewById(R.id.speechrec);
        speechRecButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent speechRecIntent = new Intent(WelcomeActivity.this, SpeechRecActivity.class);
                startActivity(speechRecIntent);
            }
        });

        final Button TTSPracticeButton = findViewById(R.id.ttspractice);
        TTSPracticeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent TTSPracticeIntent = new Intent(WelcomeActivity.this, TTSActivity.class);
                startActivity(TTSPracticeIntent);
            }
        });

    }
}
