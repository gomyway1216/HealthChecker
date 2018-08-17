package com.yudaiyaguchi.HealthChecker.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import java.util.Locale;

import com.yudaiyaguchi.HealthChecker.R;

/**
 *  User can play around with TTS
 */
public class TTSActivity extends AppCompatActivity {
    private TextToSpeech mTTS;
    private EditText mEditText;
    private SeekBar mSeekBarPitch;
    private SeekBar mSeekBarSpeed;
    private int mSeekBarPitchProgress;
    private int mSeekBarSpeedProgress;
    private Button mButtonSpeak;
    private Button mDefault;
    private Button mOk;
    private float pitch;
    private float speed;

    /**
     * initialize TTS
     * @param savedInstanceState : saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts);

        mButtonSpeak = findViewById(R.id.button_speak);
        mDefault = findViewById(R.id.button_back_to_default);
        mOk = findViewById(R.id.button_ok);

        // set TTS
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.getDefault());

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        mButtonSpeak.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        mEditText = findViewById(R.id.edit_text);
        mSeekBarPitch = findViewById(R.id.seek_bar_pitch);
        mSeekBarSpeed = findViewById(R.id.seek_bar_speed);

        // if saved data doesn't exist
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        if(sharedPref.contains("seekBarPitchProgress")
                && sharedPref.contains("seekBarSpeedProgress")) {
            mSeekBarPitchProgress = sharedPref.getInt("seekBarPitchProgress", 0);
            mSeekBarSpeedProgress = sharedPref.getInt("seekBarSpeedProgress", 0);
        } else {
            mSeekBarPitchProgress = mSeekBarPitch.getMax()/2;
            mSeekBarSpeedProgress = mSeekBarSpeed.getMax()/2;
        }
        mSeekBarPitch.setProgress(mSeekBarPitchProgress);
        mSeekBarSpeed.setProgress(mSeekBarSpeedProgress);


        mButtonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

        mDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSeekBarPitchProgress = mSeekBarPitch.getMax()/ 2;
                mSeekBarSpeedProgress = mSeekBarSpeed.getMax()/ 2;
                mSeekBarPitch.setProgress(mSeekBarPitchProgress);
                mSeekBarSpeed.setProgress(mSeekBarSpeedProgress);
            }
        });

        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set the value to sharedPreference
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                Log.d("mSeekBarPitchProgress", "  " + mSeekBarPitchProgress);
                Log.d("mSeekBarSpeedProgress", "   " + mSeekBarSpeedProgress);
                editor.putInt("seekBarPitchProgress", mSeekBarPitch.getProgress());
                editor.putInt("seekBarSpeedProgress", mSeekBarSpeed.getProgress());
                editor.apply();
                finish();
            }
        });

    }

    /**
     * speak method. speak is async. Be careful when you are using it.
     */
    private void speak() {
        String text = mEditText.getText().toString();
        pitch = (float) mSeekBarPitch.getProgress() / 50;
        if (pitch < 0.1) pitch = 0.1f;
        speed = (float) mSeekBarSpeed.getProgress() / 50;
        if (speed < 0.1) speed = 0.1f;

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTTS.speak(text,TextToSpeech.QUEUE_FLUSH,null,null);
        } else {
            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    /**
     * This method might not be called.
     */
    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }
}